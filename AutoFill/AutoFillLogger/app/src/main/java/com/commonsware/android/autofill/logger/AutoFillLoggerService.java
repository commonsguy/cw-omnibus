/***
 Copyright (c) 2017 CommonsWare, LLC
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

package com.commonsware.android.autofill.logger;

import android.app.assist.AssistStructure;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.service.autofill.AutoFillService;
import android.service.autofill.FillCallback;
import android.service.autofill.FillResponse;
import android.service.autofill.SaveCallback;
import android.util.Log;
import android.view.autofill.AutoFillId;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class AutoFillLoggerService extends AutoFillService {
  private static final String EXTRA_LOG_DIR="logDirName";

  @Override
  public void onFillRequest(AssistStructure assistStructure, Bundle extras,
                            CancellationSignal cancellationSignal,
                            FillCallback fillCallback) {
    if (Environment.MEDIA_MOUNTED
      .equals(Environment.getExternalStorageState())) {
      if (extras==null) {
        extras=new Bundle();
      }

      File logDir=getLogDir(extras);
      Set<AutoFillId> ids=new HashSet<>();

      for (int i=0; i<assistStructure.getWindowNodeCount(); i++) {
        AssistStructure.WindowNode node=assistStructure.getWindowNodeAt(i);

        collectViewIds(node.getRootViewNode(), ids);
      }

      FillResponse.Builder b=new FillResponse.Builder();

      b.addSavableFields(ids.toArray(new AutoFillId[ids.size()]));
      b.setExtras(extras);

      Log.d(getClass().getSimpleName(),
        String.format("onFillRequest() called, saving %d fields", ids.size()));

      try {
        File log=File.createTempFile("fill-", ".json", logDir);

        new DumpThread.Fill(this, log, extras, assistStructure, fillCallback,
          b.build()).start();
      }
      catch (IOException e) {
        fillCallback.onSuccess(b.build());
        Log.e(getClass().getSimpleName(), "Exception creating temp file", e);
      }
    }
    else {
      fillCallback.onSuccess(null);
    }
  }

  @Override
  public void onSaveRequest(AssistStructure assistStructure, Bundle extras,
                            SaveCallback saveCallback) {

    Log.d(getClass().getSimpleName(), "onSaveRequest() called");

    if (Environment.MEDIA_MOUNTED
      .equals(Environment.getExternalStorageState())) {
      File logDir=getLogDir(extras);

      try {
        File log=File.createTempFile("save-", ".json", logDir);

        new DumpThread.Save(this, log, extras, assistStructure, saveCallback)
          .start();
      }
      catch (IOException e) {
        saveCallback.onFailure(e.getMessage());
        Log.e(getClass().getSimpleName(), "Exception creating temp file", e);
      }
    }
  }

  private void collectViewIds(AssistStructure.ViewNode node,
                              Set<AutoFillId> ids) {
    ids.add(node.getAutoFillId());

    for (int i=0; i<node.getChildCount(); i++) {
      collectViewIds(node.getChildAt(i), ids);
    }
  }

  private File getLogDir(Bundle extras) {
    File result=(File)extras.getSerializable(EXTRA_LOG_DIR);

    if (result==null) {
      String logDirName=
        "autofilllogger_"+
          new SimpleDateFormat("yyyyMMdd'-'HHmmss").format(new Date());

      result=new File(getExternalCacheDir(), logDirName);
      result.mkdirs();
      extras.putSerializable(EXTRA_LOG_DIR, result);
    }

    return(result);
  }
}
