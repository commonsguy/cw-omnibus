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

package com.commonsware.android.auth.note;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.security.keystore.UserNotAuthenticatedException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import java.io.File;
import java.nio.charset.Charset;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends Activity {
  private static final String FILENAME="sekrits.bin";
  private static final String KEY_NAME="sooper-sekrit-key";
  private static final int TIMEOUT_SECONDS=60;
  private static final Charset UTF8=Charset.forName("UTF-8");
  private static final int REQUEST_SAVE=1337;
  private static final int REQUEST_LOAD=1338;
  private EditText textarea;
  private KeyguardManager mgr;
  private NoteRepository.Note note;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mgr=(KeyguardManager)getSystemService(KEYGUARD_SERVICE);

    if (mgr.isKeyguardSecure()) {
      textarea=findViewById(R.id.note);
      load();
    }
    else {
      Toast.makeText(this, R.string.insecure, Toast.LENGTH_LONG).show();
      finish();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.actions, menu);

    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId()==R.id.save) {
      save();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode,
                                  Intent data) {
    if (resultCode==RESULT_OK) {
      if (requestCode==REQUEST_SAVE) {
        save();
      }
      else if (requestCode==REQUEST_LOAD) {
        load();
      }
    }
    else {
      Toast.makeText(this, R.string.sorry, Toast.LENGTH_SHORT).show();
      finish();
    }
  }

  private void load() {
    final Context app=getApplicationContext();
    File encryptedFile=new File(getFilesDir(), FILENAME);

    RxPassphrase.get(encryptedFile, KEY_NAME, TIMEOUT_SECONDS)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .flatMap(chars -> NoteRepository.load(app, chars))
      .subscribe(this::onNoteReady,
        t -> {
          if (t instanceof UserNotAuthenticatedException) {
            requestAuth(REQUEST_LOAD);
          }
          else {
            Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(getString(R.string.app_name), "Exception loading encrypted file", t);
          }
      });
  }

  private void onNoteReady(NoteRepository.Note note) {
    this.note=note;
    textarea.setText(note.content);
  }

  private void onNoteSaved(NoteRepository.Note note) {
    Toast.makeText(this, R.string.saved, Toast.LENGTH_LONG).show();
    onNoteReady(note);
  }

  private void save() {
    Observable.just(textarea.getText().toString())
      .map(content -> NoteRepository.save(note, content))
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(this::onNoteSaved,
        t -> {
          if (t instanceof UserNotAuthenticatedException) {
            requestAuth(REQUEST_LOAD);
          }
          else {
            Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(getString(R.string.app_name), "Exception loading encrypted file", t);
          }
        });
  }

  private void requestAuth(int requestCode) {
    Intent i=
      mgr.createConfirmDeviceCredentialIntent("title", "description");

    if (i==null) {
      Toast.makeText(this, "No authentication required?!?",
        Toast.LENGTH_SHORT).show();
    }
    else {
      startActivityForResult(i, requestCode);
    }
  }
}
