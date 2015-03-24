package cn.edu.zju.webcube.server;

import java.util.ArrayList;

import cn.edu.zju.webcube.client.service.HiveQueryService;
import cn.edu.zju.webcube.server.hive.HiveCubeLoader;
import cn.edu.zju.webcube.shared.db.CubeManager;
import cn.edu.zju.webcube.shared.msg.CubeInfoMessage;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.apache.log4j.Logger;

/**
 * The server-side implementation of the RPC service.
 */

public class HiveQueryServiceImpl extends RemoteServiceServlet implements HiveQueryService{
	private static final Logger logger = Logger.getLogger(HiveQueryServiceImpl.class);
	@Override
	public boolean createCube(String sql, String cubeMsg)
			throws IllegalArgumentException {
		CubeInfoMessage cmsg = new CubeInfoMessage(cubeMsg);
		CubeManager cube = cmsg.getCube();
		HiveCubeLoader cloader = new HiveCubeLoader("hive");
		cloader.createCube(sql, cube);
		return true;
	}

	@Override
	public String submitQuery(String cubeID,
			ArrayList<String> ordinaryColumns, ArrayList<String> quantityColumns)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}


}
