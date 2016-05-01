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
    https://commonsware.com/Android
*/

package com.commonsware.android.syssvc.settings;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SettingsSetter extends ListActivity {
  private static Map<Integer,String> menuActivities=new HashMap<Integer,String>();
  private static List<BooleanSetting> settings=new ArrayList<BooleanSetting>();
  
  static {
    menuActivities.put(R.id.app,
                       Settings.ACTION_APPLICATION_SETTINGS);
    menuActivities.put(R.id.security,
                       Settings.ACTION_SECURITY_SETTINGS);
    menuActivities.put(R.id.wireless,
                       Settings.ACTION_WIRELESS_SETTINGS);
    menuActivities.put(R.id.all,
                       Settings.ACTION_SETTINGS);
    
    settings.add(new BooleanSetting(Settings.System.INSTALL_NON_MARKET_APPS,
                                      "Allow non-Market app installs",
                                      true));
    settings.add(new BooleanSetting(Settings.System.HAPTIC_FEEDBACK_ENABLED,
                                      "Use haptic feedback",
                                      false));
    settings.add(new BooleanSetting(Settings.System.ACCELEROMETER_ROTATION,
                                      "Rotate based on accelerometer",
                                      false));
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
  
    getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    setListAdapter(new ArrayAdapter<BooleanSetting>(this,
                                    android.R.layout.simple_list_item_multiple_choice,
                                    settings));
    
    ContentResolver cr=getContentResolver();
    
    for (int i=0;i<settings.size();i++) {
      BooleanSetting s=settings.get(i);
      
      getListView().setItemChecked(i, s.isChecked(cr));
    }
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.option, menu);

    return(super.onCreateOptionsMenu(menu));
  }
  

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    String activity=menuActivities.get(item.getItemId());
    
    if (activity!=null) {
      startActivity(new Intent(activity));
      
      return(true);
    }
    
    return(super.onOptionsItemSelected(item));
  }
  
  @Override
  protected void onListItemClick(ListView l, View v,
                                 int position, long id) {
    super.onListItemClick(l, v, position, id);
    
    BooleanSetting s=settings.get(position);
    
    s.setChecked(getContentResolver(),
                 l.isItemChecked(position));
  }
  
  static class BooleanSetting {
    String key;
    String displayName;
    boolean isSecure=false;
    
    BooleanSetting(String key, String displayName) {
      this(key, displayName, false);
    }
    
    BooleanSetting(String key, String displayName,
                   boolean isSecure) {
      this.key=key;
      this.displayName=displayName;
      this.isSecure=isSecure;
    }
    
    @Override
    public String toString() {
      return(displayName);
    }
    
    boolean isChecked(ContentResolver cr) {
      try {
        int value=0;
        
        if (isSecure) {
          value=Settings.Secure.getInt(cr, key);
        }
        else {
          value=Settings.System.getInt(cr, key);
        }
        
        return(value!=0);
      }
      catch (Settings.SettingNotFoundException e) {
        Log.e("SettingsSetter", e.getMessage());
      }
      
      return(false);
    }
    
    void setChecked(ContentResolver cr, boolean value) {
      try {
        if (isSecure) {
          Settings.Secure.putInt(cr, key, (value ? 1 : 0));
        }
        else {
          Settings.System.putInt(cr, key, (value ? 1 : 0));
        }
      }
      catch (Throwable t) {
        Log.e("SettingsSetter", "Exception in setChecked()", t);
      }
    }
  }
}