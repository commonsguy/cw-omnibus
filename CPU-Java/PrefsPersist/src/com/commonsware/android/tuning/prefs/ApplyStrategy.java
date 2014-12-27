/***
	Copyright (c) 2008-2011 CommonsWare, LLC
	Licensed under the Apache License, Version 2.0 (the "License"); you may not
	use this file except in compliance with the License. You may obtain	a copy
	of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
	by applicable law or agreed to in writing, software distributed under the
	License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
	OF ANY KIND, either express or implied. See the License for the specific
	language governing permissions and limitations under the License.
	
	From _Tuning Android Applications_
		http://commonsware.com/AndTuning
 */

package com.commonsware.android.tuning.prefs;

import android.annotation.TargetApi;
import android.content.SharedPreferences.Editor;
import android.os.Build;

public class ApplyStrategy extends AbstractPrefsPersistStrategy
{

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	void persistAsync(Editor editor)
	{
		editor.apply();
	}
}
