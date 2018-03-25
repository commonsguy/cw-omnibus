/***
  Copyright (c) 2012-2015 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  Covered in detail in the book _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
 */

package com.commonsware.android.permreporter;

import android.content.pm.PermissionInfo;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PermissionListFragment extends ListFragment {
  private static final String ARG_PERMS="perms";

  static PermissionListFragment newInstance(ArrayList<PermissionInfo> perms) {
    PermissionListFragment frag=new PermissionListFragment();
    Bundle args=new Bundle();

    args.putParcelableArrayList(ARG_PERMS, perms);
    frag.setArguments(args);

    return(frag);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    ArrayList<PermissionInfo> perms=
      getArguments().getParcelableArrayList(ARG_PERMS);

    if (perms!=null && perms.size()>0) {
      Collections.sort(perms, new Comparator<PermissionInfo>() {
        @Override
        public int compare(PermissionInfo one, PermissionInfo two) {
          return (one.name.compareTo(two.name));
        }
      });

      setListAdapter(new PermissionAdapter(perms));
    }
    else {
      setListAdapter(new PermissionAdapter(new ArrayList<PermissionInfo>()));
      setEmptyText(getActivity().getString(R.string.msg_no_perms));
    }
  }

  private class PermissionAdapter extends ArrayAdapter<PermissionInfo> {
    PermissionAdapter(ArrayList<PermissionInfo> perms) {
      super(getActivity(), android.R.layout.simple_list_item_1, perms);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View result=super.getView(position, convertView, parent);
      TextView tv=(TextView)result.findViewById(android.R.id.text1);

      tv.setText(getItem(position).name);

      return(result);
    }
  }
}