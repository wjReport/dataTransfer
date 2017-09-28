package com.pccw.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class InsertTask implements Runnable {
	public static final int queueSize = 3;
	public static final BlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(queueSize);
	private List<List<Object>> list;
	private Connection conn;
	private String sql ;
	
	public InsertTask(String dbName,String sql,List<List<Object>> list) throws Exception {
		super();
		this.conn = DbHelper.getConnection(dbName);
		this.conn.setAutoCommit(Boolean.FALSE);
		this.sql = sql;
		this.list = list;
		queue.put(0);
	}

	@Override
	public void run() {
		try(PreparedStatement ps = conn.prepareStatement(this.sql)) {
			//从队列中取出list数据
			if(list != null){
				for(List<Object> elements : list){
					for(int i=0;i<elements.size();i++){
						ps.setObject(i+1, elements.get(i));
					}
					ps.addBatch();
				}
				ps.executeBatch();
				this.conn.commit();
				queue.take();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}finally{
			this.closeConnection();
		}
	}

	public void closeConnection(){
		if(this.conn != null){
			try {
				this.conn.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
