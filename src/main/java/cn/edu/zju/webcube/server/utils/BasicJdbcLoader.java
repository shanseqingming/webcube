package cn.edu.zju.webcube.server.utils;

import java.io.IOException;
import java.sql.Connection;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;

public class BasicJdbcLoader {
	private BasicDataSource dataSource;

	public BasicJdbcLoader(String databaseType) {
		
		dataSource = DataSourceUtils.getDataSource(databaseType);
	}

	protected Connection getDBConnection(boolean autoCommit) throws IOException {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(autoCommit);
		} catch (Exception e) {
			DbUtils.closeQuietly(connection);
			throw new IOException("Error getting DB connection.", e);
		}

		return connection;
	}

	protected QueryRunner createQueryRunner() {
		return new QueryRunner(dataSource);
	}

	
}
