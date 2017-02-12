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

package com.commonsware.android.documents.provider;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.commonsware.cwac.provider.StreamProvider;

public class GetContentActivity extends Activity {
  private static final String TYPE_PLAIN="text/plain";
  private static final String TYPE_PDF="application/pdf";
  private static final String TYPE_PNG="image/png";
  private static final String AUTHORITY=
    BuildConfig.APPLICATION_ID+".stream";
  private static final Uri PROVIDER=
    Uri.parse("content://"+AUTHORITY);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    String type=getIntent().getType();
    Uri.Builder builder=PROVIDER
      .buildUpon()
      .appendEncodedPath(StreamProvider.getUriPrefix(AUTHORITY));

    if (TYPE_PLAIN.equals(type)) {
      builder.appendEncodedPath("assets/docs/foo.txt");
    }
    else if (TYPE_PDF.equals(type)) {
      builder.appendEncodedPath("assets/docs/bar/test.pdf");
    }
    else if (TYPE_PNG.equals(type)) {
      builder.appendEncodedPath("assets/docs/bar/ic_launcher.png");
    }
    else {
      builder=null;
    }

    if (builder==null) {
      setResult(RESULT_CANCELED);
    }
    else {
      Intent i=
        new Intent()
          .setData(builder.build())
          .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

      setResult(RESULT_OK, i);
    }

    finish();
  }
}
