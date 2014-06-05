/***
  Copyright (c) 2008-2013 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain	a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
	
  From _The Busy Coder's Guide to Android Development_
    http://commonsware.com/Android
 */

package com.commonsware.android.weather2;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class WeatherDemo extends Activity {
  protected static final String TAG_ERROR_DIALOG_FRAGMENT="errorDialog";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (readyToGo()) {
      if (getFragmentManager().findFragmentById(android.R.id.content) == null) {
        getFragmentManager().beginTransaction()
                            .add(android.R.id.content,
                                 new WeatherFragment()).commit();
      }
    }
  }

  protected boolean readyToGo() {
    int status=
        GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

    if (status == ConnectionResult.SUCCESS) {
      return(true);
    }
    else if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
      ErrorDialogFragment.newInstance(status)
                         .show(getFragmentManager(),
                               TAG_ERROR_DIALOG_FRAGMENT);
    }
    else {
      Toast.makeText(this, R.string.no_fused, Toast.LENGTH_LONG).show();
      finish();
    }

    return(false);
  }

  public static class ErrorDialogFragment extends DialogFragment {
    static final String ARG_STATUS="status";

    static ErrorDialogFragment newInstance(int status) {
      Bundle args=new Bundle();

      args.putInt(ARG_STATUS, status);

      ErrorDialogFragment result=new ErrorDialogFragment();

      result.setArguments(args);

      return(result);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      Bundle args=getArguments();

      return GooglePlayServicesUtil.getErrorDialog(args.getInt(ARG_STATUS),
                                                   getActivity(), 0);
    }

    @Override
    public void onDismiss(DialogInterface dlg) {
      if (getActivity() != null) {
        getActivity().finish();
      }
    }
  }
}
