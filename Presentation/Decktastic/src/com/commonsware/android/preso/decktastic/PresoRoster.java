/***
 Copyright (c) 2014 CommonsWare, LLC
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

package com.commonsware.android.preso.decktastic;

import android.util.Log;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

class PresoRoster {
  private static final PresoRoster INSTANCE=new PresoRoster();
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

  void load(File base) {
    base.mkdirs();

    String[] presoDirs=base.list(new FilenameFilter() {
      @Override
      public boolean accept(File orig, String name) {
        return(new File(orig, name).isDirectory());
      }
    });

    Gson gson=new Gson();

    for (String presoDir : presoDirs) {
      PresoContents c=loadPreso(gson, new File(base, presoDir));

      if (c!=null) {
        c.id=presos.size();
        presos.add(c);
      }
    }
  }

  private PresoContents loadPreso(Gson gson, File base) {
    PresoContents result=null;

    try {
      InputStream is=new FileInputStream(new File(base, "preso.json"));
      BufferedReader reader=
          new BufferedReader(new InputStreamReader(is));

      result=gson.fromJson(reader, PresoContents.class);
      result.baseDir=base;
      is.close();
    }
    catch (IOException e) {
      Log.e(getClass().getSimpleName(), "Exception parsing JSON", e);
    }

    return(result);
  }
}
