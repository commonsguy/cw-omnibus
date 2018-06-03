/***
  Copyright (c) 2014 CommonsWare, LLC
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

package com.commonsware.android.diceware;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.UriPermission;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;
import com.commonsware.cwac.document.DocumentFileCompat;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class PassphraseFragment extends Fragment {
  private static final String ASSET_FILENAME="eff_short_wordlist_2_0.txt";
  private static final int REQUEST_OPEN=1337;
  private static final int REQUEST_GET=REQUEST_OPEN + 1;
  private static final String PREF_URI="uri";
  private static final String STATE_WORD_COUNT="wordCount";
  private static final int[] WORD_COUNT_MENU_IDS={
    R.id.word_count_4,
    R.id.word_count_5,
    R.id.word_count_6,
    R.id.word_count_7,
    R.id.word_count_8,
    R.id.word_count_9,
    R.id.word_count_10
  };
  private Observable<List<String>> wordsObservable;
  private Disposable wordsSub;
  private Observable<DocumentFileCompat> docObservable;
  private Disposable docSub;
  private SecureRandom random=new SecureRandom();
  private SharedPreferences prefs;
  private TextView passphrase;
  private int wordCount=6;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setRetainInstance(true);
    setHasOptionsMenu(true);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return(inflater.inflate(R.layout.activity_main, container, false));
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    passphrase=view.findViewById(R.id.passphrase);

    if (savedInstanceState!=null) {
      wordCount=savedInstanceState.getInt(STATE_WORD_COUNT);
    }

    if (docObservable!=null) {
      docSub();
    }
    else {
      loadWords(false, wordsObservable==null);
    }
  }

  @Override
  public void onDestroy() {
    unsubDoc();
    unsubWords();
    super.onDestroy();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putInt(STATE_WORD_COUNT, wordCount);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.actions, menu);

    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT) {
      menu.findItem(R.id.open).setEnabled(true);
    }

    MenuItem checkable=menu.findItem(WORD_COUNT_MENU_IDS[wordCount-4]);

    if (checkable!=null) {
      checkable.setChecked(true);
    }

    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()) {
      case R.id.open:
        open();
        return(true);

      case R.id.get:
        get();
        return(true);

      case R.id.refresh:
        loadWords(false, true);
        return(true);

      case R.id.reset:
        prefs.edit().clear().apply();
        loadWords(true, true);
        return(true);

      case R.id.word_count_4:
      case R.id.word_count_5:
      case R.id.word_count_6:
      case R.id.word_count_7:
      case R.id.word_count_8:
      case R.id.word_count_9:
      case R.id.word_count_10:
        item.setChecked(!item.isChecked());

        int temp=Integer.parseInt(item.getTitle().toString());

        if (temp!=wordCount) {
          wordCount=temp;
          loadWords(false, true);
        }

        return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode,
                               Intent resultData) {
    if (resultCode==Activity.RESULT_OK) {
      docObservable=Observable
        .defer(() -> (Observable.just(createDurableContent(resultData))))
        .subscribeOn(Schedulers.io())
        .cache()
        .observeOn(AndroidSchedulers.mainThread());
      docSub();
    }
  }

  private void unsubWords() {
    if (wordsSub!=null && !wordsSub.isDisposed()) {
      wordsSub.dispose();
    }
  }

  private void unsubDoc() {
    if (docSub!=null && !docSub.isDisposed()) {
      docSub.dispose();
    }
  }

  private void loadWords(boolean forceReload, boolean regenPassphrase) {
    if (wordsObservable==null || forceReload) {
      final Application app=getActivity().getApplication();

      wordsObservable=Observable
        .defer(() -> (Observable.just(PreferenceManager
          .getDefaultSharedPreferences(app))))
        .subscribeOn(Schedulers.io())
        .map(sharedPreferences -> {
          PassphraseFragment.this.prefs=sharedPreferences;

          return(sharedPreferences.getString(PREF_URI, ""));
        })
        .map(s -> {
          InputStream in;

          if (s.length()==0) {
            in=app.getAssets().open(ASSET_FILENAME);
          }
          else {
            in=app.getContentResolver().openInputStream(Uri.parse(s));
          }

          return(readWords(in));
        })
        .cache()
        .observeOn(AndroidSchedulers.mainThread());
    }

    unsubWords();

    if (regenPassphrase) {
      wordsSub=wordsObservable.subscribe(this::rollDemBones, error -> {
          Toast
            .makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG)
            .show();
          Log.e(getClass().getSimpleName(), "Exception processing request",
            error);
      });
    }
  }

  private static List<String> readWords(InputStream in) throws IOException {
    InputStreamReader isr=new InputStreamReader(in);
    BufferedReader reader=new BufferedReader(isr);
    String line;
    List<String> result=new ArrayList<>();

    while ((line = reader.readLine())!=null) {
      String[] pieces=line.split("\\s");

      if (pieces.length==2) {
        result.add(pieces[1]);
      }
    }

    return(result);
  }

  private void rollDemBones(List<String> words) {
    StringBuilder buf=new StringBuilder();
    int size=words.size();

    for (int i=0;i<wordCount;i++) {
      if (buf.length()>0) {
        buf.append(' ');
      }

      buf.append(words.get(random.nextInt(size)));
    }

    passphrase.setText(buf.toString());
  }

  @TargetApi(Build.VERSION_CODES.KITKAT)
  private void open() {
    Intent i=
      new Intent()
        .setType("text/plain")
        .setAction(Intent.ACTION_OPEN_DOCUMENT)
        .addCategory(Intent.CATEGORY_OPENABLE);

    startActivityForResult(i, REQUEST_OPEN);
  }

  private void get() {
    Intent i=
      new Intent()
        .setType("text/plain")
        .setAction(Intent.ACTION_GET_CONTENT)
        .addCategory(Intent.CATEGORY_OPENABLE);

    startActivityForResult(i, REQUEST_GET);
  }

  private void docSub() {
    docSub=docObservable.subscribe(documentFile -> {
      docObservable=null;
      loadWords(true, true);
    });
  }

  private DocumentFileCompat createDurableContent(Intent result) throws IOException {
    Uri document=result.getData();
    ContentResolver resolver=getActivity().getContentResolver();
    boolean weHaveDurablePermission=obtainDurablePermission(resolver, document);

    if (!weHaveDurablePermission) {
      document=makeLocalCopy(getActivity(), resolver, document);
    }

    if (weHaveDurablePermission || document!=null) {
      prefs
        .edit()
        .putString(PREF_URI, document.toString())
        .commit();

      return(buildDocFileForUri(getActivity(), document));
    }

    throw new IllegalStateException("Could not get durable permission or make copy");
  }

  private static boolean obtainDurablePermission(ContentResolver resolver,
                                                 Uri document) {
    boolean weHaveDurablePermission=false;

    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT) {
      int perms=Intent.FLAG_GRANT_READ_URI_PERMISSION
        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

      try {
        resolver.takePersistableUriPermission(document, perms);

        for (UriPermission perm : resolver.getPersistedUriPermissions()) {
          if (perm.getUri().equals(document)) {
            weHaveDurablePermission=true;
          }
        }
      }
      catch (SecurityException e) {
        // OK, we were not offered any persistable permissions
      }
    }

    return(weHaveDurablePermission);
  }

  private static Uri makeLocalCopy(Context ctxt, ContentResolver resolver,
                                   Uri document)
    throws IOException {
    DocumentFileCompat docFile=buildDocFileForUri(ctxt, document);
    Uri result=null;

    if (docFile.getName()!=null) {
      String ext=
        MimeTypeMap.getSingleton().getExtensionFromMimeType(docFile.getType());

      if (ext!=null) {
        ext="."+ext;
      }

      File f=File.createTempFile("cw_", ext, ctxt.getFilesDir());

      docFile.copyTo(f);
      result=Uri.fromFile(f);
    }

    return(result);
  }

  private static DocumentFileCompat buildDocFileForUri(Context ctxt, Uri document) {
    DocumentFileCompat docFile;

    if (document.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
      docFile=DocumentFileCompat.fromSingleUri(ctxt, document);
    }
    else {
      docFile=DocumentFileCompat.fromFile(new File(document.getPath()));
    }

    return(docFile);
  }
}
