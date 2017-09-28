package com.pccw.util;

import java.util.Map;

import org.junit.Test;

public class UtilTest {
	
	@Test
	public void getDbConfigByName(){
		DbManager dm = new DbManager();
		Map<String, String> retMap = dm.getDBConnectionByName("system");
		System.out.println(retMap.toString());
	}
}
