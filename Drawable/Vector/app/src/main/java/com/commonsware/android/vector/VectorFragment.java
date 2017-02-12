/***
 Copyright (c) 2016 CommonsWare, LLC
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

package com.commonsware.android.vector;

import android.app.ListFragment;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class VectorFragment extends ListFragment {
  private static final Integer[] VECTORS={
    R.drawable.ic_account_circle,
    R.drawable.ic_check_circle_24px,
    R.drawable.ic_corp_badge,
    R.drawable.ic_corp_icon_badge,
    R.drawable.ic_corp_statusbar_icon,
    R.drawable.ic_eject_24dp,
    R.drawable.ic_expand_more_48dp,
    R.drawable.ic_folder_24dp,
    R.drawable.ic_more_items,
    R.drawable.ic_perm_device_info,
    R.drawable.ic_sd_card_48dp,
    R.drawable.ic_settings_24dp,
    R.drawable.ic_storage_48dp,
    R.drawable.ic_usb_48dp
  };

  @Override
  public void onViewCreated(View view,
                            Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    setListAdapter(new VectorAdapter());
  }

  void applyIcon(ImageView icon, int resourceId) {
    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
      icon.setImageResource(resourceId);
    }
  }

  class VectorAdapter extends ArrayAdapter<Integer> {
    VectorAdapter() {
      super(getActivity(), R.layout.row, R.id.title, VECTORS);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View row=super.getView(position, convertView, parent);
      ImageView icon=(ImageView)row.findViewById(R.id.icon);
      TextView title=(TextView)row.findViewById(R.id.title);

      applyIcon(icon, getItem(position));
      title.setText(getResources().getResourceName(getItem(position)));

      return(row);
    }
  }
}
