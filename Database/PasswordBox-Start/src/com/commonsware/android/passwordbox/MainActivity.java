/***
  Copyright (c) 2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Android Development_
    http://commonsware.com/Android
 */

package com.commonsware.android.passwordbox;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
  private RosterFragment roster=null;
  private PassphraseFragment passphrase=null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    passphrase=
        (PassphraseFragment)getFragmentManager().findFragmentById(R.id.passphrase);
    roster=
        (RosterFragment)getFragmentManager().findFragmentById(R.id.roster);
  }

  void showPassphrase() {
    boolean needsClear=false;

    if (passphrase == null) {
      passphrase=new PassphraseFragment();
    }
    else {
      needsClear=true;
    }

    if (!passphrase.isVisible()) {
      getFragmentManager().beginTransaction()
                                 .addToBackStack(null)
                                 .replace(R.id.passphrase, passphrase)
                                 .commit();
    }

    if (needsClear) {
      findViewById(android.R.id.content).post(new Runnable() {
        public void run() {
          passphrase.clear();
        }
      });
    }
  }

  void showPassphrase(final int id, final String _title,
                      final String _passphrase) {
    showPassphrase();

    findViewById(android.R.id.content).post(new Runnable() {
      public void run() {
        passphrase.populate(id, _title, _passphrase);
      }
    });
  }

  void savePassphrase(int id, String title, String passphrase) {
    roster.savePassphrase(id, title, passphrase);
    getFragmentManager().popBackStack();
  }
}
