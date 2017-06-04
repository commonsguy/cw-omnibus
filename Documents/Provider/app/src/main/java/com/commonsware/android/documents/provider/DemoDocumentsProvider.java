/***
 Copyright (c) 2014 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.documents.provider;

import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract.Document;
import android.provider.DocumentsContract.Root;
import android.provider.DocumentsProvider;
import android.util.Log;
import android.webkit.MimeTypeMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DemoDocumentsProvider extends DocumentsProvider {
  private static final String[] SUPPORTED_ROOT_PROJECTION=new String[] {
      Root.COLUMN_ROOT_ID, Root.COLUMN_FLAGS, Root.COLUMN_TITLE,
      Root.COLUMN_DOCUMENT_ID, Root.COLUMN_ICON };
  private static final String[] SUPPORTED_DOCUMENT_PROJECTION=
      new String[] { Document.COLUMN_DOCUMENT_ID, Document.COLUMN_SIZE,
          Document.COLUMN_MIME_TYPE, Document.COLUMN_DISPLAY_NAME,
          Document.COLUMN_FLAGS};
  private static final String ROOT_ID="thisIsMyBoomstick";
  private static final String ROOT_DOCUMENT_ID="docs";
  private AssetManager assets;

  @Override
  public boolean onCreate() {
    assets=getContext().getAssets();

    return(true);
  }

  @Override
  public Cursor queryRoots(String[] projection)
      throws FileNotFoundException {
    String[] netProjection=
        netProjection(projection, SUPPORTED_ROOT_PROJECTION);
    MatrixCursor result=new MatrixCursor(netProjection);
    MatrixCursor.RowBuilder row=result.newRow();

    row.add(Root.COLUMN_ROOT_ID, ROOT_ID);
    row.add(Root.COLUMN_ICON, R.drawable.ic_launcher);
    row.add(Root.COLUMN_FLAGS, Root.FLAG_LOCAL_ONLY);
    row.add(Root.COLUMN_TITLE, getContext().getString(R.string.root));
    row.add(Root.COLUMN_DOCUMENT_ID, ROOT_DOCUMENT_ID);

    return(result);
  }

  @Override
  public Cursor queryChildDocuments(String parentDocId,
                                    String[] projection,
                                    String sortOrder)
      throws FileNotFoundException {
    String[] netProjection=
        netProjection(projection, SUPPORTED_DOCUMENT_PROJECTION);
    MatrixCursor result=new MatrixCursor(netProjection);

    try {
      String[] children=assets.list(parentDocId);

      for (String child : children) {
        addDocumentRow(result, child,
                        parentDocId + File.separator + child);
      }
    }
    catch (IOException e) {
      Log.e(getClass().getSimpleName(),
            "Exception reading asset dir", e);
    }

    return(result);
  }

  @Override
  public Cursor queryDocument(String documentId, String[] projection)
      throws FileNotFoundException {
    String[] netProjection=
        netProjection(projection, SUPPORTED_DOCUMENT_PROJECTION);
    MatrixCursor result=new MatrixCursor(netProjection);

    try {
      addDocumentRow(result, Uri.parse(documentId).getLastPathSegment(),
          documentId);
    }
    catch (IOException e) {
      Log.e(getClass().getSimpleName(), "Exception reading asset dir", e);
    }

    return(result);
  }

  @Override
  public ParcelFileDescriptor openDocument(String documentId,
                                           String mode,
                                           CancellationSignal signal)
      throws FileNotFoundException {
    ParcelFileDescriptor[] pipe=null;

    try {
      pipe=ParcelFileDescriptor.createPipe();
      AssetManager assets=getContext().getAssets();

      new TransferThread(assets.open(documentId),
          new ParcelFileDescriptor.AutoCloseOutputStream(pipe[1])).start();
    }
    catch (IOException e) {
      Log.e(getClass().getSimpleName(), "Exception opening pipe", e);
      throw new FileNotFoundException("Could not open pipe for: "
          + documentId);
    }

    return(pipe[0]);
  }

  private void addDocumentRow(MatrixCursor result, String child,
                              String assetPath) throws IOException {
    MatrixCursor.RowBuilder row=result.newRow();

    row.add(Document.COLUMN_DOCUMENT_ID, assetPath);

    if (isDirectory(assetPath)) {
      row.add(Document.COLUMN_MIME_TYPE, Document.MIME_TYPE_DIR);
    }
    else {
      String ext=MimeTypeMap.getFileExtensionFromUrl(assetPath);

      row.add(Document.COLUMN_MIME_TYPE,
          MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext));
      row.add(Document.COLUMN_SIZE, getAssetLength(assetPath));
    }

    row.add(Document.COLUMN_DISPLAY_NAME, child);
    row.add(Document.COLUMN_FLAGS, 0);
  }

  private boolean isDirectory(String assetPath) throws IOException {
    return(assets.list(assetPath).length>=1);
  }

  private long getAssetLength(String assetPath) throws IOException {
    return(assets.openFd(assetPath).getLength());
  }

  private static String[] netProjection(String[] requested, String[] supported) {
    if (requested==null) {
      return(supported);
    }

    ArrayList<String> result=new ArrayList<String>();

    for (String request : requested) {
      for (String support : supported) {
        if (request.equals(support)) {
          result.add(request);
          break;
        }
      }
    }

    return(result.toArray(new String[0]));
  }

  static class TransferThread extends Thread {
    InputStream in;
    OutputStream out;

    TransferThread(InputStream in, OutputStream out) {
      this.in=in;
      this.out=out;
    }

    @Override
    public void run() {
      byte[] buf=new byte[8192];
      int len;

      try {
        while ((len=in.read(buf)) >= 0) {
          out.write(buf, 0, len);
        }

        in.close();
        out.flush();
        out.close();
      }
      catch (IOException e) {
        Log.e(getClass().getSimpleName(),
            "Exception transferring file", e);
      }
    }
  }
}
