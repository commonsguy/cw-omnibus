/***
  Copyright (c) 2012-2017 CommonsWare, LLC
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

package com.commonsware.android.mapsv2.imagepopups;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

abstract public class AbstractMapActivity extends FragmentActivity {
  static final String TAG_ERROR_DIALOG_FRAGMENT="errorDialog";

  protected boolean readyToGo() {
    GoogleApiAvailability checker=
      GoogleApiAvailability.getInstance();

    int status=checker.isGooglePlayServicesAvailable(this);

    if (status == ConnectionResult.SUCCESS) {
      if (getVersionFromPackageManager(this)>=2) {
        return(true);
      }
      else {
        Toast.makeText(this, R.string.no_maps, Toast.LENGTH_LONG).show();
        finish();
      }
    }
    else if (checker.isUserResolvableError(status)) {
      ErrorDialogFragment.newInstance(status)
                         .show(getFragmentManager(),
                               TAG_ERROR_DIALOG_FRAGMENT);
    }
    else {
      Toast.makeText(this, R.string.no_maps, Toast.LENGTH_LONG).show();
      finish();
    }

    return(false);
  }

  public static class ErrorDialogFragment extends DialogFragment {
    static final String ARG_ERROR_CODE="errorCode";

    static ErrorDialogFragment newInstance(int errorCode) {
      Bundle args=new Bundle();
      ErrorDialogFragment result=new ErrorDialogFragment();

      args.putInt(ARG_ERROR_CODE, errorCode);
      result.setArguments(args);

      return(result);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      Bundle args=getArguments();
      GoogleApiAvailability checker=
        GoogleApiAvailability.getInstance();

      return(checker.getErrorDialog(getActivity(),
        args.getInt(ARG_ERROR_CODE), 0));
    }

    @Override
    public void onDismiss(DialogInterface dlg) {
      if (getActivity()!=null) {
        getActivity().finish();
      }
    }
  }

  // following from
  // https://android.googlesource.com/platform/cts/+/master/tests/tests/graphics/src/android/opengl/cts/OpenGlEsVersionTest.java

  /*
   * Copyright (C) 2010 The Android Open Source Project
   * 
   * Licensed under the Apache License, Version 2.0 (the
   * "License"); you may not use this file except in
   * compliance with the License. You may obtain a copy of
   * the License at
   * 
   * http://www.apache.org/licenses/LICENSE-2.0
   * 
   * Unless required by applicable law or agreed to in
   * writing, software distributed under the License is
   * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   * CONDITIONS OF ANY KIND, either express or implied. See
   * the License for the specific language governing
   * permissions and limitations under the License.
   */

  private static int getVersionFromPackageManager(Context context) {
    PackageManager packageManager=context.getPackageManager();
    FeatureInfo[] featureInfos=
        packageManager.getSystemAvailableFeatures();
    if (featureInfos != null && featureInfos.length > 0) {
      for (FeatureInfo featureInfo : featureInfos) {
        // Null feature name means this feature is the open
        // gl es version feature.
        if (featureInfo.name == null) {
          if (featureInfo.reqGlEsVersion != FeatureInfo.GL_ES_VERSION_UNDEFINED) {
            return getMajorVersion(featureInfo.reqGlEsVersion);
          }
          else {
            return 1; // Lack of property means OpenGL ES
                      // version 1
          }
        }
      }
    }
    return 1;
  }

  /** @see FeatureInfo#getGlEsVersion() */
  private static int getMajorVersion(int glEsVersion) {
    return((glEsVersion & 0xffff0000) >> 16);
  }
}
