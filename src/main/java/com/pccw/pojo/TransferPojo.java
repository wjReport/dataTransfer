package com.pccw.pojo;

public class TransferPojo {
	private String dbName;
	private String table;
	private String sql;
	private String[] params;
	
	public TransferPojo() {
		super();
	}

	public TransferPojo(String dbName, String table, String sql, String[] params) {
		super();
		this.dbName = dbName;
		this.table = table;
		this.sql = sql;
		this.params = params;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String[] getParams() {
		return params;
	}

	public void setParams(String[] params) {
		this.params = params;
	}
	
	
}
