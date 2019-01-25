package blog.csdn.net.mchenys.common.utils;

import android.util.Base64;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * RSA加密工具类
 * lcn
 * Created by user on 2018/1/3.
 */

public class RSAUtils {
    public static byte[] processData(byte[] srcData, Key key, int mode) {

        //用来保存处理结果
        byte[] resultBytes = null;

        try {

            //构建Cipher对象，需要传入一个字符串，格式必须为"algorithm/mode/padding"或者"algorithm/",意为"算法/加密模式/填充方式"
            Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");
            //初始化Cipher，mode指定是加密还是解密，key为公钥或私钥
            cipher.init(mode, key);
            //处理数据
            resultBytes = cipher.doFinal(srcData);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return resultBytes;
    }

        /*
        将字符串形式的公钥转换为公钥对象
     */

    public static PublicKey keyStrToPublicKey(String publicKeyStr) {

        PublicKey publicKey = null;

        byte[] keyBytes = Base64.decode(publicKeyStr, Base64.DEFAULT);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

        try {
            publicKey = KeyFactory.getInstance("RSA").generatePublic(keySpec);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }
}
