package com.pccw.util;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.pccw.pojo.PropertiesPojo;
import com.pccw.pojo.TransferPojo;

public class TransferWithMultiThread {
	
	Logger log = Logger.getLogger(TransferWithMultiThread.class);
	
	public void transfer(PropertiesPojo p){
		this.transfer(p.getSrc(), p.getTarget());
	}
	
	public void transfer(TransferPojo src, TransferPojo target){
		long time = System.currentTimeMillis();
		new DbHelper().executeQuery(src, rs ->{
			ExecutorService service = Executors.newFixedThreadPool(InsertTask.queueSize);
			try {
				if (rs != null) {
					ResultSetMetaData rsmd = rs.getMetaData();
					int cc = rsmd.getColumnCount();
					String insertSql = DbHelper.getInserSql(target, rsmd);
					List<List<Object>> list = new ArrayList<>();
					while (rs.next()) {
						List<Object> elements = new ArrayList<>(cc);
						list.add(elements);
						for (int i = 1; i <= cc; i++) {
							elements.add(rs.getObject(i));
						}
						if(rs.getRow()%5000==0){
							service.submit(new InsertTask(target.getDbName(), insertSql, list));
							log.info("提交了"+rs.getRow()+"行");
							list = new ArrayList<>();
						}
					}
					service.submit(new InsertTask(target.getDbName(), insertSql, list));
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}finally{
				service.shutdown();
			}
		});
		System.out.println("耗时："+(System.currentTimeMillis()-time));
	}
}
