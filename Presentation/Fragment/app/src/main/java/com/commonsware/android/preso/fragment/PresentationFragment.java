/***
  Copyright (c) 2013 CommonsWare, LLC
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

package com.commonsware.android.preso.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Presentation;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;

abstract public class PresentationFragment extends DialogFragment {
  private Display display=null;
  private Presentation preso=null;

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    if (preso == null) {
      return(super.onCreateDialog(savedInstanceState));
    }

    return(preso);
  }

  public void setDisplay(Context ctxt, Display display) {
    if (display == null) {
      preso=null;
    }
    else {
      preso=new Presentation(ctxt, display, getTheme());
    }

    this.display=display;
  }

  public Display getDisplay() {
    return(display);
  }

  protected Context getContext() {
    if (preso != null) {
      return(preso.getContext());
    }

    return(getActivity());
  }
}
