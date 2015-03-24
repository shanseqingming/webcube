package cn.edu.zju.webcube.server;

import java.util.ArrayList;

import cn.edu.zju.webcube.client.service.HiveCubeListService;
import cn.edu.zju.webcube.server.hive.HiveCubeLoader;
import cn.edu.zju.webcube.shared.db.CubeManager;
import cn.edu.zju.webcube.shared.msg.CubeInfoMessage;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;


/**
 * The server-side implementation of the RPC service.
 */
public class HiveCubeListServiceImpl extends RemoteServiceServlet implements HiveCubeListService{

	@Override
	public ArrayList<String> getCubeList() throws IllegalArgumentException {
		
		ArrayList<String> cubeStr = new ArrayList<>();

		for(CubeManager cube : new HiveCubeLoader("hive").getAllCubes()) {
			cubeStr.add(new CubeInfoMessage(cube).serialize());
		}
		
		return cubeStr;
	}
}
