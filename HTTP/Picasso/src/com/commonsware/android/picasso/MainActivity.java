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

package com.commonsware.android.picasso;

import info.juanmendez.android.utils.Trace;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;

public class MainActivity extends SherlockFragmentActivity implements
		QuestionsFragment.Contract
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Trace.setTAG("picowso");
		SherlockListFragment f = (SherlockListFragment) getSupportFragmentManager()
				.findFragmentById(android.R.id.content);

		if (f == null)
		{

			getSupportFragmentManager().beginTransaction()
					.add(android.R.id.content, new QuestionsFragment())
					.commit();
		}
		else
		{
			Trace.warn("no fragment found", this);
		}
	}

	@Override
	public void showItem(Item item)
	{
		startActivity(new Intent(Intent.ACTION_VIEW,
				Uri.parse(item.link)));
	}
}
