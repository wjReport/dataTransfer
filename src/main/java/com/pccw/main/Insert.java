package com.pccw.main;

import java.io.FileInputStream;
import java.io.IOException;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.pccw.pojo.PropertiesPojo;
import com.pccw.util.TransferWithMultiThread;

public class Insert {

	public static void main(String[] args) {
		if(args==null || args.length<1) throw new RuntimeException("请传入配置文件地址");
		try {
			PropertiesPojo p = JSONObject.parseObject(new FileInputStream(args[0]), PropertiesPojo.class, Feature.AllowComment);
			TransferWithMultiThread t = new TransferWithMultiThread();
			t.transfer(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
