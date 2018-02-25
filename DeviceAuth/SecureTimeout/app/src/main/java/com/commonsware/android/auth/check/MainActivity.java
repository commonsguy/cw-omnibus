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

package com.commonsware.android.auth.check;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyInfo;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.security.keystore.UserNotAuthenticatedException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
import java.security.KeyStore;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;

public class MainActivity extends Activity {
  private static final String KEYSTORE="AndroidKeyStore";
  private static final String KEY_NAME="sooper-sekrit-key";
  private static final byte[] POINTLESS_DATA=new byte[] {1, 2, 3};
  private static final int TIMEOUT_SECONDS=60;
  private static final int REQUEST_CODE=1337;
  private KeyguardManager mgr;
  private KeyStore ks;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    try {
      ks=KeyStore.getInstance(KEYSTORE);
      ks.load(null);
    }
    catch (Exception e) {
      Toast.makeText(this, "Ummm... this shouldn't happen", Toast.LENGTH_LONG).show();
      Log.e(getClass().getSimpleName(), "Exception initializing keystore", e);
    }

    mgr=(KeyguardManager)getSystemService(KEYGUARD_SERVICE);

    ImageView device=findViewById(R.id.device);
    ImageView keyguard=findViewById(R.id.keyguard);

    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
      if (mgr.isDeviceSecure()) {
        device.setImageResource(R.drawable.ic_lock_black_24dp);
      }
      else {
        device.setImageResource(R.drawable.ic_lock_open_black_24dp);
      }
    }
    else {
      device.setImageResource(R.drawable.ic_help_black_24dp);
    }

    if (mgr.isKeyguardSecure()) {
      keyguard.setImageResource(R.drawable.ic_lock_black_24dp);
    }
    else {
      keyguard.setImageResource(R.drawable.ic_lock_open_black_24dp);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.actions, menu);
    menu.findItem(R.id.auth).setEnabled(mgr.isKeyguardSecure());

    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId()==R.id.auth) {
      authenticate();

      return true;
    }
    else if (item.getItemId()==R.id.hw) {
      isInsideSecureHardware();
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode,
                                  Intent data) {
    if (requestCode==REQUEST_CODE) {
      if (resultCode==RESULT_OK) {
        if (needsAuth(true)) {
          Toast.makeText(this, "Good authentication... but still needs auth?",
            Toast.LENGTH_SHORT).show();
        }
        else {
          Toast.makeText(this, "Authenticated!", Toast.LENGTH_SHORT).show();
        }
      }
      else {
        Toast.makeText(this, "WE ARE UNDER ATTACK!", Toast.LENGTH_SHORT).show();
      }
    }
  }

  private void authenticate() {
    try {
      createKeyForTimeout();
    }
    catch (Exception e) {
      Toast.makeText(this, "Could not create the key", Toast.LENGTH_LONG).show();
      Log.e(getClass().getSimpleName(), "Exception creating key", e);
      return;
    }

    if (needsAuth(false)) {
      Intent i=
        mgr.createConfirmDeviceCredentialIntent("title", "description");

      if (i==null) {
        Toast.makeText(this, "No authentication required!",
          Toast.LENGTH_SHORT).show();
      }
      else {
        startActivityForResult(i, REQUEST_CODE);
      }
    }
  }

  private void createKeyForTimeout() throws Exception {
    KeyStore.Entry entry=ks.getEntry(KEY_NAME, null);

    if (entry==null) {
      KeyGenParameterSpec spec=
        new KeyGenParameterSpec.Builder(KEY_NAME,
          KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
          .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
          .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
          .setUserAuthenticationRequired(true)
          .setUserAuthenticationValidityDurationSeconds(TIMEOUT_SECONDS)
          .build();

      KeyGenerator keygen=
        KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE);

      keygen.init(spec);
      keygen.generateKey();
    }
  }

  private boolean needsAuth(boolean isRecheck) {
    boolean result=false;

    try {
      SecretKey secretKey=(SecretKey)ks.getKey(KEY_NAME, null);
      Cipher cipher=Cipher.getInstance("AES/CBC/PKCS7Padding");

      cipher.init(Cipher.ENCRYPT_MODE, secretKey);
      cipher.doFinal(POINTLESS_DATA);

      if (!isRecheck) {
        Toast.makeText(this, "Already authenticated!", Toast.LENGTH_LONG).show();
      }
    }
    catch (UserNotAuthenticatedException e) {
      result=true;
    }
    catch (KeyPermanentlyInvalidatedException e) {
      Toast.makeText(this, "You reset the lock screen!",
        Toast.LENGTH_LONG).show();
    }
    catch (Exception e) {
      Toast.makeText(this, "Could not validate the key", Toast.LENGTH_LONG).show();
      Log.e(getClass().getSimpleName(), "Exception validating key", e);
    }

    return result;
  }

  private void isInsideSecureHardware() {
    try {
      createKeyForTimeout();
    }
    catch (Exception e) {
      Toast.makeText(this, "Could not create the key", Toast.LENGTH_LONG).show();
      Log.e(getClass().getSimpleName(), "Exception creating key", e);
      return;
    }

    try {
      SecretKey key=(SecretKey)ks.getKey(KEY_NAME, null);
      KeyInfo info=
        (KeyInfo)SecretKeyFactory.getInstance(key.getAlgorithm(), KEYSTORE)
          .getKeySpec(key, KeyInfo.class);

      if (info.isInsideSecureHardware()) {
        Toast.makeText(this, "Key is inside secure hardware", Toast.LENGTH_LONG).show();
      }
      else {
        Toast.makeText(this, "Key is only secured by software", Toast.LENGTH_LONG).show();
      }
    }
    catch (Exception e) {
      Toast.makeText(this, "Well, *that* didn't work...", Toast.LENGTH_LONG).show();
      Log.e(getClass().getSimpleName(), "Exception getting key info", e);
    }
  }
}
