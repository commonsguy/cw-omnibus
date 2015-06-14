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
    https://commonsware.com/Android
 */

package com.commonsware.android.async;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

abstract public class SimpleAsyncTask extends
    AsyncTask<Void, Void, Void> {
  abstract protected void doInBackground();

  abstract protected void onPostExecute();

  @Override
  final protected Void doInBackground(Void... unused) {
    doInBackground();

    return null;
  }

  @Override
  final protected void onPostExecute(Void unused) {
    onPostExecute();
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public SimpleAsyncTask execute() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
      return(this);
    }

    return (SimpleAsyncTask)(super.execute());
  }
}
