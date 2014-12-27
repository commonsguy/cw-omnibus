/***
  Copyright (c) 2013 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain	a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
	
  From _The Busy Coder's Guide to Android Development_
    http://commonsware.com/Android
 */

package com.commonsware.android.sensor.monitor;

import android.hardware.Sensor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import java.util.List;

public class SensorsFragment extends
		ContractListFragment<SensorsFragment.Contract>
{
	static private final String STATE_CHECKED = "com.commonsware.android.sensor.monitor.STATE_CHECKED";
	private SensorListAdapter adapter = null;

	@Override
	public void onActivityCreated(Bundle state)
	{
		super.onActivityCreated(state);

		/**
		 * as a ListFragment, this takes care of setting an adapter which
		 * automatically fills in the list of sensors via ContractListFragment.getContract();
		 */
		adapter = new SensorListAdapter(this);
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		setListAdapter(adapter);

		if (state != null)
		{
			//OJO
			int position = state.getInt(STATE_CHECKED, -1);

			if (position > -1)
			{
				getListView().setItemChecked(position, true);
				getContract().onSensorSelected(adapter.getItem(position));
			}
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle state)
	{
		super.onSaveInstanceState(state);

		//OJO
		state.putInt(STATE_CHECKED, getListView().getCheckedItemPosition());
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		l.setItemChecked(position, true);

		getContract().onSensorSelected(adapter.getItem(position));
	}

	interface Contract
	{
		void onSensorSelected(Sensor s);

		List<Sensor> getSensorList();
	}
}
