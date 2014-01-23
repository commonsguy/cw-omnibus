package com.commonsware.empublite;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

import android.os.Bundle;

@EFragment
public class SimpleContentFragment extends AbstractContentFragment
{
	private static final String KEY_FILE = "file";
	@FragmentArg(KEY_FILE) String file;

	protected static SimpleContentFragment_ newInstance(String file)
	{
		SimpleContentFragment_ f = new SimpleContentFragment_();

		Bundle args = new Bundle();
		args.putString(KEY_FILE, file);
		f.setArguments(args);

		return (f);
	}

	@Override
	String getPage()
	{
		return file;
	}
}
