/**
 * Copyright (c) 2012-2016 CommonsWare, LLC
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.commonsware.android.kbmouse.hotkeys;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ShortcutDialogFragment extends DialogFragment {
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
    Dialog dlg=builder
      .setTitle(R.string.title_shortcuts)
      .setMessage(R.string.msg_shortcuts)
      .setPositiveButton(android.R.string.ok, null)
      .create();

    return(dlg);
  }
}
