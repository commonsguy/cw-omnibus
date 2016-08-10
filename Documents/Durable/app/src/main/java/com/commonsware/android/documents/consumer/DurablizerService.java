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

package com.commonsware.android.documents.consumer;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.UriPermission;
import android.net.Uri;
import android.os.Build;
import android.support.v4.provider.DocumentFile;
import android.util.Log;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.NoSubscriberEvent;
import org.greenrobot.eventbus.Subscribe;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class DurablizerService extends IntentService {
  public DurablizerService() {
    super("DurablizerService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    Uri document=intent.getData();
    boolean weHaveDurablePermission=obtainDurablePermission(document);

    if (!weHaveDurablePermission) {
      document=makeLocalCopy(document);
    }

    if (weHaveDurablePermission || document!=null) {
      Log.d(getClass().getSimpleName(), document.toString());

      DocumentFile docFile=buildDocFileForUri(document);

      Log.d(getClass().getSimpleName(),
        "Display name: "+docFile.getName());
      Log.d(getClass().getSimpleName(),
        "Size: "+Long.toString(docFile.length()));

      EventBus.getDefault().post(new ContentReadyEvent(docFile));
    }
  }

  private boolean obtainDurablePermission(Uri document) {
    boolean weHaveDurablePermission=false;

    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT) {
      int perms=Intent.FLAG_GRANT_READ_URI_PERMISSION
        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

      try {
        getContentResolver()
          .takePersistableUriPermission(document, perms);

        for (UriPermission perm :
          getContentResolver().getPersistedUriPermissions()) {
          if (perm.getUri().equals(document)) {
            weHaveDurablePermission=true;
          }
        }
      }
      catch (SecurityException e) {
        // OK, we were not offered any persistable permissions
      }
    }

    return(weHaveDurablePermission);
  }

  private Uri makeLocalCopy(Uri document) {
    DocumentFile docFile=buildDocFileForUri(document);
    Uri result=null;

    if (docFile.getName()!=null) {
      File f=new File(getFilesDir(), docFile.getName());

      try {
        FileOutputStream fos=new FileOutputStream(f);
        BufferedOutputStream out=new BufferedOutputStream(fos);
        InputStream in=
          getContentResolver().openInputStream(document);

        try {
          byte[] buffer=new byte[8192];
          int len=0;

          while ((len=in.read(buffer))>=0) {
            out.write(buffer, 0, len);
          }

          out.flush();
          result=Uri.fromFile(f);
        }
        finally {
          fos.getFD().sync();
          out.close();
          in.close();
        }
      }
      catch (Exception e) {
        Log.e(getClass().getSimpleName(),
          "Exception copying content to file", e);
      }
    }

    return(result);
  }

  private DocumentFile buildDocFileForUri(Uri document) {
    DocumentFile docFile;

    if (document.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
      docFile=DocumentFile.fromSingleUri(this, document);
    }
    else {
      docFile=DocumentFile.fromFile(new File(document.getPath()));
    }

    return(docFile);
  }

  static class ContentReadyEvent {
    final DocumentFile docFile;

    ContentReadyEvent(DocumentFile docFile) {
      this.docFile=docFile;
    }
  }
}
