/***
  Copyright (c) 2008-2017 CommonsWare, LLC
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

package com.commonsware.android.appwrangler;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ChangedPackages;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Collections;
import java.util.List;

public class MainActivity extends ListActivity {
  private static final String MIME_TYPE_APK=
    "application/vnd.android.package-archive";
  private static final int REQUEST_OPEN=1337;

  private AppAdapter adapter;
  private PackageManager pm;
  private int lastPackageSequenceNumber=-1;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    pm=getPackageManager();
  }

  @Override
  protected void onResume() {
    super.onResume();

    refresh(false);
  }

  @Override
  protected void onListItemClick(ListView l, View v,
                                 int position, long id) {
    ApplicationInfo app=adapter.getItem(position);
    Intent i=new Intent(Intent.ACTION_UNINSTALL_PACKAGE)
      .setData(Uri.parse("package:"+app.packageName));

    startActivity(i);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.actions, menu);

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()) {
      case R.id.install:
        Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT)
          .setType(MIME_TYPE_APK)
          .addCategory(Intent.CATEGORY_OPENABLE);

        startActivityForResult(i, REQUEST_OPEN);
        return(true);

      case R.id.refresh:
        refresh(true);
        return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode,
                                  Intent data) {
    if (requestCode==REQUEST_OPEN && resultCode==RESULT_OK) {
      if (getPackageManager().canRequestPackageInstalls()) {
        Toast.makeText(this, R.string.msg_install_perm, Toast.LENGTH_LONG).show();
      }

      Intent i=new Intent(Intent.ACTION_INSTALL_PACKAGE)
        .setData(data.getData())
        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

      startActivity(i);
    }
  }

  private void populateList() {
    List<ApplicationInfo> apps=pm.getInstalledApplications(0);

    Collections.sort(apps,
      new ApplicationInfo.DisplayNameComparator(pm));

    adapter=new AppAdapter(apps);
    setListAdapter(adapter);
  }

  private void refresh(boolean toast) {
    if (lastPackageSequenceNumber==-1) {
      ChangedPackages delta=pm.getChangedPackages(0);

      lastPackageSequenceNumber=(delta==null) ? 0 : delta.getSequenceNumber();
      populateList();
    }
    else {
      ChangedPackages delta=pm.getChangedPackages(lastPackageSequenceNumber);

      if (delta!=null && delta.getSequenceNumber()>lastPackageSequenceNumber) {
        populateList();

        if (toast) {
          Toast.makeText(this, R.string.msg_refresh_ack,
            Toast.LENGTH_SHORT).show();
        }
      }
      else if (toast) {
        Toast.makeText(this, R.string.msg_refresh_nack,
          Toast.LENGTH_LONG).show();
      }
    }
  }

  private class AppAdapter extends ArrayAdapter<ApplicationInfo> {
    AppAdapter(List<ApplicationInfo> apps) {
      super(MainActivity.this, R.layout.row, apps);
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
      TextView label=row.findViewById(R.id.label);
      
      label.setText(getItem(position).loadLabel(pm));
      
      ImageView icon=row.findViewById(R.id.icon);
      
      icon.setImageDrawable(getItem(position).loadIcon(pm));
    }
  }
}