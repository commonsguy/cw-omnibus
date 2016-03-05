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

package com.commonsware.android.profile.device;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;

public class RestrictionsFragment extends ListFragment {
  public void showRestrictions(Bundle restrictions) {
    setListAdapter(new RestrictionsAdapter(restrictions));
  }

  class RestrictionsAdapter extends ArrayAdapter<String> {
    Bundle restrictions;

    RestrictionsAdapter(Bundle restrictions) {
      super(getActivity(), android.R.layout.simple_list_item_1,
            new ArrayList<String>());

      ArrayList<String> keys=
          new ArrayList<String>(restrictions.keySet());

      Collections.sort(keys);
      addAll(keys);

      this.restrictions=restrictions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      TextView row=
          (TextView)super.getView(position, convertView, parent);
      int icon=
          restrictions.getBoolean(getItem(position))
              ? R.drawable.ic_true : R.drawable.ic_false;

      row.setCompoundDrawablesWithIntrinsicBounds(0, 0, icon, 0);

      return(row);
    }
  }
}
