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

package com.commonsware.android.preso.decktastic;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

class PresoRoster {
  private static final PresoRoster INSTANCE=new PresoRoster();
  private static String[] PRESO_ASSET_DIRS={"preso1/", "preso2/"};
  private List<PresoContents> presos=new ArrayList<PresoContents>();

  static PresoRoster getInstance() {
    return(INSTANCE);
  }

  private PresoRoster() {}

  int getPresoCount() {
    return(presos.size());
  }

  PresoContents getPreso(int position) {
    return(presos.get(position));
  }

  PresoContents getPresoById(int id) {
    return(getPreso(id));
  }

  void load(Context ctxt) {
    Gson gson=new Gson();
    AssetManager assets=ctxt.getAssets();

    for (String presoDir : PRESO_ASSET_DIRS) {
      PresoContents c=loadPreso(gson, assets, presoDir);

      if (c!=null) {
        c.id=presos.size();
        presos.add(c);
      }
    }
  }

  private PresoContents loadPreso(Gson gson, AssetManager assets,
                                  String presoDir) {
    PresoContents result=null;

    try {
      InputStream is=assets.open(presoDir+"preso.json");
      BufferedReader reader=
          new BufferedReader(new InputStreamReader(is));

      result=gson.fromJson(reader, PresoContents.class);
      result.baseDir=presoDir;
      is.close();
    }
    catch (IOException e) {
      Log.e(getClass().getSimpleName(), "Exception parsing JSON", e);
    }

    return(result);
  }
}
