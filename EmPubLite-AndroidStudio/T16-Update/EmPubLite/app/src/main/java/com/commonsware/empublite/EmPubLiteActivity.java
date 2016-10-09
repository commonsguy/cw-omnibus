package com.commonsware.empublite;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
  private static final String PREF_LAST_POSITION="lastPosition";
  private static final String PREF_SAVE_LAST_POSITION="saveLastPosition";
  private static final String PREF_KEEP_SCREEN_ON="keepScreenOn";
  private ViewPager pager;
  private ContentsAdapter adapter;
  private ModelFragment mfrag=null;

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
      mfrag=(ModelFragment)getFragmentManager().findFragmentByTag(MODEL);

      if (mfrag==null) {
        mfrag=new ModelFragment();

        getFragmentManager().beginTransaction()
          .add(mfrag, MODEL).commit();
      }
      else if (mfrag.getBook()!=null) {
        setupPager(mfrag.getBook());
      }
    }

    if (mfrag.getPrefs()!=null) {
      pager.setKeepScreenOn(mfrag.getPrefs()
        .getBoolean(PREF_KEEP_SCREEN_ON, false));
    }
  }

  @Override
  public void onStop() {
    EventBus.getDefault().unregister(this);

    if (mfrag.getPrefs()!=null) {
      int position=pager.getCurrentItem();

      mfrag.getPrefs().edit().putInt(PREF_LAST_POSITION, position)
        .apply();
    }

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

      case R.id.settings:
        startActivity(new Intent(this, Preferences.class));

        return(true);

      case R.id.notes:
        startActivity(new Intent(this, NoteActivity.class)
          .putExtra(NoteActivity.EXTRA_POSITION,
            pager.getCurrentItem()));

        return(true);

      case R.id.update:
        startService(new Intent(this, DownloadCheckService.class));

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

    SharedPreferences prefs=mfrag.getPrefs();

    if (prefs!=null) {
      if (prefs.getBoolean(PREF_SAVE_LAST_POSITION, false)) {
        pager.setCurrentItem(prefs.getInt(PREF_LAST_POSITION, 0));
      }

      pager.setKeepScreenOn(prefs.getBoolean(PREF_KEEP_SCREEN_ON, false));
    }
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
