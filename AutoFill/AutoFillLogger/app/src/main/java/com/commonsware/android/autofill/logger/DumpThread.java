/***
 Copyright (c) 2015-2017 CommonsWare, LLC
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
import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.service.autofill.FillCallback;
import android.service.autofill.FillContext;
import android.service.autofill.FillResponse;
import android.service.autofill.SaveCallback;
import android.service.autofill.SaveRequest;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Set;

abstract class DumpThread extends Thread {
  abstract void writeTo(JSONObject json);
  abstract void onSuccess();
  abstract void onFailure(String message);

  private final File logFile;
  private final Context ctxt;

  private DumpThread(Context ctxt, File logDir) {
    this.ctxt=ctxt.getApplicationContext();
    this.logFile=logDir;
  }

  @Override
  public void run() {
    if (logFile!=null) {
      JSONObject json=new JSONObject();

      writeTo(json);

      try {
        FileOutputStream fos=new FileOutputStream(logFile);
        OutputStreamWriter osw=new OutputStreamWriter(fos);
        PrintWriter pw=new PrintWriter(osw);

        pw.print(json.toString(2));
        pw.flush();
        fos.getFD().sync();
        fos.close();

        MediaScannerConnection
          .scanFile(ctxt,
            new String[]{logFile.getAbsolutePath()},
            new String[]{"application/json"}, null);

        Log.d(getClass().getSimpleName(),
          "autofill data written to: "+logFile.getAbsolutePath());

        onSuccess();
      }
      catch (Exception e) {
        Log.e(getClass().getSimpleName(),
          "Exception writing out autofill data", e);
        onFailure("Exception writing out autofill data");
      }
    }
    else {
      Log.d(getClass().getSimpleName(), "no log directory!");
    }
  }

  protected JSONObject dumpBundle(Bundle b, JSONObject json)
    throws JSONException {
    Set<String> keys=b.keySet();

    for (String key : keys) {
      json.put(key, wrap(b.get(key)));
    }

    return(json);
  }

  protected JSONObject dumpStructure(AssistStructure structure, JSONObject json)
    throws JSONException {
    return (json.put("windows",
      dumpStructureWindows(structure, new JSONArray())));
  }

  protected JSONArray dumpStructureWindows(AssistStructure structure,
                                           JSONArray windows)
    throws JSONException {
    for (int i=0; i<structure.getWindowNodeCount(); i++) {
      windows.put(
        dumpStructureWindow(structure.getWindowNodeAt(i),
          new JSONObject()));
    }

    return(windows);
  }

  protected JSONObject dumpStructureWindow(
    AssistStructure.WindowNode window,
    JSONObject json)
    throws JSONException {
    json.put("displayId", wrap(window.getDisplayId()));
    json.put("height", wrap(window.getHeight()));
    json.put("left", wrap(window.getLeft()));
    json.put("title", wrap(window.getTitle()));
    json.put("top", wrap(window.getTop()));
    json.put("width", wrap(window.getWidth()));
    json.put("root",
      dumpStructureNode(window.getRootViewNode(),
        new JSONObject()));

    return(json);
  }

  private JSONObject dumpStructureNode(
    AssistStructure.ViewNode node,
    JSONObject json)
    throws JSONException {
    json.put("accessibilityFocused",
      wrap(node.isAccessibilityFocused()));
    json.put("activated", wrap(node.isActivated()));
    json.put("alpha", wrap(node.getAlpha()));
    json.put("assistBlocked", wrap(node.isAssistBlocked()));
    json.put("autofillHints", wrap(node.getAutofillHints()));
    json.put("autofillId", wrap(node.getAutofillId()));
    json.put("autofillOptions", wrap(node.getAutofillOptions()));
    json.put("autofillType", wrap(node.getAutofillType()));
    json.put("autofillValue", wrap(node.getAutofillValue()));
    json.put("checkable", wrap(node.isCheckable()));
    json.put("checked", wrap(node.isChecked()));
    json.put("className", wrap(node.getClassName()));
    json.put("clickable", wrap(node.isClickable()));
    json.put("contentDescription",
      wrap(node.getContentDescription()));
    json.put("contextClickable",
      wrap(node.isContextClickable()));
    json.put("elevation", wrap(node.getElevation()));
    json.put("enabled", wrap(node.isEnabled()));

    if (node.getExtras()!=null) {
      json.put("extras", dumpBundle(node.getExtras(),
        new JSONObject()));
    }

    json.put("focusable", wrap(node.isFocusable()));
    json.put("focused", wrap(node.isFocused()));
    json.put("height", wrap(node.getHeight()));
    json.put("hint", wrap(node.getHint()));
    json.put("htmlInfo", wrap(node.getHtmlInfo()));
    json.put("id", wrap(node.getId()));
    json.put("idEntry", wrap(node.getIdEntry()));
    json.put("idPackage", wrap(node.getIdPackage()));
    json.put("idType", wrap(node.getIdType()));
    json.put("inputType", wrap(node.getInputType()));
    json.put("left", wrap(node.getLeft()));
    json.put("longClickable", wrap(node.isLongClickable()));
    json.put("scrollX", wrap(node.getScrollX()));
    json.put("scrollY", wrap(node.getScrollY()));
    json.put("isSelected", wrap(node.isSelected()));
    json.put("isOpaque", wrap(node.isOpaque()));
    json.put("text", wrap(node.getText()));
    json.put("textBackgroundColor",
      wrap(node.getTextBackgroundColor()));
    json.put("textColor", wrap(node.getTextColor()));
    json.put("textLineBaselines",
      wrap(node.getTextLineBaselines()));
    json.put("textLineCharOffsets",
      wrap(node.getTextLineCharOffsets()));
    json.put("textSelectionEnd",
      wrap(node.getTextSelectionEnd()));
    json.put("textSelectionStart",
      wrap(node.getTextSelectionStart()));
    json.put("textSize", wrap(node.getTextSize()));
    json.put("textStyle", wrap(node.getTextStyle()));
    json.put("top", wrap(node.getTop()));
    json.put("transformation",
      wrap(node.getTransformation()));
    json.put("visibility", wrap(node.getVisibility()));
    json.put("webDomain", wrap(node.getWebDomain()));
    json.put("width", wrap(node.getWidth()));

    json.put("children",
      dumpStructureNodes(node, new JSONArray()));

    return(json);
  }

  private JSONArray dumpStructureNodes(
    AssistStructure.ViewNode node,
    JSONArray children) throws JSONException {
    for (int i=0; i<node.getChildCount(); i++) {
      children.put(dumpStructureNode(node.getChildAt(i),
        new JSONObject()));
    }

    return(children);
  }

  private Object wrap(Object thingy) {
    if (thingy instanceof Array) {
      Object[] array=(Object[])thingy;
      JSONArray jsonArray=new JSONArray();

      for (Object o : array) {
        jsonArray.put(wrap(o));
      }

      return(jsonArray);
    }

    if (thingy instanceof CharSequence) {
      return(JSONObject.wrap(thingy.toString()));
    }

    return(JSONObject.wrap(thingy));
  }

  static class Save extends DumpThread {
    private final SaveCallback saveCallback;
    private final SaveRequest request;

    Save(Context ctxt, File logDir, SaveRequest request,
         SaveCallback saveCallback) {
      super(ctxt, logDir);

      this.saveCallback=saveCallback;
      this.request=request;
    }

    @Override
    void writeTo(JSONObject json) {
      if (request.getClientState()!=null) {
        try {
          json.put("data",
            dumpBundle(request.getClientState(), new JSONObject()));
        }
        catch (JSONException e) {
          Log.e(getClass().getSimpleName(),
            "Exception saving data", e);
        }
      }

      try {
        JSONArray contexts=new JSONArray();

        for (FillContext fillContext : request.getFillContexts()) {
          contexts.put(dumpStructure(fillContext.getStructure(),
            new JSONObject()));
        }

        json.put("contexts", contexts);
      }
      catch (JSONException e) {
        Log.e(getClass().getSimpleName(),
          "Exception saving structure", e);
      }
    }

    @Override
    void onSuccess() {
      saveCallback.onSuccess();
    }

    @Override
    void onFailure(String message) {
      saveCallback.onFailure(message);
    }
  }

  static class Fill extends DumpThread {
    private final FillCallback fillCallback;
    private final FillResponse response;
    private final Bundle data;
    private final List<AssistStructure> structures;

    Fill(Context ctxt, File logDir, Bundle data, List<AssistStructure> structures,
         FillCallback fillCallback, FillResponse response) {
      super(ctxt, logDir);

      this.fillCallback=fillCallback;
      this.response=response;
      this.data=data;
      this.structures=structures;
    }

    @Override
    void writeTo(JSONObject json) {
      if (data!=null) {
        try {
          json.put("data", dumpBundle(data, new JSONObject()));
        }
        catch (JSONException e) {
          Log.e(getClass().getSimpleName(),
            "Exception saving data", e);
        }
      }

      JSONArray structureArray=new JSONArray();

      try {
        json.put("structures", structureArray);

        for (AssistStructure structure : structures) {
          structureArray.put(dumpStructure(structure, new JSONObject()));
        }
      }
      catch (JSONException e) {
        Log.e(getClass().getSimpleName(),
          "Exception saving structure", e);
      }
    }

    @Override
    void onSuccess() {
      fillCallback.onSuccess(response);
    }

    @Override
    void onFailure(String message) {
      fillCallback.onFailure(message);
    }
  }
}
