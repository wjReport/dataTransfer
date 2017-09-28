package com.pccw.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.pccw.pojo.PropertiesPojo;
import com.pccw.pojo.TransferPojo;

public class Transfer {
	
	Logger log = Logger.getLogger(Transfer.class);
	
	public void transfer(PropertiesPojo p){
		this.transfer(p.getSrc(), p.getTarget());
	}
	
	public void transfer(TransferPojo src, TransferPojo target){
		new DbHelper().executeQuery(src, rs->{
			Connection con = null;
			PreparedStatement ps = null;
			try{
				ResultSetMetaData rsmd = rs.getMetaData();
				int cc = rsmd.getColumnCount();
				con = DbHelper.getConnection(target.getDbName());
				con.setAutoCommit(Boolean.FALSE);
				ps = con.prepareStatement(DbHelper.getInserSql(target, rsmd));
				int curRow=0;
				if (rs != null) {
					while (rs.next()) {
						curRow++;
						for(int i=1;i<=cc;i++){
							ps.setObject(i, rs.getObject(i));
						}
						ps.addBatch();
						if(curRow%5000==0){
							ps.executeBatch();
							con.commit();
							log.info("导入"+curRow+"条");
						}
					}
					ps.executeBatch();
					con.commit();
					log.info("导入"+curRow+"条");
				}
			}catch (Exception e) {
				throw new RuntimeException(e);
			}finally{
				try {
					if(ps != null) ps.close();
					if(con != null) con.close();
				} catch (SQLException e) {
					log.error("关闭连接时发生异常",e);
				}
			}
		});
	}
}
