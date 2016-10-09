package com.commonsware.empublite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import io.karim.MaterialTabs;

public class EmPubLiteActivity extends Activity {
  private ViewPager pager;
  private ContentsAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    pager=(ViewPager)findViewById(R.id.pager);
    adapter=new ContentsAdapter(this);
    pager.setAdapter(adapter);

    MaterialTabs tabs=(MaterialTabs)findViewById(R.id.tabs);
    tabs.setViewPager(pager);
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
}
