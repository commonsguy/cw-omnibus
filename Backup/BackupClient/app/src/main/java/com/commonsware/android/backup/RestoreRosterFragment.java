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

package com.commonsware.android.backup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class RestoreRosterFragment extends ListFragment
  implements Callback {
  private static final String URL_BACKUPS=
    BuildConfig.URL_SERVER+"/api/backups";
  private ArrayAdapter<BackupMetadata> adapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setRetainInstance(true);
  }

  @Override
  public void onStart() {
    super.onStart();

    EventBus.getDefault().register(this);
  }

  @Override
  public void onStop() {
    EventBus.getDefault().unregister(this);

    super.onStop();
  }

  @Override
  public void onViewCreated(View view,
                            Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    Request request=new Request.Builder()
      .url(URL_BACKUPS)
      .build();

    BackupService.OKHTTP_CLIENT.newCall(request).enqueue(this);
  }

  @Override
  public void onFailure(Request request, IOException e) {
    Toast.makeText(getActivity(), R.string.msg_roster_failure,
      Toast.LENGTH_LONG).show();
    Log.e(getClass().getSimpleName(),
      "Exception retrieving backup roster", e);
  }

  @Override
  public void onResponse(Response response) throws IOException {
    Gson gson=new GsonBuilder()
      .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ")
      .create();

    Type listType=new TypeToken<List<BackupMetadata>>() {}.getType();

    EventBus
      .getDefault()
      .post(
        gson.fromJson(response.body().charStream(), listType));
  }

  @Override
  public void onListItemClick(ListView l, View v, int position,
                              long id) {
    String url=
      BuildConfig.URL_SERVER+adapter.getItem(position).dataset;
    Intent i=
      new Intent(getActivity(), RestoreProgressActivity.class)
        .setData(Uri.parse(url))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|
                  Intent.FLAG_ACTIVITY_CLEAR_TASK|
                  Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

    startActivity(i);
  }

  @Subscribe(threadMode =ThreadMode.MAIN)
  public void onEventMainThread(List<BackupMetadata> roster) {
    adapter=new ArrayAdapter<BackupMetadata>(getActivity(),
      android.R.layout.simple_list_item_1, roster);

    setListAdapter(adapter);
  }
}
