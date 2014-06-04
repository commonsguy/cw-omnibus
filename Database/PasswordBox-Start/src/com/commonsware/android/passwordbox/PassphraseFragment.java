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

import android.app.Fragment;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class PassphraseFragment extends Fragment implements
    OnClickListener, OnCheckedChangeListener {
  private EditText passphrase=null;
  private EditText title=null;
  private int id=-1;

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    View result=inflater.inflate(R.layout.passphrase, container, false);

    result.findViewById(R.id.save).setOnClickListener(this);
    title=(EditText)result.findViewById(R.id.title);
    passphrase=(EditText)result.findViewById(R.id.passphrase);

    CompoundButton cb=
        (CompoundButton)result.findViewById(R.id.show_passphrase);

    cb.setOnCheckedChangeListener(this);

    return(result);
  }

  @Override
  public void onClick(View v) {
    getActivityContract().savePassphrase(id,
                                         title.getText().toString(),
                                         passphrase.getText()
                                                   .toString());
  }

  @Override
  public void onCheckedChanged(CompoundButton cb, boolean isChecked) {
    int start=passphrase.getSelectionStart();
    int end=passphrase.getSelectionEnd();

    if (isChecked) {
      passphrase.setTransformationMethod(null);
    }
    else {
      passphrase.setTransformationMethod(new PasswordTransformationMethod());
    }

    passphrase.setSelection(start, end);
  }

  void populate(int id, String _title, String _passphrase) {
    this.id=id;
    title.setText(_title);
    passphrase.setText(_passphrase);
  }

  void clear() {
    id=-1;
    title.setText("");
    passphrase.setText("");
  }

  private MainActivity getActivityContract() {
    return((MainActivity)getActivity());
  }
}
