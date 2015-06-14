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
    https://commonsware.com/Android
 */

package com.commonsware.android.jimmyb;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import java.nio.charset.Charset;

public class MainActivity extends Activity implements
    NfcAdapter.CreateNdefMessageCallback,
    NfcAdapter.OnNdefPushCompleteCallback {
  private static final String MIME_TYPE="vnd.secret/agent.man";
  private static final Charset US_ASCII=Charset.forName("US-ASCII");
  private NfcAdapter nfc=null;
  private boolean inWriteMode=false;
  private EditText secretMessage=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    nfc=NfcAdapter.getDefaultAdapter(this);
    secretMessage=(EditText)findViewById(R.id.secretMessage);

    nfc.setOnNdefPushCompleteCallback(this, this);

    if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
      readFromTag(getIntent());
    }
  }

  @Override
  protected void onNewIntent(Intent i) {
    if (inWriteMode
        && NfcAdapter.ACTION_TAG_DISCOVERED.equals(i.getAction())) {
      writeToTag(i);
    }
    else if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(i.getAction())) {
      readFromTag(i);
    }
  }

  @Override
  public void onPause() {
    if (isFinishing()) {
      cleanUpWritingToTag();
    }

    super.onPause();
  }

  @TargetApi(16)
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.activity_main, menu);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      menu.findItem(R.id.simple_beam)
          .setEnabled(nfc.isNdefPushEnabled());
      menu.findItem(R.id.file_beam).setEnabled(nfc.isNdefPushEnabled());
    }

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.write_tag:
        setUpWriteMode();
        return(true);

      case R.id.simple_beam:
        enablePush();
        return(true);

      case R.id.file_beam:
        Intent i=new Intent(Intent.ACTION_GET_CONTENT);

        i.setType("*/*");
        startActivityForResult(i, 0);
        return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  @TargetApi(16)
  @Override
  protected void onActivityResult(int requestCode, int resultCode,
                                  Intent data) {
    if (requestCode==0 && resultCode==RESULT_OK) {
      nfc.setBeamPushUris(new Uri[] {data.getData()}, this);
    }
  }

  @Override
  public NdefMessage createNdefMessage(NfcEvent event) {
    return(new NdefMessage(
                           new NdefRecord[] {
                               buildNdefRecord(),
                               NdefRecord.createApplicationRecord("com.commonsware.android.jimmyb") }));
  }

  @Override
  public void onNdefPushComplete(NfcEvent event) {
    nfc.setNdefPushMessageCallback(null, this);
  }

  void setUpWriteMode() {
    if (!inWriteMode) {
      IntentFilter discovery=
          new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
      IntentFilter[] tagFilters=new IntentFilter[] { discovery };
      Intent i=
          new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
              | Intent.FLAG_ACTIVITY_CLEAR_TOP);
      PendingIntent pi=PendingIntent.getActivity(this, 0, i, 0);

      inWriteMode=true;
      nfc.enableForegroundDispatch(this, pi, tagFilters, null);
    }
  }

  void writeToTag(Intent i) {
    Tag tag=i.getParcelableExtra(NfcAdapter.EXTRA_TAG);
    NdefMessage msg=
        new NdefMessage(new NdefRecord[] { buildNdefRecord() });

    new WriteTagTask(this, msg, tag).execute();
  }

  NdefRecord buildNdefRecord() {
    return(new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                          MIME_TYPE.getBytes(), new byte[] {},
                          secretMessage.getText().toString().getBytes()));
  }

  void cleanUpWritingToTag() {
    nfc.disableForegroundDispatch(this);
    inWriteMode=false;
  }

  void readFromTag(Intent i) {
    Parcelable[] msgs=
        (Parcelable[])i.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

    if (msgs.length > 0) {
      NdefMessage msg=(NdefMessage)msgs[0];

      if (msg.getRecords().length > 0) {
        NdefRecord rec=msg.getRecords()[0];

        secretMessage.setText(new String(rec.getPayload(), US_ASCII));
      }
    }
  }

  void enablePush() {
    nfc.setNdefPushMessageCallback(this, this);
  }
}
