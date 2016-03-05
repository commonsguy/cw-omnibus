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

package com.commonsware.android.profile.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.RestrictionEntry;
import android.os.Bundle;
import java.util.ArrayList;

public class RestrictionEntriesReceiver extends BroadcastReceiver {
  static final String RESTRICTION_BOOLEAN="bool";
  static final String RESTRICTION_CHOICE="choice";
  static final String RESTRICTION_MULTI="multi";

  @Override
  public void onReceive(Context ctxt, Intent intent) {
    Bundle current=
        (Bundle)intent.getParcelableExtra(Intent.EXTRA_RESTRICTIONS_BUNDLE);
    ArrayList<RestrictionEntry> restrictions=
        new ArrayList<RestrictionEntry>();

    restrictions.add(buildBooleanRestriction(ctxt, current));
    restrictions.add(buildChoiceRestriction(ctxt, current));
    restrictions.add(buildMultiSelectRestriction(ctxt, current));

    Bundle result=new Bundle();

    result.putParcelableArrayList(Intent.EXTRA_RESTRICTIONS_LIST,
                                  restrictions);

    setResultExtras(result);
  }

  private RestrictionEntry buildBooleanRestriction(Context ctxt,
                                                   Bundle current) {
    RestrictionEntry entry=
        new RestrictionEntry(RESTRICTION_BOOLEAN,
                             current.getBoolean(RESTRICTION_BOOLEAN,
                                                false));

    entry.setTitle(ctxt.getString(R.string.boolean_restriction_title));
    entry.setDescription(ctxt.getString(R.string.boolean_restriction_desc));

    return(entry);
  }

  private RestrictionEntry buildChoiceRestriction(Context ctxt,
                                                  Bundle current) {
    RestrictionEntry entry=
        new RestrictionEntry(RESTRICTION_CHOICE,
                             current.getString(RESTRICTION_CHOICE));

    entry.setTitle(ctxt.getString(R.string.choice_restriction_title));
    entry.setChoiceEntries(ctxt, R.array.display_values);
    entry.setChoiceValues(ctxt, R.array.restriction_values);

    return(entry);
  }

  private RestrictionEntry buildMultiSelectRestriction(Context ctxt,
                                                       Bundle current) {
    RestrictionEntry entry=
        new RestrictionEntry(RESTRICTION_MULTI,
                             current.getStringArray(RESTRICTION_MULTI));

    entry.setTitle("A Multi-Select Restriction");
    entry.setChoiceEntries(ctxt, R.array.display_values);
    entry.setChoiceValues(ctxt, R.array.restriction_values);

    return(entry);
  }
}
