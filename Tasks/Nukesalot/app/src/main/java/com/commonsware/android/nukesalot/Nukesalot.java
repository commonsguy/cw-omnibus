/***
  Copyright (c) 2008-2012 CommonsWare, LLC
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

package com.commonsware.android.nukesalot;

import android.app.ActivityManager;
import android.app.ListActivity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class Nukesalot extends ListActivity {
  private AppAdapter adapter;
  private ActivityManager am;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    am=(ActivityManager)getSystemService(ACTIVITY_SERVICE);
  }

  @Override
  public void onResume() {
    super.onResume();

    adapter=buildAdapter();
    setListAdapter(adapter);
  }
  
  @Override
  protected void onListItemClick(ListView l, View v,
                                 int position, long id) {
    ApplicationInfo app=adapter.getItem(position);

    am.killBackgroundProcesses(app.packageName);
    adapter=buildAdapter();
    setListAdapter(adapter);
  }

  private AppAdapter buildAdapter() {
    HashSet<String> runningPackages=new HashSet<String>();

    for (ActivityManager.RunningAppProcessInfo proc :
        am.getRunningAppProcesses()) {
      for (String pkg : proc.pkgList) {
        runningPackages.add(pkg);
      }
    }

    PackageManager pm=getPackageManager();
    List<ApplicationInfo> apps=new ArrayList<ApplicationInfo>();

    for (ApplicationInfo app : pm.getInstalledApplications(0)) {
      if (runningPackages.contains(app.packageName)) {
        apps.add(app);
      }
    }

    Collections.sort(apps,
        new ApplicationInfo.DisplayNameComparator(pm));

    return(new AppAdapter(pm, apps));
  }

  class AppAdapter extends ArrayAdapter<ApplicationInfo> {
    private PackageManager pm=null;
    
    AppAdapter(PackageManager pm, List<ApplicationInfo> apps) {
      super(Nukesalot.this, R.layout.row, apps);
      this.pm=pm;
    }
    
    @Override
    public View getView(int position, View convertView,
                          ViewGroup parent) {
      if (convertView==null) {
        convertView=newView(parent);
      }
      
      bindView(position, convertView);
      
      return(convertView);
    }
    
    private View newView(ViewGroup parent) {
      return(getLayoutInflater().inflate(R.layout.row, parent, false));
    }
    
    private void bindView(int position, View row) {
      TextView label=(TextView)row.findViewById(R.id.label);
      
      label.setText(getItem(position).loadLabel(pm));
      
      ImageView icon=(ImageView)row.findViewById(R.id.icon);
      
      icon.setImageDrawable(getItem(position).loadIcon(pm));
    }
  }
}