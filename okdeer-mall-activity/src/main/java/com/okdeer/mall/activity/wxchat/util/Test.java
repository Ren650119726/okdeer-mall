
package com.okdeer.mall.activity.wxchat.util;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import com.qiniu.util.Auth;
import com.yschome.file.uploadFile;

public class Test {

	public static void main(String[] args) {
		Auth auth = Auth.create("OeIiUD1NqIj6XdTEQSgzgzV6fR2RKmY1tkMaNbMg", "oYxn4_yaL5D4Pqnb6CcdNjSInUXI9_Om5uqLb36Y");
		uploadFile uploadFile = new uploadFile();
		String token = auth.uploadToken("operate");
		File file = new File("");
		// 获取文件名
		String fileName = file.getName();
		// 获取文件后缀
		String subkey = fileName.substring(fileName.lastIndexOf("."));
		// 重新生成文件名称
		String name = String.valueOf(new Date().getTime());
		String key = name + subkey;
		// 文件
		byte[] data;
		try {
			data = FileUtils.readFileToByteArray(file);
			boolean flag = uploadFile.upload(data, key, token);
			System.out.println(flag);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
