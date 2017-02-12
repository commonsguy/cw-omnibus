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

package com.commonsware.android.tte;

import android.app.Fragment;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class EditorFragment extends Fragment {
  public interface Contract {
    void applyDisplayName(Uri document, String displayName);
    void close(Uri document);
    void launchInNewWindow(Uri document);
  }

  private static final String KEY_DOCUMENT="doc";
  private EditText editor;
  private View progress;
  private boolean isClosing=false;
  private boolean isLoaded=false;
  private MenuItem launchItem;
  private boolean canWrite=false;

  static EditorFragment newInstance(Uri document) {
    EditorFragment frag=new EditorFragment();
    Bundle args=new Bundle();

    args.putParcelable(KEY_DOCUMENT, document);
    frag.setArguments(args);

    return(frag);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setHasOptionsMenu(true);
    setRetainInstance(true);
    EventBus.getDefault().register(this);
  }

  @Override
  public void onStop() {
    save();

    super.onStop();
  }

  @Override
  public void onDestroy() {
    EventBus.getDefault().unregister(this);

    super.onDestroy();
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    View result=inflater.inflate(R.layout.editor, container, false);

    editor=(EditText)result.findViewById(R.id.editor);
    progress=result.findViewById(R.id.progress);

    return(result);
  }

  @Override
  public void onViewCreated(View view,
                            Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    if (editor.getText().length()==0) {
      DocumentStorageService.loadDocument(getActivity(),
        getDocumentUri());
    }
  }

  @Override
  public void onCreateOptionsMenu(Menu menu,
                                  MenuInflater inflater) {
    inflater.inflate(R.menu.editor_actions, menu);

    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N) {
      launchItem=menu.findItem(R.id.launch);
      launchItem.setVisible(getActivity().isInMultiWindowMode());
    }

    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()) {
      case R.id.save:
        save();
        return(true);

      case R.id.close:
        ((Contract)getActivity()).close(getDocumentUri());
        return(true);

      case R.id.launch:
        ((Contract)getActivity())
          .launchInNewWindow(getDocumentUri());
        return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  @Override
  public void onMultiWindowModeChanged(boolean inMultiWindow) {
    super.onMultiWindowModeChanged(inMultiWindow);

    if (launchItem!=null) {
      launchItem.setVisible(inMultiWindow);
    }
  }

  @Subscribe(threadMode=ThreadMode.MAIN)
  public void onDocumentLoaded(DocumentStorageService.DocumentLoadedEvent event) {
    if (event.document.equals(getDocumentUri())) {
      editor.setText(event.text);
      editor.setVisibility(View.VISIBLE);
      progress.setVisibility(View.GONE);
      ((Contract)getActivity())
        .applyDisplayName(getDocumentUri(), event.displayName);
      isLoaded=true;
      canWrite=event.canWrite;

      if (!canWrite) {
        editor.setEnabled(false);
      }
    }
  }

  @Subscribe(threadMode=ThreadMode.MAIN)
  public void onDocumentSaved(DocumentStorageService.DocumentSavedEvent event) {
    if (event.document.equals(getDocumentUri())) {
      editor.setEnabled(true);
    }
  }

  @Subscribe(threadMode=ThreadMode.MAIN)
  public void onDocumentSaveError(DocumentStorageService.DocumentSaveErrorEvent event) {
    if (event.document.equals(getDocumentUri())) {
      editor.setEnabled(true);
      Toast
        .makeText(getActivity(), R.string.msg_save_error,
          Toast.LENGTH_LONG)
        .show();
    }
  }

  private void save() {
    if (isLoaded && canWrite) {
      editor.setEnabled(false);
      DocumentStorageService.saveDocument(getActivity(),
        getDocumentUri(), editor.getText().toString(),
        isClosing);
    }
  }

  Uri getDocumentUri() {
    return(getArguments().getParcelable(KEY_DOCUMENT));
  }

  void markAsClosing() {
    isClosing=true;
  }
}