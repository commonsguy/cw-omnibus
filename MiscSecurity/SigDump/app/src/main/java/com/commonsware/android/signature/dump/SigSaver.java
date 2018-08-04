/***
 Copyright (c) 2013-2018 CommonsWare, LLC
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

package com.commonsware.android.signature.dump;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;

public class SigSaver extends JobIntentService {
  private static final int UNIQUE_JOB_ID=1337;
  private static final String EXTRA_PACKAGE="package";

  static void enqueueWork(Context ctxt, PackageInfo packageInfo) {
    Intent i=new Intent(ctxt, SigSaver.class)
      .putExtra(EXTRA_PACKAGE, packageInfo.packageName);

    enqueueWork(ctxt, SigSaver.class, UNIQUE_JOB_ID, i);
  }

  @Override
  protected void onHandleWork(@NonNull Intent intent) {
    String packageName=intent.getStringExtra(EXTRA_PACKAGE);

    try {
      PackageInfo packageInfo=getPackageManager().getPackageInfo(packageName,
        PackageManager.GET_SIGNATURES);
      File output=
        new File(getExternalFilesDir(null),
          packageInfo.packageName.replace('.', '_')+".bin");

      if (output.exists()) {
        output.delete();
      }

      Signature[] signatures=packageInfo.signatures;
      byte[] raw=signatures[0].toByteArray();

      try {
        FileOutputStream fos=new FileOutputStream(output.getPath());

        fos.write(raw);
        fos.close();
      }
      catch (java.io.IOException e) {
        Log.e(getClass().getSimpleName(),
          "Exception in writing signature file", e);
      }
    }
    catch (PackageManager.NameNotFoundException e) {
      Log.e(getClass().getSimpleName(),
        "Exception loading package info: "+packageName, e);
    }
  }
}
