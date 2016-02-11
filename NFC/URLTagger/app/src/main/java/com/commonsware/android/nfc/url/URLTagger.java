/***
  Copyright (c) 2012 CommonsWare, LLC
  portions Copyright 2012, The Android Open Source Project
  
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

package com.commonsware.android.nfc.url;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class URLTagger extends Activity {
  static private final String[] PREFIXES={"http://www.", "https://www.",
                                          "http://", "https://",
                                          "tel:", "mailto:",
                                          "ftp://anonymous:anonymous@",
                                          "ftp://ftp.", "ftps://",
                                          "sftp://", "smb://",
                                          "nfs://", "ftp://",
                                          "dav://", "news:",
                                          "telnet://", "imap:",
                                          "rtsp://", "urn:",
                                          "pop:", "sip:", "sips:",
                                          "tftp:", "btspp://",
                                          "btl2cap://", "btgoep://",
                                          "tcpobex://",
                                          "irdaobex://",
                                          "file://", "urn:epc:id:",
                                          "urn:epc:tag:",
                                          "urn:epc:pat:",
                                          "urn:epc:raw:",
                                          "urn:epc:", "urn:nfc:"};
  private NfcAdapter nfc=null;
  private boolean inWriteMode=false;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    
    nfc=NfcAdapter.getDefaultAdapter(this);
  }
  
  @Override
  protected void onNewIntent(Intent intent) {
    if (inWriteMode &&
        NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
      Tag tag=intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
      byte[] url=buildUrlBytes(getIntent().getStringExtra(Intent.EXTRA_TEXT));
      NdefRecord record=new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                                        NdefRecord.RTD_URI,
                                        new byte[] {}, url);
      NdefMessage msg=new NdefMessage(new NdefRecord[] {record});

      new WriteTask(this, msg, tag).execute();
    }
  }
  
  @Override
  public void onResume() {
    super.onResume();
    
    if (!inWriteMode) {
      IntentFilter discovery=new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
      IntentFilter[] tagFilters=new IntentFilter[] { discovery };
      Intent i=new Intent(this, getClass())
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|
                          Intent.FLAG_ACTIVITY_CLEAR_TOP);
      PendingIntent pi=PendingIntent.getActivity(this, 0, i, 0);
      
      inWriteMode=true;
      nfc.enableForegroundDispatch(this, pi, tagFilters, null);
    }
  }
  
  @Override
  public void onPause() {
    if (isFinishing()) {
      nfc.disableForegroundDispatch(this);
      inWriteMode=false;
    }
    
    super.onPause();
  }
  
  private byte[] buildUrlBytes(String url) {
    byte prefixByte=0;
    String subset=url;
    int bestPrefixLength=0;
    
    for (int i=0;i<PREFIXES.length;i++) {
      String prefix = PREFIXES[i];
      
      if (url.startsWith(prefix) && prefix.length() > bestPrefixLength) {
        prefixByte=(byte)(i+1);
        bestPrefixLength=prefix.length();
        subset=url.substring(bestPrefixLength);
      }
    }
    
    final byte[] subsetBytes = subset.getBytes();
    final byte[] result = new byte[subsetBytes.length+1];
    
    result[0]=prefixByte;
    System.arraycopy(subsetBytes, 0, result, 1, subsetBytes.length);
    
    return(result);
  }

  static class WriteTask extends AsyncTask<Void, Void, Void> {
    Activity host=null;
    NdefMessage msg=null;
    Tag tag=null;
    String text=null;
    
    WriteTask(Activity host, NdefMessage msg, Tag tag) {
      this.host=host;
      this.msg=msg;
      this.tag=tag;
    }
    
    @Override
    protected Void doInBackground(Void... arg0) {
      int size=msg.toByteArray().length;

      try {
        Ndef ndef=Ndef.get(tag);
        
        if (ndef==null) {
          NdefFormatable formatable=NdefFormatable.get(tag);
          
          if (formatable!=null) {
            try {
              formatable.connect();
              
              try {
                formatable.format(msg);
              }
              catch (Exception e) {
                text="Tag refused to format";
              }
            }
            catch (Exception e) {
              text="Tag refused to connect";
            }
            finally {
              formatable.close();
            }
          }
          else {
            text="Tag does not support NDEF";
          }
        }
        else {
          ndef.connect();

          try {
            if (!ndef.isWritable()) {
              text="Tag is read-only";
            }
            else if (ndef.getMaxSize()<size) {
              text="Message is too big for tag";
            }
            else {
              ndef.writeNdefMessage(msg);
            }
          }
          catch (Exception e) {
            text="Tag refused to connect";
          }
          finally {
            ndef.close();
          }
        }
      }
      catch (Exception e) {
        Log.e("URLTagger", "Exception when writing tag", e);
        text="General exception: "+e.getMessage();
      }
      
      return(null);
    }
    
    @Override
    protected void onPostExecute(Void unused) {
      if (text!=null) {
        Toast.makeText(host, text, Toast.LENGTH_SHORT).show();
      }
      
      host.finish();
    }
  }
}
