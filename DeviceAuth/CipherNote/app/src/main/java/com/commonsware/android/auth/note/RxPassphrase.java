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
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Arrays;
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

public class RxPassphrase {
  private static final String BASE36_SYMBOLS="abcdefghijklmnopqrstuvwxyz0123456789";
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

  static Observable<char[]> get(File encryptedFile, String keyName, int timeout) {
    return Observable.create(new RxPassphrase.PassphraseObservable(encryptedFile, keyName, timeout));
  }

  public static class PassphraseObservable implements ObservableOnSubscribe<char[]> {
    private final File encryptedFile;
    private final String keyName;
    private final int timeout;

    public PassphraseObservable(File encryptedFile, String keyName, int timeout) {
      this.encryptedFile=encryptedFile;
      this.keyName=keyName;
      this.timeout=timeout;
    }

    @Override
    public void subscribe(ObservableEmitter<char[]> emitter) throws Exception {
      KeyStore ks=KeyStore.getInstance(KEYSTORE);

      ks.load(null);

      if (encryptedFile.exists()) {
        load(ks, emitter);
      }
      else {
        create(ks, emitter);
      }
    }

    private void create(KeyStore ks, ObservableEmitter<char[]> emitter)
      throws Exception {
      SecureRandom rand=new SecureRandom();
      char[] passphrase=new char[128];

      for (int i=0; i<passphrase.length; i++) {
        passphrase[i]=BASE36_SYMBOLS.charAt(rand.nextInt(BASE36_SYMBOLS.length()));
      }

      createKey(ks, keyName, timeout);

      SecretKey secretKey=(SecretKey)ks.getKey(keyName, null);
      Cipher cipher=Cipher.getInstance("AES/CBC/PKCS7Padding");
      byte[] iv=new byte[BLOCK_SIZE];

      rand.nextBytes(iv);

      IvParameterSpec ivParams=new IvParameterSpec(iv);

      cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParams);

      byte[] toEncrypt=toBytes(passphrase);
      byte[] encrypted=cipher.doFinal(toEncrypt);

      BufferedSink sink=Okio.buffer(Okio.sink(encryptedFile));

      sink.write(iv);
      sink.write(encrypted);
      sink.close();

      emitter.onNext(passphrase);
    }

    private void createKey(KeyStore ks, String keyName, int timeout)
      throws Exception {
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

    private void load(KeyStore ks, ObservableEmitter<char[]> emitter)
      throws Exception {
      BufferedSource source=Okio.buffer(Okio.source(encryptedFile));
      byte[] iv=source.readByteArray(BLOCK_SIZE);
      byte[] encrypted=source.readByteArray();

      source.close();

      SecretKey secretKey=(SecretKey)ks.getKey(keyName, null);
      Cipher cipher=Cipher.getInstance("AES/CBC/PKCS7Padding");

      cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

      byte[] decrypted=cipher.doFinal(encrypted);
      char[] passphrase=toChars(decrypted);

      emitter.onNext(passphrase);
    }
  }

  // based on https://stackoverflow.com/a/9670279/115145

  static byte[] toBytes(char[] chars) {
    CharBuffer charBuffer=CharBuffer.wrap(chars);
    ByteBuffer byteBuffer=Charset.forName("UTF-8").encode(charBuffer);
    byte[] bytes=Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(),
      byteBuffer.limit());

//    Arrays.fill(charBuffer.array(), '\u0000'); // clear the cleartext
    Arrays.fill(byteBuffer.array(), (byte) 0); // clear the ciphertext

    return bytes;
  }

  static char[] toChars(byte[] bytes) {
    Charset charset=Charset.forName("UTF-8");
    ByteBuffer byteBuffer=ByteBuffer.wrap(bytes);
    CharBuffer charBuffer=charset.decode(byteBuffer);
    char[] chars=Arrays.copyOf(charBuffer.array(), charBuffer.limit());

    Arrays.fill(charBuffer.array(), '\u0000'); // clear the cleartext
    Arrays.fill(byteBuffer.array(), (byte) 0); // clear the ciphertext

    return chars;
  }
}

