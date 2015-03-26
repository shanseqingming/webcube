package cn.edu.zju.webcube.server.mysql;

import cn.edu.zju.webcube.server.utils.BasicJdbcLoader;
import cn.edu.zju.webcube.shared.db.CubeManager;
import cn.edu.zju.webcube.shared.msg.CubeInfoMessage;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by zhifei on 2/6/15.
 */
public class MysqlMetaLoader extends BasicJdbcLoader {
	private static final Logger logger = Logger.getLogger(MysqlMetaLoader.class);


	public MysqlMetaLoader(String databaseType) {
		super(databaseType);
	}



	public HashMap<String, CubeManager> getAllCubeMeta() {
		HashMap<String, CubeManager> cubeBuffer = new HashMap<String, CubeManager>();
		try {
			Connection conn = this.getDBConnection(true);
			QueryRunner queryRunner = this.createQueryRunner();
			cubeBuffer = queryRunner.query(conn, FetchAllCubeMetaHandler.QUERY_ALL_CUBE_MATA, new FetchAllCubeMetaHandler());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return cubeBuffer;
	}


	public CubeManager getCubeMeta(String cubeId){
		CubeManager cube = null;
		try {
			Connection conn = this.getDBConnection(true);
			QueryRunner queryRunner = this.createQueryRunner();
			cube = queryRunner.query(conn, FetchCubeMetaHandler.QUERY_CUBE_MATA, new FetchCubeMetaHandler(), cubeId);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return cube;
	}


	public void saveCubeBuffer(CubeManager cube) {
		final String INSERT_CUBE_META = "INSERT INTO cubeInfo (cubeId, columnInfo) VALUES (?,?)";
		try{
			Connection conn = getDBConnection(true);
			QueryRunner queryRunner = this.createQueryRunner();
			String cubeStr = new CubeInfoMessage(cube).serialize();
			queryRunner.update(conn, INSERT_CUBE_META, cube.getID(), cubeStr);


		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}


	private static class FetchAllCubeMetaHandler implements ResultSetHandler<HashMap<String, CubeManager>> {
		private static String QUERY_ALL_CUBE_MATA  =  "SELECT columnInfo FROM cubeInfo";

		@Override
		public HashMap<String, CubeManager> handle(ResultSet rs) throws SQLException {
			HashMap<String, CubeManager> buffer = new HashMap<String, CubeManager>();

			if (!rs.next()) {
				return buffer;
			}

			do {
				String line = rs.getString(1);
				CubeInfoMessage cmsg = new CubeInfoMessage(line);
				CubeManager cube = cmsg.getCube();
				buffer.put(cube.getID(), cube);

			} while(rs.next());

			return buffer;
		}
	}

	private static class FetchCubeMetaHandler implements ResultSetHandler<CubeManager> {
		private static String QUERY_CUBE_MATA  =  "SELECT columnInfo FROM cubeInfo where cubeId=?";

		@Override
		public CubeManager handle(ResultSet rs) throws SQLException {
			CubeManager cube;

			if (!rs.next()) {
				return null;
			}

			do {
				String line = rs.getString(1);
				CubeInfoMessage cmsg = new CubeInfoMessage(line);
				cube = cmsg.getCube();

			} while(rs.next());

			return cube;
		}
	}
}
