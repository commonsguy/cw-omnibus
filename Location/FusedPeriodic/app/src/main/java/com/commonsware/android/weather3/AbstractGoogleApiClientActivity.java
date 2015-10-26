/***
 Copyright (c) 2015 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 From _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.weather3;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import java.util.ArrayList;

abstract public class AbstractGoogleApiClientActivity
  extends Activity implements GoogleApiClient.ConnectionCallbacks,
  GoogleApiClient.OnConnectionFailedListener {
  abstract protected GoogleApiClient.Builder
    configureApiClientBuilder(GoogleApiClient.Builder b);
  abstract protected String[] getDesiredPermissions();
  abstract protected void handlePermissionDenied();

  private static final int REQUEST_RESOLUTION=61124;
  private static final int REQUEST_PERMISSION=61125;
  private static final String TAG_ERROR_DIALOG_FRAGMENT="errorDialog";
  private static final String STATE_IN_PERMISSION="inPermission";
  private static final String STATE_IN_RESOLUTION="inResolution";
  private GoogleApiClient playServices;
  private boolean isResolvingPlayServicesError=false;
  private boolean isInPermission=false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (savedInstanceState!=null) {
      isInPermission=
        savedInstanceState.getBoolean(STATE_IN_PERMISSION, false);
      isResolvingPlayServicesError=
        savedInstanceState.getBoolean(STATE_IN_RESOLUTION, false);
    }

    if (hasAllPermissions(getDesiredPermissions())) {
      initPlayServices();
    }
    else if (!isInPermission) {
      isInPermission=true;

      ActivityCompat
        .requestPermissions(this,
          netPermissions(getDesiredPermissions()),
          REQUEST_PERMISSION);
    }
  }

  @Override
  protected void onStart() {
    super.onStart();

    if (!isResolvingPlayServicesError && playServices!=null) {
      playServices.connect();
    }
  }

  @Override
  protected void onStop() {
    if (playServices!=null) {
      playServices.disconnect();
    }

    super.onStop();
  }

  @Override
  public void onConnectionFailed(ConnectionResult result) {
    if (!isResolvingPlayServicesError) {
      if (result.hasResolution()) {
        try {
          isResolvingPlayServicesError=true;
          result.startResolutionForResult(this, REQUEST_RESOLUTION);
        }
        catch (IntentSender.SendIntentException e) {
          playServices.connect();
        }
      }
      else {
        ErrorDialogFragment.newInstance(result.getErrorCode())
          .show(getFragmentManager(),
            TAG_ERROR_DIALOG_FRAGMENT);
        isResolvingPlayServicesError=true;
      }
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         String[] permissions,
                                         int[] grantResults) {
    isInPermission=false;

    if (requestCode==REQUEST_PERMISSION) {
      if (hasAllPermissions(getDesiredPermissions())) {
        initPlayServices();
        playServices.connect();
      }
      else {
        handlePermissionDenied();
      }
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putBoolean(STATE_IN_PERMISSION, isInPermission);
    outState.putBoolean(STATE_IN_RESOLUTION,
      isResolvingPlayServicesError);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater()
      .inflate(R.menu.abstract_google_api_client_activity, menu);

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.legal) {
      startActivity(new Intent(this, LegalNoticesActivity.class));

      return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  @Override
  protected void onActivityResult(int requestCode,
                                  int resultCode, Intent data) {
    isResolvingPlayServicesError=false;

    super.onActivityResult(requestCode, resultCode, data);
  }

  GoogleApiClient getPlayServices() {
    return(playServices);
  }

  protected void initPlayServices() {
    playServices=
      configureApiClientBuilder(new GoogleApiClient.Builder(this))
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .build();
  }

  private boolean hasAllPermissions(String[] perms) {
    for (String perm : perms) {
      if (!hasPermission(perm)) {
        return(false);
      }
    }

    return(true);
  }

  private boolean hasPermission(String perm) {
    return(ContextCompat.checkSelfPermission(this, perm)==
      PackageManager.PERMISSION_GRANTED);
  }

  private String[] netPermissions(String[] wanted) {
    ArrayList<String> result=new ArrayList<String>();

    for (String perm : wanted) {
      if (!hasPermission(perm)) {
        result.add(perm);
      }
    }

    return(result.toArray(new String[result.size()]));
  }

  public static class ErrorDialogFragment extends
    DialogFragment {
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
      return(GoogleApiAvailability
        .getInstance()
        .getErrorDialog(
          getActivity(),
          getArguments().getInt(ARG_ERROR_CODE),
                REQUEST_RESOLUTION));
    }

    @Override
    public void onCancel(DialogInterface dlg) {
      if (getActivity()!=null) {
        getActivity().finish();
      }

      super.onCancel(dlg);
    }

    @Override
    public void onDismiss(DialogInterface dlg) {
      if (getActivity()!=null) {
        ((AbstractGoogleApiClientActivity)getActivity())
          .isResolvingPlayServicesError=false;
      }

      super.onDismiss(dlg);
    }
  }
}
