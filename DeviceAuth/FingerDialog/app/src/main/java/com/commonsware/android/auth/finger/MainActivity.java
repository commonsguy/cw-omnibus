/***
 Copyright (c) 2018 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.auth.finger;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.provider.Settings;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends Activity {
  private ImageButton button;
  private Drawable on;
  private Drawable off;
  private BiometricPrompt prompt;
  private CancellationSignal signal=new CancellationSignal();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    prompt=new BiometricPrompt.Builder(this)
      .setTitle("This is the title")
      .setDescription("This is the description")
      .setNegativeButton("Ick!", getMainExecutor(),
        (dialogInterface, i) -> button.setImageDrawable(off))
      .setSubtitle("This is the subtitle")
      .build();

    off=DrawableCompat.wrap(VectorDrawableCompat.create(getResources(),
      R.drawable.ic_fingerprint_black_24dp, null));
    off.setTint(getResources().getColor(android.R.color.black, null));

    on=DrawableCompat.wrap(VectorDrawableCompat.create(getResources(),
      R.drawable.ic_fingerprint_black_24dp, null));
    on.setTint(getResources().getColor(R.color.primary, null));

    button=findViewById(R.id.fingerprint);
    button.setImageDrawable(off);
    button.setOnClickListener(view -> authenticate());
  }

  private void authenticate() {
    if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
      button.setImageDrawable(on);
      prompt.authenticate(signal, getMainExecutor(), authCallback);
    }
    else {
      Toast.makeText(this, R.string.msg_not_available, Toast.LENGTH_LONG).show();
    }
  }

  private final BiometricPrompt.AuthenticationCallback authCallback=
    new BiometricPrompt.AuthenticationCallback() {
    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
      button.setImageDrawable(off);

      if (errorCode==BiometricPrompt.BIOMETRIC_ERROR_NO_BIOMETRICS) {
        startActivity(new Intent(Settings.ACTION_FINGERPRINT_ENROLL));
      }
      else {
        Toast.makeText(MainActivity.this, errString, Toast.LENGTH_LONG).show();
      }
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
      // unused
    }

    @Override
    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
      Toast.makeText(MainActivity.this, R.string.msg_authenticated, Toast.LENGTH_LONG).show();
      button.setImageDrawable(off);
    }

    @Override
    public void onAuthenticationFailed() {
      // unused
    }
  };
}
