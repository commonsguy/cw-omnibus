/***
 Copyright (c) 2015 CommonsWare, LLC
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

package com.commonsware.android.fsendermnc;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.service.chooser.ChooserTarget;
import android.service.chooser.ChooserTargetService;
import java.util.ArrayList;
import java.util.List;

public class CTService extends ChooserTargetService {
  private String titleTemplate;

  @Override
  public void onCreate() {
    super.onCreate();

    titleTemplate=getString(R.string.title_template);
  }

  @Override
  public List<ChooserTarget> onGetChooserTargets(ComponentName sendTarget,
                                                 IntentFilter matchedFilter) {
    ArrayList<ChooserTarget> result=new ArrayList<ChooserTarget>();

    for (int i=1;i<=6;i++) {
      result.add(buildTarget(i));
    }

    return(result);
  }

  private ChooserTarget buildTarget(int targetId) {
    String title=String.format(titleTemplate, targetId);
    int iconId=getResources().getIdentifier("ic_share" + targetId,
        "drawable", getPackageName());
    Icon icon=Icon.createWithResource(this, iconId);
    float score=1.0f-((float)targetId/40);
    ComponentName cn=new ComponentName(this, FauxSender.class);
    Bundle extras=new Bundle();

    extras.putInt(FauxSender.EXTRA_TARGET_ID, targetId);

    return(new ChooserTarget(title, icon, score, cn, extras));
  }
}
