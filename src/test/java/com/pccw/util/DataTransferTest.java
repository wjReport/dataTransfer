package com.pccw.util;

import org.junit.Test;

import com.pccw.pojo.TransferPojo;

public class DataTransferTest {

	@Test
	public void executeQuery(){
		DbHelper h = new DbHelper();
		h.executeQuery(new TransferPojo("system", "gl_balances", null, null));
	}
	
	@Test
	public void transfer(){
		Transfer t = new Transfer();
		TransferPojo src = new TransferPojo("system", "gl_balances", null, null);
		TransferPojo target = new TransferPojo("form", "gl_balances", null, null);
		t.transfer(src, target);
	}
	
	@Test
	public void transferMultiThread(){
		TransferWithMultiThread t = new TransferWithMultiThread();
		TransferPojo src = new TransferPojo("system", "gl_balances", null, null);
		TransferPojo target = new TransferPojo("form", "gl_balances", null, null);
		t.transfer(src, target);
	}
}
