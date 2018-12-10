package net.mofancy.analysis.util;

/**
 * 用户密码工具类
 * @author wangfei
 *
 */
public class UserUtils {
	private static final String KEY = "yjkegGVwMcBEreN4";
	
	/**
	 * 加密手机号
	 * @param mobile
	 * @return
	 */
	public static String encodeMobile(String mobile) {
		return CryptUtils.encodeHex(CryptUtils.encodeAES(KEY, mobile));
	}
	
	/**
	 * 解密手机号
	 * @param encryptMobile
	 * @return
	 */
	public static String decodeMobile(String encryptMobile) {
		return CryptUtils.decodeAES(KEY, CryptUtils.decodeHex(encryptMobile));
	}
	
	public static void main(String[] args) {
//		System.out.println(encodeMobile("58352c80b48fb56e9b1e01d4a7cdfe79"));
		System.out.println(encodeMobile("18047581882"));
		System.out.println(encodeMobile("18258888260"));
		System.out.println(decodeMobile("4b5091f8a14008c02a921b29fab1b726"));
		System.out.println(CryptUtils.md5Hex("cBa63Ac5604"));
		System.out.println(CryptUtils.md5Hex("870828"));
		
	}
}
