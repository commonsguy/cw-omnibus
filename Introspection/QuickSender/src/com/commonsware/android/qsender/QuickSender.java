/***
  Copyright (c) 2008-2012 CommonsWare, LLC
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

package com.commonsware.android.qsender;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

public class QuickSender extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	public void save(View v)
	{
		Intent shortcutIntent = new Intent(Intent.ACTION_SEND);
		TextView addr = (TextView) findViewById(R.id.addr);
		TextView subject = (TextView) findViewById(R.id.subject);
		TextView body = (TextView) findViewById(R.id.body);
		TextView name = (TextView) findViewById(R.id.name);

		if (!TextUtils.isEmpty(addr.getText()))
		{
			shortcutIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { addr.getText()
					.toString() });
		}

		if (!TextUtils.isEmpty(subject.getText()))
		{
			shortcutIntent.putExtra(Intent.EXTRA_SUBJECT, subject.getText()
					.toString());
		}

		if (!TextUtils.isEmpty(body.getText()))
		{
			shortcutIntent.putExtra(Intent.EXTRA_TEXT, body.getText().toString());
		}

		shortcutIntent.setType("text/plain");

		Intent resultIntent = new Intent();

		resultIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		resultIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name.getText().toString());
		resultIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
				Intent.ShortcutIconResource.fromContext(this, R.drawable.icon));

		setResult(RESULT_OK, resultIntent);
		finish();
	}
}