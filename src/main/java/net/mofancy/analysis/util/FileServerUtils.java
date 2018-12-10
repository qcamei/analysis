package net.mofancy.analysis.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 上传文件到文件服务器,文件分类为图片,文档,音频,视频
 * @author wangfei
 *
 */
public class FileServerUtils {
	
	private static Logger log = LoggerFactory.getLogger(FileServerUtils.class);
	
	private static String SERVER_IP = "120.79.166.29";
	private static String USERNAME = "yjkftpuser";
	private static String PASSWORD = "yjKfTp_us!er";
	
	
	/**
	 * 上传图片
	 * @param bytes 图片字节数组
	 * @param extension 图片文件扩展名
	 * @return URL
	 */
	public static String uploadPic(byte[] bytes, String extension) {
		return uploadFile(bytes, extension, "pic");
	}
	
	/**
	 * 上传文档
	 * @param bytes 文档字节数组
	 * @param extension 文档文件扩展名
	 * @return URL
	 */
	public static String uploadDoc(byte[] bytes, String extension) {
		return uploadFile(bytes, extension, "doc");
	}
	
	/**
	 * 上传视频
	 * @param bytes 视频文件字节数组
	 * @param extension 视频文件扩展名
	 * @return URL
	 */
	public static String uploadVideo(byte[] bytes, String extension) {
		return uploadFile(bytes, extension, "video");
	}
	
	/**
	 * 上传音频文件
	 * @param bytes 音频文件字节数组
	 * @param extension 音频文件扩展名
	 * @return URL
	 */
	public static String uploadAudio(byte[] bytes, String extension) {
		return uploadFile(bytes, extension, "audio");
	}
	
	private static String uploadFile(byte[] bytes, String extension, String basePath) {
		if ((bytes == null) || (bytes.length == 0)) {
			log.info("上传数据为空,取消本次上传");
			return "";
		}
		String url = "";
		FTPClient ftp = new FTPClient();
		try {
			ftp.connect(SERVER_IP);
			ftp.login(USERNAME, PASSWORD);
			ftp.enterLocalPassiveMode();
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftp.setBufferSize(1024 * 1024 * 5); // 5M
			String path = basePath + "/" + DateFormatUtils.format(new Date(), "yyyy/MM/dd");
			makeDirectory(ftp, path);
			ftp.changeWorkingDirectory(path);
			String remoteFilename = UUID.randomUUID().toString().replaceAll("-", "") + "." + extension;
			try (InputStream is = new ByteArrayInputStream(bytes)) {
				boolean succ = ftp.storeFile(remoteFilename, is);
				if (!succ) {
					log.warn("FTP文件上传失败:" + path);
				}
			}
			ftp.logout();
			url = "http://" + SERVER_IP + "/" + path + "/" + remoteFilename;
			log.info(url);
		} catch (IOException e) {
			log.error("ftp上传异常", e);
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException e) {
					log.error("ftp未正常断开", e);
				}
			}
		}
		return url;
	}
	
	private static void makeDirectory(FTPClient ftp, String path) throws IOException {
		if (path.startsWith("/")) {
			path = path.substring(1); // 去掉第一位的/, 否则后面split时会多一个空串
		}
		String[] dirs = path.split("/");
		for (int i = 0; i < dirs.length; i++) {
			String newdir = "";
			for (int j = 0; j <= i; j++) {
				newdir += "/" + dirs[j];
			}
			ftp.makeDirectory(newdir.substring(1));
		}
	}
	
}
