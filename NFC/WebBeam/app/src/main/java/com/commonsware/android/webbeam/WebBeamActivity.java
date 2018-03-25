/***
  Copyright (c) 2012 CommonsWare, LLC
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

package com.commonsware.android.webbeam;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import java.nio.charset.Charset;

public class WebBeamActivity extends FragmentActivity implements
    CreateNdefMessageCallback {
  private static final String MIME_TYPE=
      "application/vnd.commonsware.sample.webbeam";
  private NfcAdapter adapter=null;
  private BeamFragment beamFragment=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    beamFragment=
        (BeamFragment)getSupportFragmentManager().findFragmentById(android.R.id.content);

    if (beamFragment == null) {
      beamFragment=new BeamFragment();

      getSupportFragmentManager().beginTransaction()
                                 .add(android.R.id.content, beamFragment)
                                 .commit();
    }

    adapter=NfcAdapter.getDefaultAdapter(this);

    findViewById(android.R.id.content).post(new Runnable() {
      public void run() {
        handleIntent(getIntent());
      }
    });
  }

  @Override
  public void onNewIntent(Intent i) {
    handleIntent(i);
  }

  @Override
  public void onStop() {
    disablePush();
    super.onStop();
  }

  void enablePush() {
    adapter.setNdefPushMessageCallback(this, this);
  }

  void disablePush() {
    adapter.setNdefPushMessageCallback(null, this);
  }

  boolean hasNFC() {
    return(adapter != null);
  }

  private void handleIntent(Intent i) {
    if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(i.getAction())) {
      Parcelable[] rawMsgs=
          i.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
      NdefMessage msg=(NdefMessage)rawMsgs[0];
      String url=new String(msg.getRecords()[0].getPayload());

      beamFragment.loadUrl(url);
    }
  }

  @Override
  public NdefMessage createNdefMessage(NfcEvent arg0) {
    NdefRecord uriRecord=
        new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                       MIME_TYPE.getBytes(Charset.forName("US-ASCII")),
                       new byte[0],
                       beamFragment.getUrl()
                           .getBytes(Charset.forName("US-ASCII")));
    NdefMessage msg=
        new NdefMessage(
                        new NdefRecord[] {
                            uriRecord,
                            NdefRecord.createApplicationRecord("com.commonsware.android.webbeam") });
    
    return(msg);
  }
}