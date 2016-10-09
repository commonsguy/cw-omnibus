package com.commonsware.empublite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import io.karim.MaterialTabs;

public class EmPubLiteActivity extends Activity {
  private static final String MODEL="model";
  private ViewPager pager;
  private ContentsAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    setupStrictMode();
    pager=(ViewPager)findViewById(R.id.pager);
  }

  @Override
  public void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);

    if (adapter==null) {
      ModelFragment mfrag=
        (ModelFragment)getFragmentManager().findFragmentByTag(MODEL);

      if (mfrag==null) {
        getFragmentManager().beginTransaction()
          .add(new ModelFragment(), MODEL).commit();
      }
      else if (mfrag.getBook()!=null) {
        setupPager(mfrag.getBook());
      }
    }
  }

  @Override
  public void onStop() {
    EventBus.getDefault().unregister(this);
    super.onStop();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.options, menu);

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.about:
        Intent i = new Intent(this, SimpleContentActivity.class)
          .putExtra(SimpleContentActivity.EXTRA_FILE,
            "file:///android_asset/misc/about.html");
        startActivity(i);

        return(true);

      case R.id.help:
        i = new Intent(this, SimpleContentActivity.class)
          .putExtra(SimpleContentActivity.EXTRA_FILE,
            "file:///android_asset/misc/help.html");
        startActivity(i);

        return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  @SuppressWarnings("unused")
  @Subscribe(threadMode =ThreadMode.MAIN)
  public void onBookLoaded(BookLoadedEvent event) {
    setupPager(event.getBook());
  }

  private void setupPager(BookContents contents) {
    adapter=new ContentsAdapter(this, contents);
    pager.setAdapter(adapter);

    MaterialTabs tabs=(MaterialTabs)findViewById(R.id.tabs);
    tabs.setViewPager(pager);
  }

  private void setupStrictMode() {
    StrictMode.ThreadPolicy.Builder builder=
      new StrictMode.ThreadPolicy.Builder()
        .detectAll()
        .penaltyLog();

    if (BuildConfig.DEBUG) {
      builder.penaltyFlashScreen();
    }

    StrictMode.setThreadPolicy(builder.build());
  }
}
