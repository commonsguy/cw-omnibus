/***
  Copyright (c) 2012 CommonsWare, LLC
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

package com.commonsware.android.titleprog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends Activity
{
	Button btn;
	boolean show_pb;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		
		btn = (Button) findViewById(R.id.btn);
		btn.setOnClickListener( new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				
				setProgressBarIndeterminateVisibility( show_pb = !show_pb );
			}
		} );
		
		setProgressBarIndeterminateVisibility( show_pb = false);
		
	}
}
