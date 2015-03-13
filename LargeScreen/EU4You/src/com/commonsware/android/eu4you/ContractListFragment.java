/***
  Copyright (c) 2013 Jake Wharton
  Portions Copyright (c) 2013 CommonsWare, LLC
  
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

// derived from https://gist.github.com/JakeWharton/2621173

package com.commonsware.android.eu4you;

import android.app.Activity;
import android.app.ListFragment;

public class ContractListFragment<T> extends ListFragment {
  private T contract;

  @SuppressWarnings("unchecked")
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    try {
      contract=(T)activity;
    }
    catch (ClassCastException e) {
      throw new IllegalStateException(activity.getClass()
                                              .getSimpleName()
          + " does not implement contract interface for "
          + getClass().getSimpleName(), e);
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    
    contract=null;
  }

  public final T getContract() {
    return(contract);
  }
}
