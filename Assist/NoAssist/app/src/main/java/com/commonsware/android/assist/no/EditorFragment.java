/***
  Copyright (c) 2012-14 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
 */

package com.commonsware.android.assist.no;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class EditorFragment extends Fragment {
  private static final String KEY_POSITION="position";

  static EditorFragment newInstance(int position) {
    EditorFragment frag=new EditorFragment();
    Bundle args=new Bundle();

    args.putInt(KEY_POSITION, position);
    frag.setArguments(args);

    return(frag);
  }

  static String getTitle(Context ctxt, int position) {
    return(String.format(ctxt.getString(R.string.hint), position + 1));
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    int position=getArguments().getInt(KEY_POSITION, -1);
    View result;

    if (position==2) {
      ViewGroup doctorNo=new NoAssistFrameLayout(getActivity());
      inflater.inflate(R.layout.editor, doctorNo);
      result=doctorNo;
    }
    else {
      result=inflater.inflate(R.layout.editor, container, false);
    }

    EditText editor=(EditText)result.findViewById(R.id.editor);

    editor.setHint(getTitle(getActivity(), position));

    if (position==1) {
      editor.
        setTransformationMethod(PasswordTransformationMethod.
          getInstance());
    }

    return(result);
  }
}