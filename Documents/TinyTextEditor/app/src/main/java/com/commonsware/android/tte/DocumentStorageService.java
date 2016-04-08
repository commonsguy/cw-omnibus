/***
 Copyright (c) 2016 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 From _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.tte;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.UriPermission;
import android.net.Uri;
import android.support.v4.provider.DocumentFile;
import android.util.Log;
import android.widget.Toast;
import org.greenrobot.eventbus.EventBus;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;

public class DocumentStorageService extends IntentService {
  private static final String EXTRA_CLOSING="isClosing";

  public static void loadDocument(Context ctxt, Uri document) {
    Intent i=new Intent(ctxt, DocumentStorageService.class)
      .setAction(Intent.ACTION_OPEN_DOCUMENT)
      .setData(document);

    ctxt.startService(i);
  }

  public static void saveDocument(Context ctxt, Uri document,
                                  String text, boolean isClosing) {
    Intent i=new Intent(ctxt, DocumentStorageService.class)
      .setAction(Intent.ACTION_EDIT)
      .setData(document)
      .putExtra(Intent.EXTRA_TEXT, text)
      .putExtra(EXTRA_CLOSING, isClosing);

    ctxt.startService(i);
  }

  public DocumentStorageService() {
    super("DocumentStorageService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    if (Intent.ACTION_OPEN_DOCUMENT.equals(intent.getAction())) {
      load(intent.getData());
    }
    else if (Intent.ACTION_EDIT.equals(intent.getAction())) {
      save(intent.getData(),
        intent.getStringExtra(Intent.EXTRA_TEXT),
        intent.getBooleanExtra(EXTRA_CLOSING, false));
    }
  }

  private void load(Uri document) {
    int perms=Intent.FLAG_GRANT_READ_URI_PERMISSION
      | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

    try {
      getContentResolver()
        .takePersistableUriPermission(document, perms);

      boolean weHavePermission=false;

      for (UriPermission perm :
        getContentResolver().getPersistedUriPermissions()) {
        if (perm.getUri().equals(document)) {
          weHavePermission=true;
        }
      }

      if (weHavePermission) {
        try {
          InputStream is=
            getContentResolver().openInputStream(document);

          try {
            String text=slurp(is);
            DocumentFile docFile=
              DocumentFile.fromSingleUri(this, document);

            EventBus
              .getDefault()
              .post(
                new DocumentLoadedEvent(document, text,
                  docFile.getName(), docFile.canWrite()));
          }
          finally {
            is.close();
          }
        }
        catch (Exception e) {
          Log.e(getClass().getSimpleName(),
            "Exception loading "+document.toString(), e);
          EventBus
            .getDefault()
            .post(new DocumentLoadErrorEvent(document, e));
        }
      }
      else {
        Log.e(getClass().getSimpleName(),
          "We failed to get permissions for "+document.toString());
        EventBus
          .getDefault()
          .post(new DocumentPermissionFailureEvent(document));
      }
    }
    catch (SecurityException e) {
      Log.e(getClass().getSimpleName(),
        "Exception getting permissions for "+document.toString(), e);
      EventBus
        .getDefault()
        .post(new DocumentPermissionFailureEvent(document));
    }
  }

  private void save(Uri document, String text, boolean isClosing) {
    try {
      OutputStream os=
        getContentResolver().openOutputStream(document, "w");
      OutputStreamWriter osw=new OutputStreamWriter(os);

      try {
        osw.write(text);
        osw.flush();

        if (isClosing) {
          int perms=Intent.FLAG_GRANT_READ_URI_PERMISSION
            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

          getContentResolver()
            .releasePersistableUriPermission(document, perms);
        }

        EventBus
          .getDefault()
          .post(new DocumentSavedEvent(document));
      }
      finally {
        osw.close();
      }
    }
    catch (Exception e) {
      Log.e(getClass().getSimpleName(),
        "Exception saving "+document.toString(), e);
      EventBus
        .getDefault()
        .post(new DocumentSaveErrorEvent(document, e));
    }
  }

  // based on http://stackoverflow.com/a/309718/115145

  private static String slurp(final InputStream is)
    throws IOException {
    final char[] buffer=new char[8192];
    final StringBuilder out=new StringBuilder();
    final Reader in=new InputStreamReader(is, "UTF-8");
    int rsz=in.read(buffer, 0, buffer.length);

    while (rsz>0) {
      out.append(buffer, 0, rsz);
      rsz=in.read(buffer, 0, buffer.length);
    }

    return(out.toString());
  }

  public static class DocumentEvent {
    public final Uri document;

    DocumentEvent(Uri document) {
      this.document=document;
    }
  }

  public static class DocumentLoadedEvent extends DocumentEvent {
    public final String text;
    public final String displayName;
    public final boolean canWrite;

    DocumentLoadedEvent(Uri document, String text,
                        String displayName, boolean canWrite) {
      super(document);
      this.text=text;
      this.displayName=displayName;
      this.canWrite=canWrite;
    }
  }

  public static class DocumentErrorEvent extends DocumentEvent {
    public final Exception e;

    DocumentErrorEvent(Uri document, Exception e) {
      super(document);
      this.e=e;
    }
  }

  public static class DocumentLoadErrorEvent
    extends DocumentErrorEvent {
    DocumentLoadErrorEvent(Uri document, Exception e) {
      super(document, e);
    }
  }

  public static class DocumentSaveErrorEvent
    extends DocumentErrorEvent {
    DocumentSaveErrorEvent(Uri document, Exception e) {
      super(document, e);
    }
  }

  public static class DocumentSavedEvent extends DocumentEvent {
    DocumentSavedEvent(Uri document) {
      super(document);
    }
  }

  public static class DocumentPermissionFailureEvent
    extends DocumentEvent {
    DocumentPermissionFailureEvent(Uri document) {
      super(document);
    }
  }
}
