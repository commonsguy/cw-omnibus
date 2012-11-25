package com.commonsware.empublite;

import android.content.Intent;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class EmPubLiteActivity extends SherlockFragmentActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    new MenuInflater(this).inflate(R.menu.options, menu);
    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        return(true);
        
      case R.id.about:
        Intent i=new Intent(this, SimpleContentActivity.class);
        startActivity(i);
        
        return(true);
        
      case R.id.help:
        i=new Intent(this, SimpleContentActivity.class);
        startActivity(i);
        
        return(true);
    }
    
    return(super.onOptionsItemSelected(item));
  }
}
