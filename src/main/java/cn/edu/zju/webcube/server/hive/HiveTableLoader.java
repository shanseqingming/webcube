package cn.edu.zju.webcube.server.hive;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.commons.dbutils.DbUtils;

import cn.edu.zju.webcube.server.utils.BasicJdbcLoader;
import cn.edu.zju.webcube.shared.db.Column;
import cn.edu.zju.webcube.shared.db.Table;
import org.apache.log4j.Logger;

public class HiveTableLoader extends BasicJdbcLoader {
	private static  final Logger  logger = Logger.getLogger(HiveTableLoader.class);
	public HiveTableLoader(String databaseType) {
		super(databaseType);
	}

	public ArrayList<Table> queryAllTable() {

		String sql = "show tables";
		ArrayList<Table> tlist = new ArrayList<Table>();
		String tname;
		
		Connection conn = null;
		Statement tableStmt = null;
		Statement descStmt = null;
		ResultSet tableRes = null;
		ResultSet columnRes = null;

		try {
			conn = this.getDBConnection(false);
			if(conn == null){
				logger.error("no conn!");
			}
			else{
				logger.info("normal conn!");
			}
			tableStmt = conn.createStatement();
			descStmt = conn.createStatement();
			tableStmt.executeQuery("use test4");
			descStmt.executeQuery("use test4");
			tableRes = tableStmt.executeQuery(sql);
			while (tableRes.next()) {
				Table t = new Table(tableRes.getString(1));
				
				tname = tableRes.getString(1);
				//System.out.println(tname);
				columnRes = descStmt.executeQuery("describe " + tname);
				while (columnRes.next()) {
					Column c = new Column(t.getName(), columnRes.getString(1), columnRes.getString(2));
					t.addColumn(c);
				}
				tlist.add(t);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			DbUtils.closeQuietly(columnRes);
			DbUtils.closeQuietly(tableRes);
			DbUtils.closeQuietly(tableStmt);
			DbUtils.closeQuietly(descStmt);
			DbUtils.closeQuietly(conn);
			
		}

		return tlist;
	}

}
