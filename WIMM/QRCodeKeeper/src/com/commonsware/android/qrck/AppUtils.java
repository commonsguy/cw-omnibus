/***
  Copyright (c) 2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Advanced Android Development_
    http://commonsware.com/AdvAndroid
 */

package com.commonsware.android.qrck;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

public class AppUtils {
  static void persist(final SharedPreferences.Editor editor) {
    new Thread() {
      public void run() {
        editor.commit();
      }
    }.run();
  }

  static void cleanup(Context ctxt) {
    try {
      String[] children=ctxt.getFilesDir().list();

      if (children != null) {
        for (int i=0; i < children.length; i++) {
          String filename=children[i];
          new File(ctxt.getFilesDir(), filename).delete();
        }
      }
    }
    catch (Exception ex) {
      // TODO: let the UI know about this via broadcast
      Log.e("QRCodeKeeper-AppUtils", "Exception cleaning up from past exception", ex);
    }
  }

  static JSONObject load(Context ctxt, String fn) throws JSONException,
                                                 IOException {
    FileInputStream is=ctxt.openFileInput(fn);
    InputStreamReader reader=new InputStreamReader(is);
    BufferedReader in=new BufferedReader(reader);
    StringBuilder buf=new StringBuilder();
    String str;

    while ((str=in.readLine()) != null) {
      buf.append(str);
    }

    in.close();

    return(new JSONObject(buf.toString()));
  }
}
