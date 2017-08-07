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
import android.service.autofill.AutofillService;
import android.service.autofill.FillCallback;
import android.service.autofill.FillContext;
import android.service.autofill.FillRequest;
import android.service.autofill.FillResponse;
import android.service.autofill.SaveCallback;
import android.service.autofill.SaveInfo;
import android.service.autofill.SaveRequest;
import android.util.Log;
import android.view.autofill.AutofillId;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class AutoFillLoggerService extends AutofillService {
  private static final String EXTRA_LOG_DIR="logDirName";
  private static final int SAVE_DATA_ALL=SaveInfo.SAVE_DATA_TYPE_GENERIC |
    SaveInfo.SAVE_DATA_TYPE_ADDRESS | SaveInfo.SAVE_DATA_TYPE_CREDIT_CARD |
    SaveInfo.SAVE_DATA_TYPE_EMAIL_ADDRESS | SaveInfo.SAVE_DATA_TYPE_PASSWORD |
    SaveInfo.SAVE_DATA_TYPE_USERNAME;

  @Override
  public void onFillRequest(FillRequest request,
                            CancellationSignal cancellationSignal,
                            FillCallback fillCallback) {
    if (Environment.MEDIA_MOUNTED
      .equals(Environment.getExternalStorageState())) {
      Bundle extras=request.getClientState();

      if (extras==null) {
        extras=new Bundle();
      }

      File logDir=getLogDir(extras);
      Set<AutofillId> ids=new HashSet<>();
      AutofillId first=null;
      ArrayList<AssistStructure> assistStructures=new ArrayList<>();

      for (FillContext fillContext : request.getFillContexts()) {
        AssistStructure assistStructure=fillContext.getStructure();

        assistStructures.add(assistStructure);

        for (int i=0; i<assistStructure.getWindowNodeCount(); i++) {
          AssistStructure.WindowNode node=assistStructure.getWindowNodeAt(i);

          AutofillId temp=collectViewIds(node.getRootViewNode(), ids);

          if (first==null) {
            first=temp;
          }
        }
      }

      ids.remove(first);

      SaveInfo saveInfo=
        new SaveInfo.Builder(SAVE_DATA_ALL, new AutofillId[] { first })
          .setOptionalIds(ids.toArray(new AutofillId[ids.size()]))
          .build();

      FillResponse.Builder b=new FillResponse.Builder();

      b.setSaveInfo(saveInfo);
      b.setClientState(extras);

      Log.d(getClass().getSimpleName(),
        String.format("onFillRequest() called, saving %d fields", ids.size()));

      FillResponse response=b.build();

      try {
        File log=File.createTempFile("fill-", ".json", logDir);

        new DumpThread.Fill(this, log, extras, assistStructures, fillCallback,
          response).start();
      }
      catch (IOException e) {
        Log.e(getClass().getSimpleName(), "Exception creating temp file", e);
      }
    }
    else {
      fillCallback.onSuccess(null);
    }
  }

  @Override
  public void onSaveRequest(SaveRequest request, SaveCallback saveCallback) {
    Log.d(getClass().getSimpleName(), "onSaveRequest() called");

    if (Environment.MEDIA_MOUNTED
      .equals(Environment.getExternalStorageState())) {
      Bundle extras=request.getClientState();
      File logDir=getLogDir(extras);

      try {
        File log=File.createTempFile("save-", ".json", logDir);

        new DumpThread.Save(this, log, request, saveCallback).start();
      }
      catch (IOException e) {
        saveCallback.onFailure(e.getMessage());
        Log.e(getClass().getSimpleName(), "Exception creating temp file", e);
      }
    }
  }

  private AutofillId collectViewIds(AssistStructure.ViewNode node,
                                    Set<AutofillId> ids) {
    AutofillId result=null;

    if (node.getAutofillHints()!=null && node.getAutofillHints().length>0) {
      result=node.getAutofillId();
      ids.add(result);
    }

    for (int i=0; i<node.getChildCount(); i++) {
      AutofillId temp=collectViewIds(node.getChildAt(i), ids);

      if (result==null) {
        result=temp;
      }
    }

    return(result);
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
