package cn.edu.zju.webcube.server.utils;

import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.logging.Logger;

import org.apache.commons.dbcp.BasicDataSource;

import cn.edu.zju.webcube.server.utils.Props;

public class DataSourceUtils {

    public static BasicDataSource getDataSource(String DataSourceType) {

        BasicDataSource dataSource = null;

        Props p = Props.getInstance();
        HashMap<String, String> props = p.get_current();

        if (DataSourceType.equals("hive")) {

			String port = props.get("hive.port");
            String host = props.get("hive.host");
			String database = props.get("hive.database");
			String user = props.get("hive.user");
			String password = props.get("hive.password");
			String numConnections = props.get("hive.numconnections");
            dataSource = getHiveDataSource(host, port, database,
                    user, password, numConnections);

        } else if (DataSourceType.equalsIgnoreCase("mysql")) {
	        String port = props.get("mysql.port");
	        String host = props.get("mysql.host");
	        String database = props.get("mysql.database");
			String user = props.get("mysql.user");
	        String password = props.get("mysql.password");
	        String numConnections = props.get("mysql.numconnections");

			dataSource = getMysqlDataSource(host, port, database, user, password, numConnections);

        }
        // There could be other dataSouce, if so, add the codes here
        return dataSource;
    }

    public static BasicDataSource getHiveDataSource(String host, String port,
                                                    String dbName, String user, String password, String numConnections) {
        return new HiveBasicDataSource(host, port, dbName, user, password,
                numConnections);
    }

	public static BasicDataSource getMysqlDataSource(String host, String port,
	                                                String dbName, String user, String password, String numConnections) {
		return new MysqlBasicDataSource(host, port, dbName, user, password,
				numConnections);
	}



    public static class HiveBasicDataSource extends BasicDataSource {

        private HiveBasicDataSource(String host, String port, String dbName,
                                    String user, String password, String numConnections) {
            super();
            String url = "jdbc:hive2://" + (host + ":" + port + "/" + dbName);
            System.out.println(url);
            addConnectionProperty("useUnicode", "yes");
            addConnectionProperty("characterEncoding", "UTF-8");
            setDriverClassName("org.apache.hive.jdbc.HiveDriver");
            setUsername(user);
            setPassword(password);
            setUrl(url);
            setMaxActive(Integer.valueOf(numConnections));
            setTestOnBorrow(true);

        }

        public String getDBType() {
            return "hive";
        }

	    @Override
	    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		    return null;
	    }
    }

	public static class MysqlBasicDataSource extends BasicDataSource {

		private MysqlBasicDataSource(String host, String port, String dbName,
		                            String user, String password, String numConnections) {
			super();
			String url = "jdbc:mysql://" + (host + ":" + port + "/" + dbName);
			addConnectionProperty("useUnicode", "yes");
			addConnectionProperty("characterEncoding", "UTF-8");
			setDriverClassName("com.mysql.jdbc.Driver");
			setUsername(user);
			setPassword(password);
			setUrl(url);
			setMaxActive(Integer.valueOf(numConnections));
			setTestOnBorrow(true);

		}

		public String getDBType() {
			return "mysql";
		}

		@Override
		public Logger getParentLogger() throws SQLFeatureNotSupportedException {
			return null;
		}
	}


}