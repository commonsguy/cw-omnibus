package com.commonsware.empublite;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentByTag;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.commonsware.cwac.wakeful.WakefulIntentService;

@EActivity(R.layout.main)
@OptionsMenu(R.menu.options)
public class EmPubLiteActivity
extends SherlockFragmentActivity 
implements FragmentManager.OnBackStackChangedListener
{

	private static final String MODEL = "model";
	private static final String PREF_LAST_POSITION = "lastPosition";
	private static final String PREF_SAVE_LAST_POSITION = "saveLastPosition";
	private static final String PREF_KEEP_SCREEN_ON = "keepScreenOn";
	private static final String HELP = "help";
	private static final String ABOUT = "about";
	private static final String FILE_HELP = "file:///android_asset/misc/help.html";
	private static final String FILE_ABOUT = "file:///android_asset/misc/about.html";

	@ViewById(R.id.sidebar)
	View sidebar;
	
	@ViewById(R.id.divider)
	View divider;
	
	@ViewById(R.id.pager)
	ViewPager pager;
		
	@ViewById( R.id.progressBar1 ) 
	ProgressBar progressBar;
	
	@FragmentByTag(HELP) SimpleContentFragment help;
	@FragmentByTag(ABOUT)SimpleContentFragment about;
	@FragmentByTag(MODEL)ModelFragment_ model;

	private ContentsAdapter adapter = null;
	private SharedPreferences prefs = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		getSupportFragmentManager().addOnBackStackChangedListener(this);

		if (getSupportFragmentManager().getBackStackEntryCount() > 0)
		{
			openSidebar();
		}

		getSupportActionBar().setHomeButtonEnabled(true);
		UpdateReceiver_.scheduleAlarm(this);
	}

	@AfterViews
	void afterViews()
	{
		if (model == null)
		{
			model = new ModelFragment_();
			getSupportFragmentManager().beginTransaction().add(model, MODEL)
					.commit();
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();

		if (prefs != null)
		{
			pager.setKeepScreenOn(prefs.getBoolean(PREF_KEEP_SCREEN_ON, false));
		}

		IntentFilter f = new IntentFilter(
				DownloadInstallService.ACTION_UPDATE_READY);
		f.setPriority(1000);
		registerReceiver(onUpdate, f);
	}

	@Override
	public void onPause()
	{
		unregisterReceiver(onUpdate);

		if (prefs != null)
		{
			int position = pager.getCurrentItem();
			prefs.edit().putInt(PREF_LAST_POSITION, position).apply();
		}

		super.onPause();
	}

	// https://github.com/excilys/androidannotations/wiki/Handling%20Options%20Menu
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@OptionsItem(android.R.id.home)
	boolean homeMenuSelected()
	{
		pager.setCurrentItem(0, false);

		return true;
	}

	@OptionsItem(R.id.notes)
	boolean notesMenuSelected()
	{
		Intent i = new Intent(this, NoteActivity_.class);

		i.putExtra(NoteActivity.EXTRA_POSITION, pager.getCurrentItem());
		startActivity(i);

		return true;
	}

	@OptionsItem(R.id.update)
	boolean updateMenuSelected()
	{
		WakefulIntentService.sendWakefulWork(this, DownloadCheckService_.class);
		return (true);
	}

	@OptionsItem(R.id.about)
	boolean aboutMenuSelected()
	{
		if (sidebar != null)
		{
			openSidebar();

			if (about == null)
			{
				about = SimpleContentFragment.newInstance(FILE_ABOUT);
			}

			getSupportFragmentManager().beginTransaction().addToBackStack(null)
					.replace(R.id.sidebar, about).commit();
		}
		else
		{
			Intent i = new Intent(this, SimpleContentActivity_.class);

			i.putExtra(SimpleContentActivity.EXTRA_FILE, FILE_ABOUT);
			startActivity(i);
		}

		return true;
	}

	@OptionsItem(R.id.help)
	boolean helpMenuSelected()
	{
		if (sidebar != null)
		{
			openSidebar();

			if (help == null)
			{
				help = SimpleContentFragment.newInstance(FILE_HELP);
			}

			getSupportFragmentManager().beginTransaction().addToBackStack(null)
					.replace(R.id.sidebar, help).commit();
		}
		else
		{
			Intent i = new Intent(this, SimpleContentActivity_.class);

			i.putExtra(SimpleContentActivity.EXTRA_FILE, FILE_HELP);
			startActivity(i);
		}

		return true;
	}

	@OptionsItem(R.id.settings)
	boolean settingsMenuSelected()
	{
		startActivity(new Intent(this, Preferences.class));
		return true;
	}

	@Override
	public void onBackStackChanged()
	{
		if (getSupportFragmentManager().getBackStackEntryCount() == 0)
		{
			LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) sidebar
					.getLayoutParams();
			if (p.weight > 0)
			{
				p.weight = 0;
				sidebar.setLayoutParams(p);
				divider.setVisibility(View.GONE);
			}
		}
	}

	void setupPager(SharedPreferences prefs, BookContents contents)
	{
		this.prefs = prefs;

		adapter = new ContentsAdapter(this, contents);
		pager.setAdapter(adapter);

		progressBar.setVisibility(View.GONE);
		pager.setVisibility(View.VISIBLE);

		if (prefs.getBoolean(PREF_SAVE_LAST_POSITION, false))
		{
			pager.setCurrentItem(prefs.getInt(PREF_LAST_POSITION, 0));
		}

		pager.setKeepScreenOn(prefs.getBoolean(PREF_KEEP_SCREEN_ON, false));
	}

	void openSidebar()
	{
		LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) sidebar.getLayoutParams();
		if (p.weight == 0)
		{
			p.weight = 3;
			sidebar.setLayoutParams(p);
		}
		divider.setVisibility(View.VISIBLE);
	}

	/*
	 * no need for @EReceiver, unless hooking up with SystemService, Beans, etc.
	 * https://github.com/excilys/androidannotations/wiki/Enhance%20BroadcastReceivers
	 */
	private BroadcastReceiver onUpdate = new BroadcastReceiver()
	{
		public void onReceive(Context ctxt, Intent i)
		{
			model.updateBook();
			abortBroadcast();
		}
	};
}
