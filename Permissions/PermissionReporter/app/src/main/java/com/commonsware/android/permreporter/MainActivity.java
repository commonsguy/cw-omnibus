/***
  Copyright (c) 2012-2015 CommonsWare, LLC
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

package com.commonsware.android.permreporter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends FragmentActivity  {
  private Observable<PermissionRoster> observable;
  private Disposable sub;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    final ViewPager pager=findViewById(R.id.pager);

    observable=(Observable<PermissionRoster>)getLastCustomNonConfigurationInstance();

    if (observable==null) {
      observable=Observable
        .create(new PermissionSource(this))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .cache();
    }

    sub=observable.subscribe(new Consumer<PermissionRoster>() {
        @Override
        public void accept(PermissionRoster roster) throws Exception {
          pager.setAdapter(new PermissionTabAdapter(MainActivity.this,
            getSupportFragmentManager(), roster));
        }
      }, new Consumer<Throwable>() {
        @Override
        public void accept(Throwable error) throws Exception {
          Toast
            .makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG)
            .show();
          Log.e(getClass().getSimpleName(), "Exception processing request",
            error);
        }
      });
  }

  public Object onRetainCustomNonConfigurationInstance() {
    return(observable);
  }

  @Override
  protected void onDestroy() {
    sub.dispose();

    super.onDestroy();
  }

  private static class PermissionSource implements ObservableOnSubscribe<PermissionRoster> {
    private Context ctxt;

    private PermissionSource(Context ctxt) {
      this.ctxt=ctxt.getApplicationContext();
    }

    @Override
    public void subscribe(ObservableEmitter<PermissionRoster> emitter)
      throws Exception {
      PackageManager pm=ctxt.getPackageManager();
      final PermissionRoster result=new PermissionRoster();

      addPermissionsFromGroup(pm, null, result);

      for (PermissionGroupInfo group :
        pm.getAllPermissionGroups(0)) {
        addPermissionsFromGroup(pm, group.name, result);
      }

      emitter.onNext(result);
      emitter.onComplete();
    }

    private void addPermissionsFromGroup(PackageManager pm,
                                         String groupName,
                                         PermissionRoster result)
      throws PackageManager.NameNotFoundException {
      for (PermissionInfo info :
        pm.queryPermissionsByGroup(groupName, 0)) {
        int coreBits=
          info.protectionLevel &
            PermissionInfo.PROTECTION_MASK_BASE;

        switch (coreBits) {
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
  }
}