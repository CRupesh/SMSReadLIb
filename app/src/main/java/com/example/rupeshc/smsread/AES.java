package com.example.rupeshc.smsread;

import android.os.Build;
import android.util.Base64;
import android.util.Log;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by user on 18-12-2015.
 */
public class AES {

    //AES/CBC/PKCS5Padding
    //PBKDF2WithHmacSHA1
    private static final String ALGO = "AES";
/*

    public static String encrypt(String Data, String encKey){

        String encryptedValue =null;
        Cipher c = null;
        try {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(256); // The AES key size in number of bits
            SecretKey secKey = generator.generateKey();
            Log.e("TAG", secKey + "");
            c = Cipher.getInstance(ALGO);
            c.init(Cipher.ENCRYPT_MODE, secKey);
            byte[] encVal = c.doFinal(Data.getBytes());
            encryptedValue = Base64.encodeToString(encVal,Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return encryptedValue;
    }

    public static String decrypt(String encryptedData, String encKey) {
        String decryptedValue = null;
        Cipher c = null;

        try {
            Key key = generateKey(encKey);
            c = Cipher.getInstance(ALGO);
            c.init(Cipher.DECRYPT_MODE, key);
            byte[] decordedValue = Base64.decode(encryptedData, Base64.DEFAULT);
            byte[] decValue = c.doFinal(decordedValue);
            decryptedValue = new String(decValue);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return decryptedValue;
    }
*/

    private static Key generateKey(String encKey) throws Exception {
        Key key = new SecretKeySpec(encKey.getBytes(), ALGO);
        return key;
    }

    public static SecretKey getSecretEncryptionKey() throws Exception{
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(128); // The AES key size in number of bits
        SecretKey secKey = generator.generateKey();
        return secKey;
    }

    public static String encryptText(String plainText,SecretKey secKey) throws Exception{
        // AES defaults to AES/ECB/PKCS5Padding in Java 7
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, secKey);
        byte[] encVal = aesCipher.doFinal(plainText.getBytes());
        String encryptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);
        return encryptedValue;
    }

    public static String decryptText(String encryptedData, SecretKey secKey) throws Exception {
        // AES defaults to AES/ECB/PKCS5Padding in Java 7
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, secKey);
        byte[] decordedValue = Base64.decode(encryptedData, Base64.DEFAULT);
        byte[] decValue = aesCipher.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    private static final String TAG = AES.class.getName();
    private static boolean D = true;
    private static final String ALGORITHM_FOR_KEY_GENERATOR = "AES";
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static byte[] rawKey;

    public static String encrypt(String cleartext, String seed) {
        if(cleartext == null || cleartext.isEmpty())
            return "";
        try {
            if (D) Log.d(TAG + " encrypt", "" + cleartext);
            if(rawKey == null){
                rawKey = getRawKey(seed.getBytes());
            }
            byte[] result = encrypt(rawKey, cleartext.getBytes());
            String resultStr = Base64.encodeToString(result, Base64.DEFAULT);
            //String resultStr = toHex(result);
            if (D) Log.d(TAG + " encrypted", "" + resultStr);
            return resultStr;
        }catch(Exception e){
            if(D) e.printStackTrace();
            return cleartext;
        }
    }

    public static String decrypt(String seed, String encrypted) {
        if(encrypted == null || encrypted.isEmpty())
            return "";
        try {
            if (D) Log.d(TAG + " decrypt", "" + encrypted);
            if(rawKey == null){
                rawKey = getRawKey(seed.getBytes());
            }
            byte[] enc = Base64.decode(encrypted, Base64.DEFAULT);
            byte[] result = decrypt(rawKey, enc);
            String resultStr = new String(result);
            if (D) Log.d(TAG + " decrypted", "" + resultStr);
            return resultStr;
        }catch(Exception e){
            if(D) e.printStackTrace();
            return encrypted;
        }
    }

    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM_FOR_KEY_GENERATOR);
        SecureRandom sr = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            sr = SecureRandom.getInstance("SHA1PRNG", new CryptoProvider());
        } else {
            sr = SecureRandom.getInstance("SHA1PRNG");
        }
        sr.setSeed(seed);
        kgen.init(128, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;
    }


    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        byte[] ivByte = {0x00, 0x01, 0x02, 0x03, 0x00, 0x01, 0x02, 0x03, 0x00, 0x01, 0x02, 0x03, 0x00, 0x01, 0x02, 0x03};
        IvParameterSpec ivParamsSpec = new IvParameterSpec(ivByte);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParamsSpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }
    private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        byte[] ivByte = {0x00, 0x01, 0x02, 0x03, 0x00, 0x01, 0x02, 0x03, 0x00, 0x01, 0x02, 0x03, 0x00, 0x01, 0x02, 0x03};
        IvParameterSpec ivParamsSpec = new IvParameterSpec(ivByte);;
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivParamsSpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

}
