package com.commonsware.empublite;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import de.greenrobot.event.EventBus;

public class EmPubLiteActivity extends Activity {
  private static final String MODEL="model";
  private static final String PREF_LAST_POSITION="lastPosition";
  private static final String PREF_SAVE_LAST_POSITION="saveLastPosition";
  private static final String PREF_KEEP_SCREEN_ON="keepScreenOn";
  private ViewPager pager=null;
  private ContentsAdapter adapter=null;
  private ModelFragment mfrag=null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setupStrictMode();

    setContentView(R.layout.main);
    pager=(ViewPager)findViewById(R.id.pager);
  }

  @Override
  public void onResume() {
    super.onResume();
    EventBus.getDefault().register(this);

    if (adapter==null) {
      mfrag=(ModelFragment)getFragmentManager().findFragmentByTag(MODEL);

      if (mfrag==null) {
        mfrag=new ModelFragment();

        getFragmentManager()
            .beginTransaction()
            .add(mfrag, MODEL)
            .commit();
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
  public void onPause() {
    EventBus.getDefault().unregister(this);

    if (mfrag.getPrefs()!=null) {
      int position=pager.getCurrentItem();

      mfrag.getPrefs().edit().putInt(PREF_LAST_POSITION, position)
          .apply();
    }

    super.onPause();
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
        i=new Intent(this, NoteActivity.class);
        i.putExtra(NoteActivity.EXTRA_POSITION, pager.getCurrentItem());
        startActivity(i);

        return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  @SuppressWarnings("unused")
  public void onEventMainThread(BookLoadedEvent event) {
    setupPager(event.getBook());
  }

  private void setupPager(BookContents contents) {
    adapter=new ContentsAdapter(this, contents);
    pager.setAdapter(adapter);
    findViewById(R.id.progressBar1).setVisibility(View.GONE);
    pager.setVisibility(View.VISIBLE);

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
