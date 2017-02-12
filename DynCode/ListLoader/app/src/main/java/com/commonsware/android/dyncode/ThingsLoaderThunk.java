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

package com.commonsware.android.dyncode;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Build;
import com.commonsware.android.dyncode.api.Thing;
import com.commonsware.android.dyncode.api.ThingsLoader;
import com.commonsware.cwac.netsecurity.TrustManagerBuilder;
import com.commonsware.cwac.security.ZipUtils;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import dalvik.system.DexClassLoader;

class ThingsLoaderThunk implements ThingsLoader {
  interface Callback {
    void onError(String message, Exception e);
  }

  final private Executor executor=Executors.newSingleThreadExecutor();
  final private File apkPath;
  final private File cachePath;
  final private String classname;
  final private Callback cb;
  final private TrustManagerBuilder tmb;
  final private URL url;
  final private List<Signature> ownSigs;
  final private PackageManager pm;
  private ThingsLoader extImpl;

  ThingsLoaderThunk(Context ctxt, String url,
                    String classname, Callback cb)
    throws MalformedURLException,
    PackageManager.NameNotFoundException {
    this.classname=classname;
    this.cb=cb;

    pm=ctxt.getPackageManager();
    ownSigs=
      Arrays.asList(pm
        .getPackageInfo(ctxt.getPackageName(), PackageManager.GET_SIGNATURES)
        .signatures);

    this.url=new URL(BuildConfig.EXTENSION_URL);

    tmb=new TrustManagerBuilder().withConfig(ctxt,
      R.xml.extension_server);

    String basename=Uri.parse(url).getLastPathSegment();

    apkPath=new File(ctxt.getCacheDir(), basename);
    cachePath=new File(ctxt.getCacheDir(),
      UUID.randomUUID().toString());

    cachePath.mkdirs();
  }

  @Override
  public void startAsyncLoad() {
    if (extImpl==null) {
      executor.execute(new Runnable() {
        @Override
        public void run() {
          if (!apkPath.exists()) {
            try {
              downloadExtension();
            }
            catch (Exception e) {
              reset();

              if (cb!=null) {
                cb.onError("exception in HTTP", e);
              }
            }
          }

          try {
            loadThunk();
            extImpl.startAsyncLoad();
          }
          catch (Exception e) {
            reset();

            if (cb!=null) {
              cb.onError("exception in loadThunk()/startAsyncLoad()",
                e);
            }
          }
        }
      });
    }
    else {
      extImpl.startAsyncLoad();
    }
  }

  @Override
  public List<Thing> getThings() {
    if (extImpl==null) {
      return(null);
    }

    return(extImpl.getThings());
  }

  void reset() {
    extImpl=null;
    ZipUtils.delete(apkPath);
    ZipUtils.delete(cachePath);
  }

  private void loadThunk()
    throws ClassNotFoundException, IllegalAccessException,
    InstantiationException {
    DexClassLoader dcl=
      new DexClassLoader(apkPath.getAbsolutePath(),
        cachePath.getAbsolutePath(), null,
        getClass().getClassLoader());
    Class<ThingsLoader> clazz=
      (Class<ThingsLoader>)dcl.loadClass(classname);

    extImpl=clazz.newInstance();
  }

  private void downloadExtension() throws Exception {
    HttpURLConnection c=
      (HttpURLConnection)url.openConnection();

    tmb.applyTo(c);

    FileOutputStream fos=
      new FileOutputStream(apkPath.getPath());
    BufferedOutputStream out=new BufferedOutputStream(fos);

    try {
      InputStream in=c.getInputStream();
      byte[] buffer=new byte[8192];
      int len;

      while ((len=in.read(buffer))>=0) {
        out.write(buffer, 0, len);
      }

      out.flush();
    }
    finally {
      try {
        fos.getFD().sync();
        out.close();
      }
      finally {
        c.disconnect();
      }
    }

    validateApk(apkPath);
  }

  private void validateApk(File apkPath) {
    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
      try {
        Signature[] fileSigs=getApkSignatures(apkPath);

        if (fileSigs.length!=ownSigs.size()) {
          throw new IllegalStateException(
            "Extension signatures do not match APK signatures");
        }

        for (Signature sig : fileSigs) {
          if (!ownSigs.contains(sig)) {
            throw new IllegalStateException(
              "Extension signatures do not match APK signatures");
          }
        }
      }
      catch (Exception e) {
        throw new IllegalStateException(
          "Could not validate extension APK", e);
      }
    }
  }

  private Signature[] getApkSignatures(File apkPath) {
    PackageInfo info=
      pm.getPackageArchiveInfo(apkPath.getAbsolutePath(),
        PackageManager.GET_SIGNATURES);

    if (info==null) {
      throw new IllegalStateException("Extension APK could not be parsed");
    }

    return(info.signatures);
  }
}
