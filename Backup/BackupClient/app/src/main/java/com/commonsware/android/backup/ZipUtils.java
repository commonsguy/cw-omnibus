/**
 * Copyright (c) 2015 CommonsWare, LLC
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.commonsware.android.backup;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

// inspired by https://www.securecoding.cert.org/confluence/display/java/IDS04-J.+Safely+extract+files+from+ZipInputStream
// modified from https://github.com/commonsguy/cwac-security

class ZipUtils {
  private static final int BUFFER_SIZE=16384;
  private static final int DEFAULT_MAX_ENTRIES=1024;
  private static final int DEFAULT_MAX_SIZE=1024*1024*64;

  public static void unzip(File zipFile, File destDir,
                           String subtreeInZip)
    throws UnzipException, IOException {
    if (destDir.exists()) {
      deleteContents(destDir);
    }
    else {
      destDir.mkdirs();
    }

    try {
      final FileInputStream fis=new FileInputStream(zipFile);
      final ZipInputStream zis=new ZipInputStream(new BufferedInputStream(fis));
      ZipEntry entry;
      int entries=0;
      long total=0;

      try {
        while ((entry=zis.getNextEntry()) != null) {
          if (subtreeInZip==null || entry.getName().startsWith(subtreeInZip)) {
            int bytesRead;
            final byte data[]=new byte[BUFFER_SIZE];
            final String zipCanonicalPath=
              validateZipEntry(entry.getName().substring(subtreeInZip.length()),
                destDir);

            if (entry.isDirectory()) {
              new File(zipCanonicalPath).mkdir();
            }
            else {
              final FileOutputStream fos=
                new FileOutputStream(zipCanonicalPath);
              final BufferedOutputStream dest=
                new BufferedOutputStream(fos, BUFFER_SIZE);

              while (total+BUFFER_SIZE<=DEFAULT_MAX_SIZE &&
                (bytesRead=zis.read(data, 0, BUFFER_SIZE))!=-1) {
                dest.write(data, 0, bytesRead);
                total+=bytesRead;
              }

              dest.flush();
              fos.getFD().sync();
              dest.close();

              if (total+BUFFER_SIZE>DEFAULT_MAX_SIZE) {
                throw new IllegalStateException(
                  "Too much output from ZIP");
              }
            }

            zis.closeEntry();
            entries++;

            if (entries>DEFAULT_MAX_ENTRIES) {
              throw new IllegalStateException(
                "Too many entries in ZIP");
            }
          }
        }
      }
      finally {
        zis.close();
      }
    }
    catch (Throwable t) {
      if (destDir.exists()) {
        delete(destDir);
      }

      throw new UnzipException("Problem in unzip operation, rolling back", t);
    }
  }

  // inspired by http://pastebin.com/PqJyzQUx

  public static boolean delete(File f) {
    if (f.isDirectory()) {
      for (File child : f.listFiles()) {
        if (!delete(child)) {
          return(false);
        }
      }
    }

    return(f.delete());
  }

  public static boolean deleteContents(File f) {
    if (f.isDirectory()) {
      for (File child : f.listFiles()) {
        if (!delete(child)) {
          return(false);
        }
      }
    }

    return(true);
  }

  private static String validateZipEntry(String zipEntryRelativePath,
                                         File destDir) throws IOException {
    File zipEntryTarget=new File(destDir, zipEntryRelativePath);
    String zipCanonicalPath=zipEntryTarget.getCanonicalPath();

    if (zipCanonicalPath.startsWith(destDir.getCanonicalPath())) {
      return(zipCanonicalPath);
    }

    throw new IllegalStateException("ZIP entry tried to write outside destination directory");
  }

  public static class UnzipException extends Exception {
    public UnzipException(String detailMessage, Throwable throwable) {
      super(detailMessage, throwable);
    }
  }
}
