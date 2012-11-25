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

package com.commonsware.android.hotkey;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements OnKeyListener {
  private EditText editor=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    editor=(EditText)findViewById(R.id.editor);
    editor.setOnKeyListener(this);
  }

  @Override
  public boolean onKey(View v, int keyCode, KeyEvent event) {
    Editable text=editor.getText();

    if (keyCode == KeyEvent.KEYCODE_TAB) {
      text.insert(editor.getSelectionStart(), "\t");
    }
    else if (event.isCtrlPressed()) {
      int rawStart=editor.getSelectionStart();
      int rawEnd=editor.getSelectionEnd();
      int selStart=(rawStart>rawEnd ? rawEnd : rawStart);
      int selEnd=(rawStart>rawEnd ? rawStart : rawEnd);
      
      switch (keyCode) {
        case KeyEvent.KEYCODE_T:
          Toast.makeText(this,
                         TextUtils.substring(text, selStart, selEnd),
                         Toast.LENGTH_LONG).show();

          return(true);
      }
    }

    return(false);
  }
}
