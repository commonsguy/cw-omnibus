/***
  Copyright (c) 2008-2013 CommonsWare, LLC
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

package com.commonsware.android.abf;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class ActionBarFragmentActivity extends ActionBarActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		findViewById(android.R.id.content).post(new Runnable()
		{
			public void run()
			{
				if (getSupportFragmentManager().findFragmentById(R.id.fragment) == null)
				{
					getSupportFragmentManager().beginTransaction()
							.add(R.id.fragment, new ActionBarFragment())
							.commit();
				}
			}
		});
	}
}
