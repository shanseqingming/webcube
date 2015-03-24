package cn.edu.zju.webcube.server;

import cn.edu.zju.webcube.client.service.HiveLoginService;
import cn.edu.zju.webcube.shared.db.Column;
import cn.edu.zju.webcube.shared.db.HiveDataType;
import cn.edu.zju.webcube.shared.db.Table;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class HiveLoginServiceImpl extends RemoteServiceServlet implements HiveLoginService{

	@Override
	public boolean login(String connectionString, String user, String password)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		// try to connect to hive db 
		
		
		return true;
	}
}
