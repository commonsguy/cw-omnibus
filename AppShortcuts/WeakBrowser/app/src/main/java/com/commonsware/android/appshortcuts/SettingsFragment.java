/***
 Copyright (c) 2016 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.appshortcuts;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@TargetApi(Build.VERSION_CODES.N_MR1)
public class SettingsFragment extends PreferenceFragment
  implements Preference.OnPreferenceChangeListener {
  private MultiSelectListPreference bookmarks;
  private ShortcutManager shortcuts;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    shortcuts=getActivity().getSystemService(ShortcutManager.class);

    addPreferencesFromResource(R.xml.settings);
    findPreference("enabled").setOnPreferenceChangeListener(this);

    bookmarks=(MultiSelectListPreference)findPreference("bookmarks");
    populateBookmarks();
    updateBookmarksSummary(bookmarks.getValues());
    bookmarks.setOnPreferenceChangeListener(this);
  }

  @Override
  public boolean onPreferenceChange(Preference preference,
                                    Object newValue) {
    if (preference==bookmarks) {
      updateBookmarksSummary((Set<String>)newValue);
      updateBookmarks((Set<String>)newValue);
    }
    else if ((Boolean)newValue) {
      showBookmarks();
    }
    else {
      hideBookmarks();
    }

    return(true);
  }

  private void populateBookmarks() {
    List<Bookmark> items=
      new ArrayList<>(Bookmark.MODEL.values());

    Collections.sort(items);

    ArrayList<CharSequence> titles=
      new ArrayList<CharSequence>();
    ArrayList<String> ids=new ArrayList<String>();

    for (Bookmark b : items) {
      titles.add(b.title);
      ids.add(b.id);
    }

    bookmarks
      .setEntries(titles.toArray(new CharSequence[titles.size()]));
    bookmarks
      .setEntryValues(ids.toArray(new String[ids.size()]));
  }

  private void showBookmarks() {
    updateBookmarks(bookmarks.getValues());
  }

  private void updateBookmarks(Set<String> ids) {
    shortcuts.setDynamicShortcuts(buildShortcuts(ids));
  }

  private void hideBookmarks() {
    shortcuts.removeAllDynamicShortcuts();
  }

  private void updateBookmarksSummary(Set<String> ids) {
    String summary;

    if (ids.size()==0) {
      summary="No app shortcuts selected";
    }
    else if (ids.size()==1) {
      summary="1 app shortcut selected";
    }
    else {
      summary=Integer.toString(ids.size())+" app shortcuts selected";
    }

    bookmarks.setSummary(summary);
  }

  private List<ShortcutInfo> buildShortcuts(Set<String> ids) {
    List<Bookmark> items=new ArrayList<>();

    for (String id: ids) {
      items.add(Bookmark.MODEL.get(id));
    }

    if (items.size()>0) Collections.sort(items);

    List<ShortcutInfo> shortcuts=new ArrayList<>();

    for (Bookmark item : items) {
      shortcuts.add(new ShortcutInfo.Builder(getActivity(), item.id)
        .setShortLabel(item.title)
        .setIcon(buildIcon(item))
        .setIntent(buildIntent(item))
        .build());
    }

    return(shortcuts);
  }

  private Intent buildIntent(Bookmark item) {
    return(new Intent(getActivity(), MainActivity.class)
      .setAction("i.can.haz.reason.why.this.is.REQUIRED")
      .setData(Uri.parse(item.url)))
      .putExtra(MainActivity.EXTRA_BOOKMARK_ID, item.id)
      .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
  }

  private Icon buildIcon(Bookmark item) {
    return(Icon.createWithResource(getActivity(),
      R.drawable.ic_bookmark_border_black_24dp));
  }
}
