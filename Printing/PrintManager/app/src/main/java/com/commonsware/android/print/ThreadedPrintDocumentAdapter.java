/***
  Copyright (c) 2014 CommonsWare, LLC
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

package com.commonsware.android.print;

import android.content.Context;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

abstract class ThreadedPrintDocumentAdapter extends
    PrintDocumentAdapter {
  abstract LayoutJob buildLayoutJob(PrintAttributes oldAttributes,
                                    PrintAttributes newAttributes,
                                    CancellationSignal cancellationSignal,
                                    LayoutResultCallback callback,
                                    Bundle extras);

  abstract WriteJob buildWriteJob(PageRange[] pages,
                                  ParcelFileDescriptor destination,
                                  CancellationSignal cancellationSignal,
                                  WriteResultCallback callback,
                                  Context ctxt);

  private Context ctxt=null;
  private ExecutorService threadPool=Executors.newFixedThreadPool(1);

  ThreadedPrintDocumentAdapter(Context ctxt) {
    this.ctxt=ctxt;
  }

  @Override
  public void onLayout(PrintAttributes oldAttributes,
                       PrintAttributes newAttributes,
                       CancellationSignal cancellationSignal,
                       LayoutResultCallback callback, Bundle extras) {
    threadPool.submit(buildLayoutJob(oldAttributes, newAttributes,
                                     cancellationSignal, callback,
                                     extras));
  }

  @Override
  public void onWrite(PageRange[] pages,
                      ParcelFileDescriptor destination,
                      CancellationSignal cancellationSignal,
                      WriteResultCallback callback) {
    threadPool.submit(buildWriteJob(pages, destination,
                                    cancellationSignal, callback, ctxt));
  }

  @Override
  public void onFinish() {
    threadPool.shutdown();

    super.onFinish();
  }

  protected abstract static class LayoutJob implements Runnable {
    PrintAttributes oldAttributes;
    PrintAttributes newAttributes;
    CancellationSignal cancellationSignal;
    LayoutResultCallback callback;
    Bundle extras;

    LayoutJob(PrintAttributes oldAttributes,
              PrintAttributes newAttributes,
              CancellationSignal cancellationSignal,
              LayoutResultCallback callback, Bundle extras) {
      this.oldAttributes=oldAttributes;
      this.newAttributes=newAttributes;
      this.cancellationSignal=cancellationSignal;
      this.callback=callback;
      this.extras=extras;
    }
  }

  protected abstract static class WriteJob implements Runnable {
    PageRange[] pages;
    ParcelFileDescriptor destination;
    CancellationSignal cancellationSignal;
    WriteResultCallback callback;
    Context ctxt;

    WriteJob(PageRange[] pages, ParcelFileDescriptor destination,
             CancellationSignal cancellationSignal,
             WriteResultCallback callback, Context ctxt) {
      this.pages=pages;
      this.destination=destination;
      this.cancellationSignal=cancellationSignal;
      this.callback=callback;
      this.ctxt=ctxt;
    }
  }
}
