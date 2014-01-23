package com.commonsware.empublite;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;

import android.content.Intent;

import com.actionbarsherlock.app.SherlockFragmentActivity;

@EActivity(R.layout.simplecontent)
public class NoteActivity extends SherlockFragmentActivity
{
	public static final String EXTRA_POSITION = "position";
	public static final String NOTE = "note_fragment";
	
	@FragmentById(R.id.content) NoteFragment_ noteFragment;
	
	@AfterViews
	void thenAfterView()
	{
		if( noteFragment == null )
		{
			int position = getIntent().getIntExtra(EXTRA_POSITION, -1);

			if (position >= 0)
			{
				noteFragment = NoteFragment.newInstance(position);
				getSupportFragmentManager().beginTransaction().add(R.id.content, noteFragment ).commit();
			}
		}
		
	}

	void closeNotes()
	{
		finish();
	}

	void sendNotes(String prose)
	{
		Intent i = new Intent(Intent.ACTION_SEND);

		i.setType("text/plain");
		i.putExtra(Intent.EXTRA_TEXT, prose);

		startActivity(Intent.createChooser(i, getString(R.string.share_title)));

	}
}
