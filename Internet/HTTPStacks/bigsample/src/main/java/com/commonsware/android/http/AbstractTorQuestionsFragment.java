/***
 Copyright (c) 2013-2016 CommonsWare, LLC
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

package com.commonsware.android.http;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.commonsware.android.http.model.TorStatus;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import info.guardianproject.netcipher.hurl.OrbotInitializer;
import info.guardianproject.netcipher.hurl.StatusCallback;
import info.guardianproject.netcipher.hurl.StrongBuilder;

abstract public class AbstractTorQuestionsFragment extends AbstractQuestionsFragment
  implements OrbotInitializer.InstallCallback,
  AbstractTorStatusStrategy.TorStatusCallback {
  abstract protected TorStatusStrategy
  buildStatusStrategy() throws Exception;

  abstract protected void loadQuestions();

  private MenuItem orbotItem;
  private TorStatusStrategy strategy=null;
  private OrbotInitializer orbot;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setHasOptionsMenu(true);
    orbot=OrbotInitializer.get(getActivity());
  }

  @Override
  public void onViewCreated(View view,
                            Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    setEmptyText(getString(R.string.msg_orbot_not_installed));
    checkTor();
  }

  @Override
  public void onCreateOptionsMenu(Menu menu,
                                  MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);

    inflater.inflate(R.menu.actions, menu);
    orbotItem=menu.findItem(R.id.orbot);
    orbotItem.setVisible(!orbot.isInstalled());
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId()==R.id.orbot) {
      OrbotInitializer
        .get(getActivity())
        .installOrbot(getActivity());
      return (true);
    }

    return (super.onOptionsItemSelected(item));
  }

  @Override
  public void onInstalled() {
    Toast
      .makeText(getActivity(), R.string.msg_orbot_installed,
        Toast.LENGTH_SHORT)
      .show();

    cleanUpAfterInstall();
    checkTor();
  }

  @Override
  public void onInstallTimeout() {
    Toast
      .makeText(getActivity(), R.string.msg_orbot_not_installed,
        Toast.LENGTH_LONG)
      .show();
    cleanUpAfterInstall();
  }

  void cleanUpAfterInstall() {
    if (orbotItem!=null) {
      orbotItem.setVisible(false);
    }
  }

  @Override
  public void onLoaded(TorStatus status) {
    if (status.wasCallThroughTor) {
      loadQuestions();
    }
    else {
      Toast.makeText(getActivity(),
        "HTTP not going through Tor!",
        Toast.LENGTH_LONG).show();
    }
  }

  private void checkTor() {
    if (orbot.isInstalled()) {
      try {
        strategy=buildStatusStrategy();
        strategy.checkStatus(this);
      }
      catch (Exception e) {
        Toast.makeText(getActivity(),
          "Exception trying to build strategy",
          Toast.LENGTH_LONG).show();
        e.printStackTrace();
      }
    }
  }

  public abstract static class StrongBuilderCallbackBase<C>
    implements StrongBuilder.Callback<C> {
    @Override
    public void onConnectionException(IOException e) {
      Log.e(getClass().getSimpleName(),
        "Exception communicating through Orbot", e);
    }

    @Override
    public void onTimeout() {
      Log.e(getClass().getSimpleName(),
        "Timeout communicating through Orbot");
    }
  }
}