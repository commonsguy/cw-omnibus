package com.commonsware.empublite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import de.greenrobot.event.EventBus;

public class EmPubLiteActivity extends Activity {
  private static final String MODEL="model";
  private ViewPager pager=null;
  private ContentsAdapter adapter=null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setupStrictMode();

    setContentView(R.layout.main);
    pager=(ViewPager)findViewById(R.id.pager);

    getActionBar().setHomeButtonEnabled(true);
  }

  @Override
  public void onResume() {
    super.onResume();
    EventBus.getDefault().register(this);

    if (adapter==null) {
      ModelFragment mfrag=
          (ModelFragment)getFragmentManager().findFragmentByTag(MODEL);

      if (mfrag == null) {
        getFragmentManager().beginTransaction()
            .add(new ModelFragment(), MODEL).commit();
      }
      else if (mfrag.getBook() != null) {
        setupPager(mfrag.getBook());
      }
    }
  }

  @Override
  public void onPause() {
    EventBus.getDefault().unregister(this);
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
      case android.R.id.home:
        pager.setCurrentItem(0, false);
        return(true);

      case R.id.about:
        Intent i=new Intent(this, SimpleContentActivity.class);

        i.putExtra(SimpleContentActivity.EXTRA_FILE,
            "file:///android_asset/misc/about.html");
        startActivity(i);

        return(true);

      case R.id.help:
        i=new Intent(this, SimpleContentActivity.class);

        i.putExtra(SimpleContentActivity.EXTRA_FILE,
            "file:///android_asset/misc/help.html");
        startActivity(i);

        return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  public void onEventMainThread(BookLoadedEvent event) {
    setupPager(event.getBook());
  }

  private void setupPager(BookContents contents) {
    adapter=new ContentsAdapter(this, contents);
    pager.setAdapter(adapter);
    findViewById(R.id.progressBar1).setVisibility(View.GONE);
    pager.setVisibility(View.VISIBLE);
  }

  private void setupStrictMode() {
    StrictMode.ThreadPolicy.Builder builder=
        new StrictMode.ThreadPolicy.Builder().detectNetwork();

    if (BuildConfig.DEBUG) {
      builder.penaltyDeath();
    }
    else {
      builder.penaltyLog();
    }

    StrictMode.setThreadPolicy(builder.build());
  }
}
