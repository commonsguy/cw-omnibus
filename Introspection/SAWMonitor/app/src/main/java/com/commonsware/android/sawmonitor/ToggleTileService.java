/***
 Copyright (c) 2016 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.sawmonitor;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.preference.PreferenceManager;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

@TargetApi(Build.VERSION_CODES.N)
public class ToggleTileService extends TileService {
  private SharedPreferences prefs;

  @Override
  public void onStartListening() {
    super.onStartListening();

    updateTile();
  }

  @Override
  public void onClick() {
    super.onClick();

    boolean isEnabled=getPrefs().getBoolean(MonitorApp.PREF_ENABLED, false);

    getPrefs()
      .edit()
      .putBoolean(MonitorApp.PREF_ENABLED, !isEnabled)
      .apply();
    updateTile();
  }

  private void updateTile() {
    Tile tile=getQsTile();

    if (tile!=null) {
      boolean isEnabled=getPrefs().getBoolean(MonitorApp.PREF_ENABLED, false);
      int state=isEnabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE;

      tile.setIcon(Icon.createWithResource(this,
        R.drawable.ic_new_releases_24dp));
      tile.setLabel(getString(R.string.app_name));
      tile.setState(state);
      tile.updateTile();
    }
  }

  private SharedPreferences getPrefs() {
    if (prefs==null) {
      prefs=PreferenceManager.getDefaultSharedPreferences(this);
    }

    return(prefs);
  }
}
