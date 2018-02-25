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
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;
import com.mtramin.rxfingerprint.RxFingerprint;
import com.mtramin.rxfingerprint.data.FingerprintAuthenticationResult;
import io.reactivex.disposables.Disposable;

public class MainActivity extends Activity {
  private Disposable disposable;
  private ImageButton button;
  private Drawable on;
  private Drawable off;

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
    button.setOnClickListener(view -> authenticate());
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

  private void authenticate() {
    unsub();

    if (RxFingerprint.isAvailable(this)) {
      button.setImageDrawable(on);
      disposable=RxFingerprint.authenticate(this)
        .subscribe(this::onAuthResult,
          t -> {
            Log.e(getClass().getSimpleName(), "Exception authenticating", t);
            button.setImageDrawable(off);
          });
    }
    else {
      Toast.makeText(this, R.string.msg_not_available, Toast.LENGTH_LONG).show();
    }
  }

  private void onAuthResult(FingerprintAuthenticationResult authResult) {
    String msg=getString(R.string.msg_not_possible);

    switch (authResult.getResult()) {
      case FAILED:
        msg=getString(R.string.msg_failed);
        button.setImageDrawable(off);
        unsub();
        break;
      case HELP:
        msg=authResult.getMessage();
        break;
      case AUTHENTICATED:
        msg=getString(R.string.msg_authenticated);
        button.setImageDrawable(off);
        unsub();
        break;
    }

    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
  }
}
