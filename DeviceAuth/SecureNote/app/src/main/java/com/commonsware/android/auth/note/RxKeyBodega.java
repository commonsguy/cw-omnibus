/***
 Copyright (c) 2018 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.commonsware.android.auth.note;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

class RxKeyBodega {
  private static final String KEYSTORE="AndroidKeyStore";
  private static final int BLOCK_SIZE;

  static {
    int blockSize=-1;

    try {
      blockSize=Cipher.getInstance("AES/CBC/PKCS7Padding").getBlockSize();
    }
    catch (Exception e) {
      Log.e("RxKeyBodega", "Could not get AES/CBC/PKCS7Padding cipher", e);
    }

    BLOCK_SIZE=blockSize;
  }

  static Observable<EncryptionResult> encrypt(byte[] toEncrypt, String keyName,
                                              int timeout) {
    return Observable.create(new EncryptObservable(keyName, timeout, toEncrypt));
  }

  static Observable<byte[]> decrypt(EncryptionResult toDecrypt, String keyName) {
    return Observable.create(new DecryptObservable(keyName, toDecrypt));
  }

  static EncryptionResult load(File f) throws Exception {
    BufferedSource source=Okio.buffer(Okio.source(f));
    byte[] iv=source.readByteArray(BLOCK_SIZE);
    byte[] encrypted=source.readByteArray();

    source.close();

    return new EncryptionResult(iv, encrypted);
  }

  static void save(File f, EncryptionResult result)
    throws IOException {
    BufferedSink sink=Okio.buffer(Okio.sink(f));

    sink.write(result.iv);
    sink.write(result.encrypted);
    sink.close();
  }

  static class EncryptionResult {
    final byte[] iv;
    final byte[] encrypted;

    EncryptionResult(byte[] iv, byte[] encrypted) {
      this.iv=iv;
      this.encrypted=encrypted;
    }
  }

  private abstract static class BodegaObservable {
    KeyStore ks;
    Exception initException;

    BodegaObservable() {
      try {
        ks=KeyStore.getInstance(KEYSTORE);
        ks.load(null);
      }
      catch (Exception e) {
        initException=e;
      }
    }
  }

  private static class EncryptObservable extends BodegaObservable implements
    ObservableOnSubscribe<EncryptionResult> {
    final private String keyName;
    final private int timeout;
    final private byte[] toEncrypt;

    private EncryptObservable(String keyName, int timeout, byte[] toEncrypt) {
      this.keyName=keyName;
      this.timeout=timeout;
      this.toEncrypt=toEncrypt;
    }

    @Override
    public void subscribe(ObservableEmitter<EncryptionResult> emitter)
      throws Exception {
      if (initException==null) {
        createKey(keyName, timeout);

        SecretKey secretKey=(SecretKey)ks.getKey(keyName, null);
        Cipher cipher=Cipher.getInstance("AES/CBC/PKCS7Padding");
        SecureRandom rand=new SecureRandom();
        byte[] iv=new byte[BLOCK_SIZE];

        rand.nextBytes(iv);

        IvParameterSpec ivParams=new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParams);
        emitter.onNext(new EncryptionResult(ivParams.getIV(), cipher.doFinal(toEncrypt)));
      }
      else {
        throw initException;
      }
    }

    private void createKey(String keyName, int timeout) throws Exception {
      KeyStore.Entry entry=ks.getEntry(keyName, null);

      if (entry==null) {
        KeyGenParameterSpec spec=
          new KeyGenParameterSpec.Builder(keyName,
            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .setUserAuthenticationRequired(true)
            .setUserAuthenticationValidityDurationSeconds(timeout)
            .setRandomizedEncryptionRequired(false)
            .build();

        KeyGenerator keygen=
          KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE);

        keygen.init(spec);
        keygen.generateKey();
      }
    }
  }

  private static class DecryptObservable extends BodegaObservable implements
    ObservableOnSubscribe<byte[]> {
    final private String keyName;
    final private EncryptionResult toDecrypt;

    private DecryptObservable(String keyName, EncryptionResult toDecrypt) {
      this.keyName=keyName;
      this.toDecrypt=toDecrypt;
    }

    @Override
    public void subscribe(ObservableEmitter<byte[]> emitter)
      throws Exception {
      if (initException==null) {
        SecretKey secretKey=(SecretKey)ks.getKey(keyName, null);
        Cipher cipher=Cipher.getInstance("AES/CBC/PKCS7Padding");

        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(toDecrypt.iv));
        emitter.onNext(cipher.doFinal(toDecrypt.encrypted));
      }
      else {
        throw initException;
      }
    }
  }
}
