package net.mofancy.analysis.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.BinaryCodec;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.net.URLCodec;

/**
 * 依赖apache common Codec工具包
 * @author wangfei
 *
 */
public class CryptUtils {
	private static final String CHARSET = StandardCharsets.UTF_8.name();
	private static final String IV_STRING_DES = "alleluia"; // must be 8 bytes long
	private static final String IV_STRING_AES = "GodCreatedHeaven"; // must be 16 bytes long
	public static final String TRANS_DES_ECB_PKCS5Padding = "DES/ECB/PKCS5Padding";
	public static final String TRANS_DES_CBC_PKCS5Padding = "DES/CBC/PKCS5Padding";
	public static final String TRANS_AES_ECB_PKCS5Padding = "AES/ECB/PKCS5Padding";
	public static final String TRANS_AES_CBC_PKCS5Padding = "AES/CBC/PKCS5Padding";
	/* NoPadding模式对加密数据长度有严格要求 此处不采用此类模式
	public static final String TRANS_DES_ECB_NoPadding = "DES/ECB/NoPadding";
	public static final String TRANS_DES_CBC_NoPadding = "DES/CBC/NoPadding";
	public static final String TRANS_AES_ECB_NoPadding = "AES/ECB/NoPadding";
	public static final String TRANS_AES_CBC_NoPadding = "AES/CBC/NoPadding";*/

	public static String encodeBase64Byte(byte[] data) {
		return Base64.encodeBase64String(data);
	}
	
	public static byte[] decodeBase64Byte(String base64String) {
		return Base64.decodeBase64(base64String);
	}
	
	/**
	 * Base64编码
	 * @param source 待编码的字符串
	 * @return
	 */
	public static String encodeBase64(String source) {
		String dest = "";
		try {
			dest = Base64.encodeBase64String((source.getBytes(StandardCharsets.UTF_8.name())));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return dest;
	}
	
	/**
	 * Base64解码
	 * @param base64String 待解码的字符串
	 * @return
	 */
	public static String decodeBase64(String base64String) {
		return new String(Base64.decodeBase64(base64String));
	}
	
	/**
	 * MD5编码
	 * @param source 待编码的字符串
	 * @return
	 */
	public static String md5Hex(String source) {
		return DigestUtils.md5Hex(source);
	}
	
	/**
	 * MD5编码 可用来计算文件的MD5编码值
	 * @param data
	 * @return
	 */
	public static String md5Hex(byte[] data) {
		return DigestUtils.md5Hex(data);
	}
	
	/**
	 * sha1编码
	 * @param source
	 * @return
	 */
	public static String sha1Hex(String source) {
		return DigestUtils.sha1Hex(source);
	}
	
	/**
	 * sha1编码 可用来计算文件的sha1编码值
	 * @param data
	 * @return
	 */
	public static String sha1Hex(byte[] data) {
		return DigestUtils.sha1Hex(data);
	}
	
	/**
	 * 编码URL
	 * @param url
	 * @return
	 */
	public static String encodeUrl(String url) {
		String dest = "";
		try {
			dest = (new URLCodec(CHARSET)).encode(url);
		} catch (EncoderException e) {
			e.printStackTrace();
		}
		return dest;
	}
	
	/**
	 * 解码URL
	 * @param encodedUrl
	 * @return
	 */
	public static String decodeUrl(String encodedUrl) {
		String dest = "";
		try {
			dest = (new URLCodec(CHARSET)).decode(encodedUrl);
		} catch (DecoderException e) {
			e.printStackTrace();
		}
		return dest;
	}
	
	public static String encodeHex(byte[] data) {
		return Hex.encodeHexString(data);
	}
	
	public static byte[] decodeHex(String hex) {
		byte[] dest = null;
		try {
			dest =  Hex.decodeHex(hex.toCharArray());
		} catch (DecoderException e) {
			e.printStackTrace();
		}
		return dest;
	}
	
	/**
	 * 将字符串编码为二进制字符串(0和1)<br />
	 * 只能编码ASCII字符串
	 * @param source
	 * @return
	 */
	public static String encodeToBinaryString(String source) {
		String dest = "";
		try {
			dest = BinaryCodec.toAsciiString(source.getBytes(CHARSET));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return dest;
	}
	
	/**
	 * 解码二进制字符串(0和1)为ASCII字符串
	 * @param binaryString
	 * @return
	 */
	public static String decodeFromBinaryString(String binaryString) {
		return new String(BinaryCodec.fromAscii(binaryString.toCharArray()));
	}
	
	/**
	 * @param algorithmName 算法名称 如 DES, AES
	 * @param opmode
	 * @param key
	 * @param data
	 * @return
	 */
	private static byte[] crypt(String algorithmName, String transformation, int opmode, byte[] key, byte[] data) {
		byte[] dest = null;
		try {
			Cipher cipher = _getCipher(algorithmName, transformation, opmode, key);
//			cipher.update(data);
			dest = cipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dest;
	}
	
	private static Cipher _getCipher(String algorithmName, String transformation, int opmode, byte[] key) 
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, 
			InvalidKeySpecException, InvalidAlgorithmParameterException {
		Cipher cipher = Cipher.getInstance(transformation);
		SecretKey secretKey = _getSecretKey(algorithmName, key);
		if ("CBC".equals(transformation.split("/")[1])) {
			// transformation 为CBC模式时 需要 IV
			IvParameterSpec iv = _getIvParameterSpec(algorithmName);
			cipher.init(opmode, secretKey, iv);
		} else {
			cipher.init(opmode, secretKey);
		}
		return cipher;
	}
	
	private static SecretKey _getSecretKey(String algorithmName, byte[] key) throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException {
		SecretKey secretKey = null;
		if ("DES".equals(algorithmName)) {
			DESKeySpec dks = new DESKeySpec(key);
			secretKey = SecretKeyFactory.getInstance("DES").generateSecret(dks);
		} else if ("AES".equals(algorithmName)) {
			secretKey = new SecretKeySpec(key, "AES");
		}
		return secretKey;
	}
	
	public static IvParameterSpec _getIvParameterSpec(String algorithmName) {
		IvParameterSpec iv = null;
		try {
			if ("DES".equals(algorithmName)) {
				iv = new IvParameterSpec(IV_STRING_DES.getBytes(CHARSET));
			} else if ("AES".equals(algorithmName)) {
				iv = new IvParameterSpec(IV_STRING_AES.getBytes(CHARSET));
			}
		} catch (UnsupportedEncodingException ue) {
			ue.printStackTrace();
		}
		return iv;
	}
	
	public static byte[] encodeDES(String transformation, byte[] key, byte[] data) {
		if (key == null || data == null) {return null;}
		if (key.length < 8) {throw new IllegalArgumentException("key must be more than 8 bytes long");}
		return crypt("DES", transformation, Cipher.ENCRYPT_MODE, key, data);
	}
	
	public static byte[] encodeDES(String transformation, String key, String data) {
		byte[] dest = null;
		try {
			dest = encodeDES(transformation, key.getBytes(CHARSET), data.getBytes(CHARSET));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return dest;
	}
	
	/**
	 * DES加密,采用 DES/ECB/PKCS5Padding 模式
	 * @param key 加密时使用的密码, 长度必须>=8位
	 * @param data 待加密的字符串
	 * @return
	 */
	public static byte[] encodeDES(String key, String data) {
		return encodeDES(TRANS_DES_ECB_PKCS5Padding, key, data);
	}
	
	public static byte[] decodeDES(String transformation, byte[] key, byte[] data) {
		if (key == null || data == null) {return null;}
		if (key.length < 8) {throw new IllegalArgumentException("key must be more than 8 bytes long");}
		return crypt("DES", transformation, Cipher.DECRYPT_MODE, key, data);
	}
	
	public static String decodeDES(String transformation, String key, byte[] data) {
		byte[] dest = null;
		try {
			dest = decodeDES(transformation, key.getBytes(CHARSET), data);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new String(dest);
	}
	
	/**
	 * DES解密,采用 DES/ECB/PKCS5Padding 模式
	 * @param key 解密时用的密码, 长度必须>=8位
	 * @param data 待解密的字符串
	 * @return
	 */
	public static String decodeDES(String key, byte[] data) {
		return decodeDES(TRANS_DES_ECB_PKCS5Padding, key, data);
	}
	
	public static byte[] encodeAES(String transformation, byte[] key, byte[] data) {
		if (key == null || data == null) {return null;}
		if (key.length != 16) {throw new IllegalArgumentException("key must be 16 bytes long");}
		return crypt("AES", transformation, Cipher.ENCRYPT_MODE, key, data);
	}
	
	public static byte[] encodeAES(String transformation, String key, String data) {
		byte[] dest = null;
		try {
			dest = encodeAES(transformation, key.getBytes(CHARSET), data.getBytes(CHARSET));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return dest;
	}
	
	/**
	 * AES加密, 采用 AES/ECB/PKCS5Padding 模式
	 * @param key 加密时使用的密码
	 * @param data 待加密的字符串
	 * @return
	 */
	public static byte[] encodeAES(String key, String data) {
		return encodeAES(TRANS_AES_ECB_PKCS5Padding, key, data);
	}
	
	public static byte[] decodeAES(String transformation, byte[] key, byte[] data) {
		if (key == null || data == null) {return null;}
		if (key.length != 16) {throw new IllegalArgumentException("key must be 16 bytes long");}
		return crypt("AES", transformation, Cipher.DECRYPT_MODE, key, data);
	}
	
	public static String decodeAES(String transformation, String key, byte[] data) {
		byte[] src = null;
		try {
			src = decodeAES(TRANS_AES_ECB_PKCS5Padding, key.getBytes(CHARSET), data);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new String(src);
	}
	
	/**
	 * AES解密, 采用 AES/ECB/PKCS5Padding 模式
	 * @param key 解密时使用的密码
	 * @param data 待解密的字符串
	 * @return
	 */
	public static String decodeAES(String key, byte[] data) {
		return decodeAES(TRANS_AES_ECB_PKCS5Padding, key, data);
	}
	
	/* demo, don't delete
	public static byte[] encodeDES(byte[] key, byte[] data) {
		if (key == null || data == null) {return null;}
		if (key.length < 8) {throw new IllegalArgumentException("key must be more than 8 bytes long");}
		byte[] dest = null;
		try {
			DESKeySpec dks = new DESKeySpec(key);
			SecretKey secretKey = SecretKeyFactory.getInstance("DES").generateSecret(dks);
			IvParameterSpec iv = new IvParameterSpec(IV_STRING.getBytes(CHARSET));
			Cipher cipher = Cipher.getInstance(TRANSFORMATION_DES);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
			cipher.update(data);
			dest = cipher.doFinal();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dest;
	}*/
	
	
	public static void main(String[] args) throws Exception {
		
	}
	
	
}
