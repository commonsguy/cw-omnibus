/***
 Copyright (c) 2016 CommonsWare, LLC
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

package com.commonsware.android.documenttree;

import android.content.Intent;
import android.preference.Preference;

public class DocumentHelper extends TreeUriPreferenceHelper
  implements Preference.OnPreferenceClickListener {
  public DocumentHelper(Host host, Preference pref) {
    super(host, pref);
    pref.setOnPreferenceClickListener(this);
  }

  @Override
  protected String getUriKey() {
    return (pref.getKey());
  }

  @Override
  public boolean onPreferenceClick(Preference preference) {
    Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);

    host.startActivityForHelper(i, this);

    return (true);
  }
}
