/***
  Copyright (c) 2012 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Android Development_
    http://commonsware.com/Android
 */

package com.commonsware.empublite;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import android.os.Bundle;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockFragment;

@EFragment(R.layout.editor)
@OptionsMenu( R.menu.notes )
public class NoteFragment extends SherlockFragment implements DatabaseHelper.NoteListener
{
	private static final String KEY_POSITION = "position";
	private boolean isDeleted = false;
	
	@FragmentArg(KEY_POSITION) int position = -1;
	@ViewById(R.id.editor)EditText editor;
	@Bean DatabaseHelper db;

	static NoteFragment_ newInstance(int position)
	{
		NoteFragment_ frag = new NoteFragment_();
		Bundle args = new Bundle();

		args.putInt(KEY_POSITION, position);
		frag.setArguments(args);
		
		return (frag);
	}

	@AfterInject
	void thenAfterInject()
	{
		if( position >= 0)
			db.getNoteAsync(position, this);
	}
	
	@Override
	public void onPause()
	{
		if (!isDeleted)
		{
			db.saveNoteAsync(position,
					editor.getText().toString());
		}

		super.onPause();
	}
	
	@OptionsItem(R.id.delete)
	boolean thenDeleteSelected()
	{
		isDeleted = true;
		db.deleteNoteAsync(position);

		((NoteActivity) getActivity()).closeNotes();

		return (true);
	}
	
	@OptionsItem(R.id.share)
	boolean thenSendNotesSelected()
	{
		((NoteActivity) getActivity()).sendNotes(editor.getText().toString());

		return (true);
	}


	@Override
	public void setNote(String note)
	{
		editor.setText(note);
	}
}