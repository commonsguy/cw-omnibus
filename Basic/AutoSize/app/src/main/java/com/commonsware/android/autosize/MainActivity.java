package com.commonsware.android.autosize;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    EditText input=(EditText)findViewById(R.id.input);
    final TextView granular=(TextView)findViewById(R.id.granular);
    final TextView steps=(TextView)findViewById(R.id.steps);

    input.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1,
                                    int i2) {
        // unused
      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1,
                                int i2) {
        // unused
      }

      @Override
      public void afterTextChanged(Editable editable) {
        granular.setText(editable.toString());
        steps.setText(editable.toString());
      }
    });
  }
}
