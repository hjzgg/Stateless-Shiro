package com.hjzgg.stateless.common.esapi;

import java.io.IOException;

import javax.crypto.SecretKey;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;
import org.owasp.esapi.crypto.CipherText;
import org.owasp.esapi.crypto.PlainText;
import org.owasp.esapi.errors.EncryptionException;
import org.owasp.esapi.errors.IntegrityException;

/**
 * 
 * <p>
 * ESAPI加解密工具类
 * <p/>
 * 需要在ESAPI.properties中预先设置Encryptor.MasterKey和Encryptor.MasterSalt
 * 可以使用ESAPI下载包中的/src/examples/scripts/setMasterKey.sh设置后保存到配置文件。
 * #IYCPEncryptor.MasterKey=
 * #IYCPEncryptor.MasterSalt=
 * <p/>
 * 加密算法
 * IYCPEncryptor.EncryptionAlgorithm=AES
 * <p/>
 * 算法/模式/填充
 * IYCPEncryptor.CipherTransformation=AES/CBC/PKCS5Padding
 * <p/>
 * 秘钥长度（AES秘钥长度 128/192/256）
 * IYCPEncryptor.EncryptionKeyLength=128
 * <p/>
 * ESAPI加密的时候，允许使用的组合模式
 * 注意jdk1.5，这些模式都不支持！！这些模式中，NIST（美国国家标准及技术研究所）只认可了GCM和CCM。其他的实现方式各有差异。
 * IYCPEncryptor.cipher_modes.combined_modes=GCM,CCM,IAPM,EAX,OCB,CWC
 * <p/>
 * 额外的加密模式
 * IYCPEncryptor.cipher_modes.additional_allowed=CBC
 * <p/>
 * 由于ESAPI默认使用CCB模式，需要一个初始向量IV。（除去ECB外所有的模式都需要一个向量）。
 * 提供两种配置方式：
 * 1）使用一个双方都知道的向量（fixed）
 * 2）让ESAPI都使用随机向量(random)
 * 当向量不需要向对方隐藏的时候，注意重要的一点是，对方不允许选择同样的向量。
 * 通常，随机向量比固定向量更加有安全性。
 * 事实上，它是必不可少的，feed-back 密码模式如CFB和OFB在为每一个key加密的时候都使用不同的向量。在这种情况下，随机向量更优先。
 * 默认情况下，ESAPI 2采用随机向量。如果你想用固定向量设置加密。设置chooseivmethod =fixed并设置Encryptor.fixedIV
 * 向量有效值： random|fixed|(specified  -- 在2.1支持)
 * IYCPEncryptor.ChooseIVMethod=random
 * <p/>
 * 设置固定向量之后，必须为设置这个值。
 * 该值是一个16进制数字，长度必须和选用的加密算法的block size一致。
 * 比如,使用了AES-128/CCB ，设置了如下的一个值(16bytes==128bits)
 * IYCPEncryptor.fixedIV=0x000102030405060708090a0b0c0d0e0f
 * <p/>
 * 任何一个密文都需要一个MAC（信息校验码）。当对方从改变向量以及其他的方法改变消息时，解密会因此失败，因为对方使用了一个错误的key。
 * 它指的是在密文中单独计算或者存在的MAC，不是指的在任何组合模式下的计算结果。
 * 注意：如果您使用了ESAPI和FIPS 140-2加密模块，这个值必须设置为false
 * IYCPEncryptor.CipherText.useMAC=true
 * <p/>
 * 明文对象时候可以被覆盖并被GC回收。如果没有设置，默认为true。
 * IYCPEncryptor.PlainText.overwrite=true
 * <p/>
 * 对于DES来说，56bit的秘钥长度实在是太小了，所以，除非在非常不重视安全的情况下，不要使用DES。
 * #IYCPEncryptor.EncryptionKeyLength=56
 * #IYCPEncryptor.EncryptionAlgorithm=DES
 * <p/>
 * TripleDES 在众多的场景中被认识是足够安全的.
 * 注意：默认只用一个DESede 112-bit 版本. 使用 168-bit 的版本需要从sun下载一个特殊的权限。
 * #IYCPEncryptor.EncryptionKeyLength=168
 * #IYCPEncryptor.EncryptionAlgorithm=DESede
 * <p/>
 * 下面列举了ESAPI 生成秘钥的时候，使用的伪随机数算法。
 * 值得注意的是，这些伪随机数算法只是使用在生成秘钥的时候，不会在计算MAC的时候使用。（在生成MAC的时候，使用最多的算法是HmacSHA1，这是为了保证生成的信息尽量小）
 * 当前JDK1.5和1.6支持的算法有：
 * HmacSHA1 (160 bits), HmacSHA256 (256 bits), HmacSHA384 (384 bits), HmacSHA512 (512 bits).
 * 注意： HmacMD5 伪随机算法不能被ESAPI用于生成秘钥。即使它被jdk支持。
 * IYCPEncryptor.KDF.PRF=HmacSHA256
 * <p/>
 * IYCPEncryptor.HashAlgorithm=SHA-512
 * IYCPEncryptor.HashIterations=1024
 * IYCPEncryptor.DigitalSignatureAlgorithm=SHA1withDSA
 * IYCPEncryptor.DigitalSignatureKeyLength=1024
 * IYCPEncryptor.RandomAlgorithm=SHA1PRNG
 * IYCPEncryptor.CharacterEncoding=UTF-8
 * <p/>
 * <p>
 * @version 1.0
 * @time 2016年4月23日 下午3:51:19
 */
public class IYCPEncryptor {

    private static final org.owasp.esapi.Encryptor encryptor = ESAPI.encryptor();

    private static final Encoder encoder = ESAPI.encoder();

    private static final int hash_iterations = 1024;


    /**
     * 根据ESAPI中定义的算法进行加密
     * 默认为AES/CBC/PKCS5Padding
     * 使用默认秘钥ESAPI.properties 中的MasterKey
     * <p/>
     * 返回的秘钥中包括加密后的数据以及MAC
     *
     * @param text
     * @return
     * @throws EncryptionException
     */
    public String encrypt(String text) throws EncryptException {
        PlainText plainText = new PlainText(text);
        try {
            CipherText cipherText = encryptor.encrypt(plainText);
            return encoder.encodeForBase64(cipherText.asPortableSerializedByteArray(), false);
        } catch (EncryptionException e) {
            throw new EncryptException(e);
        }
    }


    /**
     * 使用自定义的秘钥加密数据
     * 使用默认秘钥ESAPI.properties 中的MasterKey
     * <p/>
     * 返回的秘钥中包括加密后的数据以及MAC
     *
     * @param key
     * @param text
     * @return
     * @throws EncryptionException
     */
    public String encrypt(SecretKey key, String text) throws EncryptException {
        PlainText plainText = new PlainText(text);
        CipherText cipherText = null;
        try {
            cipherText = encryptor.encrypt(key, plainText);
            return encoder.encodeForBase64(cipherText.asPortableSerializedByteArray(), false);
        } catch (EncryptionException e) {
            throw new EncryptException(e);
        }
    }

    /**
     * 解密数据
     * 注意：该方法只能解密该类中encry方法加密的数据
     *
     * @param data
     * @return
     * @throws EncryptionException
     */
    public String decrypt(byte[] data) throws EncryptException {
        try {
            PlainText plainText = encryptor.decrypt(CipherText.fromPortableSerializedBytes(data));
            return plainText.toString();
        } catch (EncryptionException e) {
            throw new EncryptException(e);
        }
    }

    /**
     * 解密数据
     * 注意：该方法只能解密该类中encry方法加密的数据
     *
     * @param text
     * @return
     * @throws IOException
     * @throws EncryptionException
     */
    public String decrypt(String text) throws IOException, EncryptException {
        byte[] data = encoder.decodeFromBase64(text);
        return decrypt(data);
    }

    /**
     * 根据自定义的秘钥解密数据
     * 注意：该方法只能解密该类中encry方法加密的数据
     *
     * @param key
     * @param data
     * @return
     * @throws EncryptionException
     */
    public String decrypt(SecretKey key, byte[] data) throws EncryptException {
        try {
            PlainText plainText = encryptor.decrypt(key, CipherText.fromPortableSerializedBytes(data));
            return plainText.toString();
        } catch (EncryptionException e) {
            throw new EncryptException(e);
        }
    }

    /**
     * 根据自定义的秘钥解密数据
     * 注意：该方法只能解密该类中encry方法加密的数据
     *
     * @param key
     * @param text
     * @return
     * @throws EncryptionException
     */
    public String decrypt(SecretKey key, String text) throws EncryptException, IOException {
        byte[] data = encoder.decodeFromBase64(text);
        return decrypt(key, data);
    }


    /**
     * hash
     *
     * @param text
     * @param salt
     * @return
     * @throws EncryptException
     */
    public String hash(String text, String salt) throws EncryptException {
        return hash(text, salt, hash_iterations);
    }

    /**
     * hash
     *
     * @param text
     * @param salt
     * @return
     * @throws EncryptException
     */
    public String hash(String text, String salt, int iterations) throws EncryptException {
        try {
            return encryptor.hash(text, salt, iterations);
        } catch (EncryptionException e) {
            throw new EncryptException(e);
        }
    }

    /**
     * 使用私钥进行签名
     *
     * @param text
     * @return
     * @throws EncryptException
     */
    public String sign(String text) throws EncryptException {
        try {
            return encryptor.sign(text);
        } catch (EncryptionException e) {
            throw new EncryptException(e);
        }
    }


    /**
     * 验证签名
     *
     * @param sign
     * @param data
     * @return
     */
    public boolean verifySignature(String sign, String data) {
        return encryptor.verifySignature(sign, data);
    }

    /**
     * 使用过期时间expiration对数据进行有时效性的加密
     * expiration是一个将来的时间到1970-1-1的ms
     *
     * @param text
     * @param expiration
     * @return
     * @throws EncryptException
     */
    public String seal(String text, long expiration) throws EncryptException {
        try {
            return encryptor.seal(text, expiration);
        } catch (IntegrityException e) {
            throw new EncryptException(e);
        }
    }

    /**
     * 对数据进行解密，如果当前时间now> seal时的expiration，数据过期
     *
     * @param text
     * @return
     * @throws EncryptException
     */
    public String unseal(String text) throws EncryptException {
        try {
            return encryptor.unseal(text);
        } catch (EncryptionException e) {
            throw new EncryptException(e);
        }
    }


    /**
     * 获得当前时间到1970-1-1的ms
     *
     * @return
     */
    public long getCurrentTime() {
        return encryptor.getTimeStamp();
    }

    /**
     * 获得当前时间到1970-1-1的ms + offset
     *
     * @param offset
     * @return
     */
    public long getRelativeTime(long offset) {
        return encryptor.getRelativeTimeStamp(offset);
    }
}
