package com.commonsware.empublite;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class NoteActivity extends SherlockFragmentActivity implements
    NoteFragment.NoteListener {
  public static final String EXTRA_POSITION="position";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getSupportFragmentManager().findFragmentById(android.R.id.content)==null) {
      int position=getIntent().getIntExtra(EXTRA_POSITION, -1);

      if (position >= 0) {
        Fragment f=NoteFragment.newInstance(position);

        getSupportFragmentManager().beginTransaction()
                                   .add(android.R.id.content, f)
                                   .commit();
      }
    }
  }

  @Override
  public void closeNotes() {
    finish();
  }

  public static void sendNotes(Context ctxt, String prose) {
    Intent i=new Intent(Intent.ACTION_SEND);

    i.setType("text/plain");
    i.putExtra(Intent.EXTRA_TEXT, prose);

    ctxt.startActivity(Intent.createChooser(i,
                                            ctxt.getString(R.string.share_title)));

  }
}
