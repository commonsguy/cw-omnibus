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

package com.commonsware.android.key;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;
import com.mtramin.rxfingerprint.EncryptionMethod;
import com.mtramin.rxfingerprint.RxFingerprint;
import com.mtramin.rxfingerprint.data.FingerprintDecryptionResult;
import com.mtramin.rxfingerprint.data.FingerprintEncryptionResult;
import com.mtramin.rxfingerprint.data.FingerprintResult;
import java.security.SecureRandom;
import io.reactivex.disposables.Disposable;

public class MainActivity extends Activity {
  private static final String BASE36_SYMBOLS="abcdefghijklmnopqrstuvwxyz0123456789";
  private static final String KEY_NAME="sooper-sekrit-key";
  private Disposable disposable;
  private ImageButton button;
  private Drawable on;
  private Drawable off;
  private final SecureRandom rng=new SecureRandom();
  private final char[] passphrase=generatePassphrase();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    off=DrawableCompat.wrap(VectorDrawableCompat.create(getResources(),
      R.drawable.ic_fingerprint_black_24dp, null));
    off.setTint(getResources().getColor(android.R.color.black, null));

    on=DrawableCompat.wrap(VectorDrawableCompat.create(getResources(),
      R.drawable.ic_fingerprint_black_24dp, null));
    on.setTint(getResources().getColor(R.color.primary, null));

    button=findViewById(R.id.fingerprint);
    button.setImageDrawable(off);
    button.setOnClickListener(view -> doTheWork());
  }

  @Override
  protected void onDestroy() {
    unsub();
    super.onDestroy();
  }

  private void unsub() {
    if (disposable!=null) {
      disposable.dispose();
      disposable=null;
    }
  }

  private void doTheWork() {
    unsub();

    if (RxFingerprint.isAvailable(this)) {
      disposable=RxFingerprint.encrypt(EncryptionMethod.RSA, this, KEY_NAME,
        new String(passphrase))
        .subscribe(this::onEncResult,
          t -> {
            Log.e(getClass().getSimpleName(), "Exception authenticating", t);
            button.setImageDrawable(off);
          });
    }
    else {
      Toast.makeText(this, R.string.msg_not_available, Toast.LENGTH_LONG).show();
    }
  }

  private void onEncResult(FingerprintEncryptionResult encResult) {
    if (encResult.getResult()==FingerprintResult.AUTHENTICATED) {
      button.setImageDrawable(on);
      unsub();

      String encryptedValue=encResult.getEncrypted();

      disposable=RxFingerprint.decrypt(EncryptionMethod.RSA, this, KEY_NAME,
        encryptedValue)
        .subscribe(this::onDecResult,
          t -> {
            Log.e(getClass().getSimpleName(), "Exception decrypting", t);
            button.setImageDrawable(off);
          });
    }
    else {
      Toast.makeText(this, "This was unexpected...", Toast.LENGTH_LONG).show();
    }
  }

  private void onDecResult(FingerprintDecryptionResult decResult) {
    String msg=getString(R.string.msg_not_possible);

    switch (decResult.getResult()) {
      case FAILED:
        msg=getString(R.string.msg_failed);
        button.setImageDrawable(off);
        unsub();
        break;
      case HELP:
        msg=decResult.getMessage();
        break;
      case AUTHENTICATED:
        button.setImageDrawable(off);
        unsub();

        if (decResult.getDecrypted().equals(new String(passphrase))) {
          msg=getString(R.string.msg_match);
        }
        else {
          msg=getString(R.string.msg_mismatch);
        }

        break;
    }

    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
  }

  private char[] generatePassphrase() {
    char[] result=new char[128];

    for (int i=0; i<result.length; i++) {
      result[i]=BASE36_SYMBOLS.charAt(rng.nextInt(BASE36_SYMBOLS.length()));
    }

    return result;
  }
}
