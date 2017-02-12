/***
  Copyright (c) 2008-2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain	a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  Covered in detail in the book _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
 */

package com.commonsware.android.eu4you2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CountriesFragment extends
    ContractListFragment<CountriesFragment.Contract> {
  static private final String STATE_CHECKED=
      "com.commonsware.android.eu4you.STATE_CHECKED";

  @Override
  public void onActivityCreated(Bundle state) {
    super.onActivityCreated(state);

    setListAdapter(new CountryAdapter());

    if (state != null) {
      int position=state.getInt(STATE_CHECKED, -1);

      if (position > -1) {
        getListView().setItemChecked(position, true);
      }
    }
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    if (getContract().isPersistentSelection()) {
      getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
      l.setItemChecked(position, true);
    }
    else {
      getListView().setChoiceMode(ListView.CHOICE_MODE_NONE);
    }

    getContract().onCountrySelected(Country.EU.get(position));
  }

  @Override
  public void onSaveInstanceState(Bundle state) {
    super.onSaveInstanceState(state);

    if (getView() != null) {
      state.putInt(STATE_CHECKED,
                   getListView().getCheckedItemPosition());
    }
  }

  class CountryAdapter extends ArrayAdapter<Country> {
    CountryAdapter() {
      super(getActivity(), R.layout.row, R.id.name, Country.EU);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      CountryViewHolder wrapper=null;

      if (convertView == null) {
        convertView=
            LayoutInflater.from(getActivity()).inflate(R.layout.row,
                                                       null);
        wrapper=new CountryViewHolder(convertView);
        convertView.setTag(wrapper);
      }
      else {
        wrapper=(CountryViewHolder)convertView.getTag();
      }

      wrapper.populateFrom(getItem(position));

      return(convertView);
    }
  }

  interface Contract {
    void onCountrySelected(Country c);

    boolean isPersistentSelection();
  }
}
