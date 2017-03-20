/***
 Copyright (c) 2016 CommonsWare, LLC
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

package com.commonsware.android.documents.consumer;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.UriPermission;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.webkit.MimeTypeMap;
import com.commonsware.cwac.document.DocumentFileCompat;
import org.greenrobot.eventbus.EventBus;
import java.io.File;

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

      DocumentFileCompat docFile=buildDocFileForUri(document);

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
    DocumentFileCompat docFile=buildDocFileForUri(document);
    Uri result=null;

    if (docFile.getName()!=null) {
      try {
        String ext=
          MimeTypeMap.getSingleton().getExtensionFromMimeType(docFile.getType());

        if (ext!=null) {
          ext="."+ext;
        }

        File f=File.createTempFile("cw_", ext, getFilesDir());

        docFile.copyTo(f);
        result=Uri.fromFile(f);
      }
      catch (Exception e) {
        Log.e(getClass().getSimpleName(),
          "Exception copying content to file", e);
      }
    }

    return(result);
  }

  private DocumentFileCompat buildDocFileForUri(Uri document) {
    DocumentFileCompat docFile;

    if (document.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
      docFile=DocumentFileCompat.fromSingleUri(this, document);
    }
    else {
      docFile=DocumentFileCompat.fromFile(new File(document.getPath()));
    }

    return(docFile);
  }

  static class ContentReadyEvent {
    final DocumentFileCompat docFile;

    ContentReadyEvent(DocumentFileCompat docFile) {
      this.docFile=docFile;
    }
  }
}
