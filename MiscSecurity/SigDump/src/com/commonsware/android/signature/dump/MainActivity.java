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

package com.commonsware.android.signature.dump;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends Activity implements
    PackagesFragment.Contract {
  private PackageManager mgr=null;
  private SignatureFragment sigDisplay=null;
  private SlidingPaneLayout panes=null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mgr=getPackageManager();
    sigDisplay=
        (SignatureFragment)getFragmentManager().findFragmentById(R.id.log);

    panes=(SlidingPaneLayout)findViewById(R.id.panes);
    panes.openPane();
  }

  @Override
  public void onBackPressed() {
    if (panes.isOpen()) {
      super.onBackPressed();
    }
    else {
      panes.openPane();
    }
  }

  @Override
  public List<PackageInfo> getPackageList() {
    List<PackageInfo> result=
        mgr.getInstalledPackages(PackageManager.GET_SIGNATURES);

    Collections.sort(result, new Comparator<PackageInfo>() {
      @Override
      public int compare(final PackageInfo a, final PackageInfo b) {
        return(a.packageName.compareTo(b.packageName));
      }
    });

    return(result);
  }

  @Override
  public void onPackageSelected(PackageInfo pkgInfo) {
    Signature[] signatures=pkgInfo.signatures;
    byte[] raw=signatures[0].toByteArray();

    sigDisplay.show(raw);
    panes.closePane();

    File output=
        new File(getExternalFilesDir(null),
                 pkgInfo.packageName.replace('.', '_') + ".bin");

    new WriteThread(output, raw).start();
  }

  static class WriteThread extends Thread {
    private File output;
    private byte[] raw;

    WriteThread(File output, byte[] raw) {
      this.output=output;
      this.raw=raw;
    }

    @Override
    public void run() {
      if (output.exists()) {
        output.delete();
      }

      try {
        FileOutputStream fos=new FileOutputStream(output.getPath());

        fos.write(raw);
        fos.close();
      }
      catch (java.io.IOException e) {
        Log.e(getClass().getSimpleName(),
              "Exception in writing signature file", e);
      }
    }
  }
}
