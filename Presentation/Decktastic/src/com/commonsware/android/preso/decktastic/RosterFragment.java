/***
 Copyright (c) 2014 CommonsWare, LLC
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

package com.commonsware.android.preso.decktastic;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.view.View;
import de.greenrobot.event.EventBus;

public class RosterFragment extends BrowseFragment
  implements OnItemViewClickedListener {
  @Override
  public void onAttach(Activity host) {
    super.onAttach(host);

    if (PresoRoster.getInstance().getPresoCount()==0) {
      new LoadThread(host).start();
    }
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    setOnItemViewClickedListener(this);
  }

  @Override
  public void onResume() {
    super.onResume();

    EventBus.getDefault().registerSticky(this);
  }

  @Override
  public void onPause() {
    EventBus.getDefault().unregister(this);

    super.onPause();
  }

  public void onEventMainThread(RosterLoadedEvent event) {
    setHeadersState(BrowseFragment.HEADERS_ENABLED);
    setTitle(getString(R.string.app_name));

    ArrayObjectAdapter rows=new ArrayObjectAdapter(new ListRowPresenter());
    PresoRoster roster=PresoRoster.getInstance();
    ArrayObjectAdapter listRowAdapter=new ArrayObjectAdapter(new PresoPresenter());

    for (int i=0; i < roster.getPresoCount(); ++i) {
      listRowAdapter.add(roster.getPreso(i));
    }

    HeaderItem header=new HeaderItem(0, "Presentations", null);
    rows.add(new ListRow(header, listRowAdapter));

    setAdapter(rows);
  }

  @Override
  public void onItemClicked(Presenter.ViewHolder viewHolder,
                            Object o,
                            RowPresenter.ViewHolder rowViewHolder,
                            Row row) {
    ((LeanbackActivity)getActivity()).showPreso((PresoContents)o);
  }

  private static class LoadThread extends Thread {
    private Context ctxt=null;

    LoadThread(Context ctxt) {
      super();

      this.ctxt=ctxt.getApplicationContext();
      android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
    }

    @Override
    public void run() {
      PresoRoster.getInstance().load(ctxt.getExternalFilesDir(null));

      EventBus.getDefault().postSticky(new RosterLoadedEvent());
    }
  }
}
