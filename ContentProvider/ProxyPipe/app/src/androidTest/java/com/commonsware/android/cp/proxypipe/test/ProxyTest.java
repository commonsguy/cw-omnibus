/***
 Copyright (c) 2017 CommonsWare, LLC
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

package com.commonsware.android.cp.proxypipe.test;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.commonsware.android.cp.pipe.PipeProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ProxyTest {
  private static Uri ASSET_URI=
    PipeProvider.CONTENT_URI.buildUpon().appendPath("test.pdf").build();

  @Test
  public void readAll() throws IOException {
    Context app=InstrumentationRegistry.getTargetContext();
    AssetManager assets=app.getAssets();
    InputStream in=
      assets.open(ASSET_URI.getLastPathSegment(), AssetManager.ACCESS_STREAMING);
    byte[] fromAsset=PipeProvider.readAll(in);

    assertEquals(21609, fromAsset.length);

    Context me=InstrumentationRegistry.getContext();

    in=me.getContentResolver().openInputStream(ASSET_URI);

    byte[] fromProvider=PipeProvider.readAll(in);

    assertEquals(21609, fromProvider.length);
    assertArrayEquals(fromAsset, fromProvider);
  }

  @Test
  public void testMarkSupportedStream() throws IOException {
    Context me=InstrumentationRegistry.getContext();
    InputStream in=me.getContentResolver().openInputStream(ASSET_URI);

    assertTrue(in.markSupported());
  }

  @Test
  public void testMarkSupportedFDStream() throws IOException {
    Context me=InstrumentationRegistry.getContext();
    FileDescriptor fd=
      me.getContentResolver().openFileDescriptor(ASSET_URI, "r").getFileDescriptor();
    InputStream in=new FileInputStream(fd);

    assertTrue(in.markSupported());
  }

  @Test
  public void testMarkSupportedAFDStream() throws IOException {
    Context me=InstrumentationRegistry.getContext();
    AssetFileDescriptor afd=
      me.getContentResolver().openAssetFileDescriptor(ASSET_URI, "r");
    InputStream in=afd.createInputStream();

    assertTrue(in.markSupported());
  }
}
