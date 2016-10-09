package com.commonsware.empublite;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import io.karim.MaterialTabs;

public class EmPubLiteActivity extends Activity
  implements FragmentManager.OnBackStackChangedListener {
  private static final String MODEL="model";
  private static final String PREF_LAST_POSITION="lastPosition";
  private static final String PREF_SAVE_LAST_POSITION="saveLastPosition";
  private static final String PREF_KEEP_SCREEN_ON="keepScreenOn";
  private static final String HELP="help";
  private static final String ABOUT="about";
  private static final String FILE_HELP=
    "file:///android_asset/misc/help.html";
  private static final String FILE_ABOUT=
    "file:///android_asset/misc/about.html";
  private ViewPager pager;
  private ContentsAdapter adapter;
  private ModelFragment mfrag=null;
  private View sidebar=null;
  private View divider=null;
  private SimpleContentFragment help=null;
  private SimpleContentFragment about=null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    setupStrictMode();
    pager=(ViewPager)findViewById(R.id.pager);
    sidebar=findViewById(R.id.sidebar);
    divider=findViewById(R.id.divider);
    help=(SimpleContentFragment)getFragmentManager().findFragmentByTag(HELP);
    about=
      (SimpleContentFragment)getFragmentManager().findFragmentByTag(ABOUT);
    getFragmentManager().addOnBackStackChangedListener(this);
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
        showAbout();

        return(true);

      case R.id.help:
        showHelp();

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

  @Override
  public void onBackStackChanged() {
    if (getFragmentManager().getBackStackEntryCount() == 0) {
      LinearLayout.LayoutParams p=
        (LinearLayout.LayoutParams)sidebar.getLayoutParams();
      if (p.weight > 0) {
        p.weight=0;
        sidebar.setLayoutParams(p);
        divider.setVisibility(View.GONE);
      }
    }
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

  private void openSidebar() {
    LinearLayout.LayoutParams p=
      (LinearLayout.LayoutParams)sidebar.getLayoutParams();
    if (p.weight == 0) {
      p.weight=3;
      sidebar.setLayoutParams(p);
    }

    divider.setVisibility(View.VISIBLE);
  }

  private void showAbout() {
    if (sidebar!=null) {
      openSidebar();

      if (about==null) {
        about=SimpleContentFragment.newInstance(FILE_ABOUT);
      }

      getFragmentManager().beginTransaction().addToBackStack(null)
        .replace(R.id.sidebar, about, ABOUT).commit();
    }
    else {
      Intent i=new Intent(this, SimpleContentActivity.class);

      i.putExtra(SimpleContentActivity.EXTRA_FILE, FILE_ABOUT);
      startActivity(i);
    }
  }

  private void showHelp() {
    if (sidebar!=null) {
      openSidebar();

      if (help==null) {
        help=SimpleContentFragment.newInstance(FILE_HELP);
      }

      getFragmentManager().beginTransaction().addToBackStack(null)
        .replace(R.id.sidebar, help, HELP).commit();
    }
    else {
      Intent i=new Intent(this, SimpleContentActivity.class);

      i.putExtra(SimpleContentActivity.EXTRA_FILE, FILE_HELP);
      startActivity(i);
    }
  }
}
