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

package com.commonsware.android.cp.pipe;

import android.os.ProxyFileDescriptorCallback;
import android.system.ErrnoException;
import android.system.OsConstants;
import hugo.weaving.DebugLog;

class BufferProxyCallback extends ProxyFileDescriptorCallback {
  private final byte[] buffer;

  @DebugLog
  BufferProxyCallback(byte[] buffer) {
    this.buffer=buffer;
  }

  @DebugLog
  @Override
  public void onRelease() {
    // not needed here
  }

  @DebugLog
  @Override
  public long onGetSize() throws ErrnoException {
    return(buffer.length);
  }

  @DebugLog
  @Override
  public int onRead(long offset, int size, byte[] data) throws ErrnoException {
    int toRead=(offset+size<=buffer.length) ? size : (int)(buffer.length-offset);

    System.arraycopy(buffer, (int)offset, data, 0, toRead);

    return(toRead);
  }

  @DebugLog
  @Override
  public int onWrite(long offset, int size, byte[] data) throws ErrnoException {
    throw new ErrnoException("onWrite", OsConstants.EOPNOTSUPP);
  }

  @DebugLog
  @Override
  public void onFsync() throws ErrnoException {
    // not needed here
  }
}
