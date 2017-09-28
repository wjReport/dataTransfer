package com.pccw.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.log4j.Logger;

import com.pccw.pojo.TransferPojo;

public class DbHelper {
	Logger log = Logger.getLogger(DbHelper.class);
	public static Connection getConnection(String dbName) throws SQLException, ClassNotFoundException {
		Map<String, String> map = DbManager.getDBConnectionByName(dbName);
		String driver = map.get("driver");
		String url = map.get("url");
		String username = map.get("username");
		String password = map.get("password");
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, username, password);
		return conn;
	}

	public static String getInserSql(TransferPojo target, ResultSetMetaData rsmd) throws SQLException{
		String insertSql = "insert into "+target.getTable()+" (#columns#) values (#placeHolders#)";
		StringBuilder columns = new StringBuilder();
		StringBuilder placeHolders = new StringBuilder();
		for(int i=1;i<=rsmd.getColumnCount();i++){
			columns.append(rsmd.getColumnLabel(i)+",");
			placeHolders.append("?,");
		}
		columns.deleteCharAt(columns.lastIndexOf(","));
		placeHolders.deleteCharAt(placeHolders.lastIndexOf(","));
		insertSql = insertSql.replace("#columns#", columns.toString()).replace("#placeHolders#", placeHolders.toString());
		return insertSql;
	}
	
	public void executeQuery(TransferPojo src, Consumer<ResultSet> consumer) {
		Connection conn = null;
		PreparedStatement stm = null;
		try {
			conn = getConnection(src.getDbName());
			String sql = src.getSql();
			if(sql==null || sql.equals("")){
				sql = "select * from "+src.getTable();
			}
			stm = conn.prepareStatement(sql,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			stm.setFetchSize(10000);
			stm.setFetchDirection(ResultSet.FETCH_REVERSE);
			if (src.getParams() != null) {
				for (int i = 1; i <= src.getParams().length; i++) {
					stm.setObject(i, src.getParams()[i - 1]);
				}
			}
			ResultSet rs = stm.executeQuery();
			consumer.accept(rs);
		} catch (Exception e) {
			log.error(e);
			throw new RuntimeException(e);
		}finally{
			try {
				if(stm != null) stm.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				log.error("关闭连接时发生异常",e);
			}
		}
	}

	public List<Map<String, Object>> executeQuery(TransferPojo src){
		long time = System.currentTimeMillis();
		final List<Map<String, Object>> list = new ArrayList<>();
		this.executeQuery(src, rs ->{
			try {
				if (rs != null) {
					ResultSetMetaData rsmd = rs.getMetaData();
					int cc = rsmd.getColumnCount();
					while (rs.next()) {
						Map<String, Object> retMap = new HashMap<>(cc);
						list.add(retMap);
						for (int i = 1; i <= cc; i++) {
							retMap.put(rsmd.getColumnLabel(i), rs.getObject(i));
						}
					}
				}
			} catch (SQLException e) {
				throw new RuntimeException("遍历获取数据时发生异常：",e);
			}
		});
		System.out.println("耗时："+(System.currentTimeMillis()-time));
		return list;
	}
}
