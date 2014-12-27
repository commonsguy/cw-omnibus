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
    http://commonsware.com/Android
 */

package com.commonsware.android.profile.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.UserManager;
import android.widget.Toast;

public class MainActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		UserManager mgr = (UserManager) getSystemService(USER_SERVICE);
		Bundle restrictions = mgr.getApplicationRestrictions(getPackageName());

		if (restrictions.keySet().size() > 0)
		{
			setContentView(R.layout.activity_main);

			RestrictionsFragment f = (RestrictionsFragment) getFragmentManager()
					.findFragmentById(R.id.contents);

			f.showRestrictions(restrictions);
		}
		else
		{
			Toast.makeText(this, R.string.no_restrictions, Toast.LENGTH_LONG)
					.show();
			finish();
		}
	}
}
