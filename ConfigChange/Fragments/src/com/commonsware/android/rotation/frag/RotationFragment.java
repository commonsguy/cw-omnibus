/***
  Copyright (c) 2012 CommonsWare, LLC
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

package com.commonsware.android.rotation.frag;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockFragment;

public class RotationFragment extends SherlockFragment implements
		View.OnClickListener
{
	static final int PICK_REQUEST = 1337;
	Uri contact = null;

	public Uri getContact()
	{
		return contact;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState)
	{
		/**
		 * Control whether a fragment instance is retained across Activity
		 * re-creation (such as from a configuration change). This can only be
		 * used with fragments not in the back stack. If set, the fragment
		 * lifecycle will be slightly different when an activity is recreated:
		 * 
		 * onDestroy() will not be called (but onDetach() still will be, because
		 * the fragment is being detached from its current activity).
		 * onCreate(Bundle) will not be called since the fragment is not being
		 * re-created. onAttach(Activity) and onActivityCreated(Bundle) will
		 * still be called.
		 */
		setRetainInstance(true);

		View result = inflater.inflate(R.layout.main, parent, false);

		result.findViewById(R.id.pick).setOnClickListener(this);

		View v = result.findViewById(R.id.view);

		v.setOnClickListener(this);
		v.setEnabled(contact != null);

		return (result);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == PICK_REQUEST)
		{
			if (resultCode == Activity.RESULT_OK)
			{
				contact = data.getData();
				getView().findViewById(R.id.view).setEnabled(true);
			}
		}
	}

	@Override
	public void onClick(View v)
	{
		if (v.getId() == R.id.pick)
		{
			pickContact(v);
		}
		else
		{
			viewContact(v);
		}
	}

	public void pickContact(View v)
	{
		Intent i = new Intent(Intent.ACTION_PICK,
				ContactsContract.Contacts.CONTENT_URI);

		startActivityForResult(i, PICK_REQUEST);
	}

	public void viewContact(View v)
	{
		startActivity(new Intent(Intent.ACTION_VIEW, contact));
	}
}
