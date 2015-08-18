/***
  Copyright (c) 2012-2015 CommonsWare, LLC
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

package com.commonsware.android.permreporter;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import java.util.List;
import de.greenrobot.event.EventBus;
import io.karim.MaterialTabs;

public class MainActivity extends Activity  {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    ViewPager pager=(ViewPager)findViewById(R.id.pager);

    pager.setAdapter(new PermissionTabAdapter(this, getFragmentManager()));

    MaterialTabs tabs=(MaterialTabs)findViewById(R.id.tabs);
    tabs.setViewPager(pager);

    new PermissionLoadThread(this).start();
  }

  // inspired by https://stackoverflow.com/a/32063384/115145

  private static class PermissionLoadThread extends Thread {
    private final Context ctxt;
    private final PermissionRosterLoadedEvent result=
        new PermissionRosterLoadedEvent();

    PermissionLoadThread(Context ctxt) {
      this.ctxt=ctxt.getApplicationContext();
    }

    @Override
    public void run() {
      PackageManager pm=ctxt.getPackageManager();

      addPermissionsFromGroup(pm, null);

      for (PermissionGroupInfo group : pm.getAllPermissionGroups(0)) {
        addPermissionsFromGroup(pm, group.name);
      }

      EventBus.getDefault().postSticky(result);
    }

    private void addPermissionsFromGroup(PackageManager pm,
                                         String groupName) {
      try {
        for (PermissionInfo info :
            pm.queryPermissionsByGroup(groupName, 0)) {
          int coreBits=
              info.protectionLevel & PermissionInfo.PROTECTION_MASK_BASE;

          switch(coreBits) {
            case PermissionInfo.PROTECTION_NORMAL:
              result.add(PermissionType.NORMAL, info);
              break;

            case PermissionInfo.PROTECTION_DANGEROUS:
              result.add(PermissionType.DANGEROUS, info);
              break;

            case PermissionInfo.PROTECTION_SIGNATURE:
              result.add(PermissionType.SIGNATURE, info);
              break;

            default:
              result.add(PermissionType.OTHER, info);
              break;
          }
        }
      }
      catch (PackageManager.NameNotFoundException e) {
        throw new IllegalStateException("And you may ask yourself... how did I get here?");
      }
    }
  }
}