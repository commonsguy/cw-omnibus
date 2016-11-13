package com.commonsware.android.appshortcuts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ShortcutManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import static android.content.Intent.ACTION_SEARCH;

public class MainActivity extends Activity {
  static final String EXTRA_BOOKMARK_ID="bookmark_id";
  private static final String CW="https://commonsware.com";
  private static final String SEARCH="https://duckduckgo.com";
  private WebView browser;

  @SuppressLint("SetJavaScriptEnabled")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    browser=(WebView)findViewById(R.id.browser);
    browser.getSettings().setJavaScriptEnabled(true);
    browser.setWebViewClient(new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(WebView view,
                                              String url) {
        view.loadUrl(url);

        return(true);
      }
    });

    visit(getIntent());
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);

    visit(intent);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.actions, menu);

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()) {
      case R.id.search:
        visit(SEARCH);
        return(true);

      case R.id.settings:
        startActivity(new Intent(this, SettingsActivity.class));
        return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  private void visit(Intent i) {
    String url=CW;

    if (ACTION_SEARCH.equals(i.getAction())) {
      url=SEARCH;
    }
    else if (i.getData()!=null) {
      url=i.getData().toString();

      if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N_MR1) {
        String id=i.getStringExtra(EXTRA_BOOKMARK_ID);

        if (id!=null) {
          getSystemService(ShortcutManager.class)
            .reportShortcutUsed(id);
        }
      }
    }

    visit(url);
  }

  private void visit(String url) {
    if (isSafeUri(url)) {
      browser.loadUrl(url);
    }
  }

  private boolean isSafeUri(String uri) {
    return(true);
  }
}
