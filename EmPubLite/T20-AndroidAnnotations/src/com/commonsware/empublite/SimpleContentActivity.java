package com.commonsware.empublite;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;

import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;

@EActivity(R.layout.simplecontent)
public class SimpleContentActivity extends SherlockFragmentActivity
{
	public static final String EXTRA_FILE = "file";
	@FragmentById(R.id.content) Fragment f;
	
	@Extra(EXTRA_FILE) String file;
	
	@AfterViews
	void thenAfterView()
	{
		if( f == null )
		{
			Fragment f = SimpleContentFragment.newInstance(file);
			getSupportFragmentManager().beginTransaction().add(R.id.content, f ).commit();
		}
		
	}
}