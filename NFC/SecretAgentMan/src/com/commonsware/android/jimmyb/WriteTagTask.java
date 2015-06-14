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

import android.nfc.NdefMessage;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

class WriteTagTask extends AsyncTask<Void, Void, Void> {
  MainActivity host=null;
  NdefMessage msg=null;
  Tag tag=null;
  String text=null;

  WriteTagTask(MainActivity host, NdefMessage msg, Tag tag) {
    this.host=host;
    this.msg=msg;
    this.tag=tag;
  }

  @Override
  protected Void doInBackground(Void... arg0) {
    int size=msg.toByteArray().length;

    try {
      Ndef ndef=Ndef.get(tag);

      if (ndef == null) {
        NdefFormatable formatable=NdefFormatable.get(tag);

        if (formatable != null) {
          try {
            formatable.connect();

            try {
              formatable.format(msg);
            }
            catch (Exception e) {
              text=host.getString(R.string.tag_refused_to_format);
            }
          }
          catch (Exception e) {
            text=host.getString(R.string.tag_refused_to_connect);
          }
          finally {
            formatable.close();
          }
        }
        else {
          text=host.getString(R.string.tag_does_not_support_ndef);
        }
      }
      else {
        ndef.connect();

        try {
          if (!ndef.isWritable()) {
            text=host.getString(R.string.tag_is_read_only);
          }
          else if (ndef.getMaxSize() < size) {
            text=host.getString(R.string.message_is_too_big_for_tag);
          }
          else {
            ndef.writeNdefMessage(msg);
            text=host.getString(R.string.success);
          }
        }
        catch (Exception e) {
          text=host.getString(R.string.tag_refused_to_connect);
        }
        finally {
          ndef.close();
        }
      }
    }
    catch (Exception e) {
      Log.e("URLTagger", "Exception when writing tag", e);
      text=host.getString(R.string.general_exception) + e.getMessage();
    }

    return(null);
  }

  @Override
  protected void onPostExecute(Void unused) {
    host.cleanUpWritingToTag();

    if (text != null) {
      Toast.makeText(host, text, Toast.LENGTH_SHORT).show();
    }
  }
}