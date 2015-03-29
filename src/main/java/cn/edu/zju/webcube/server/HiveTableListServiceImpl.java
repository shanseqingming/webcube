package cn.edu.zju.webcube.server;

import java.util.ArrayList;

import cn.edu.zju.webcube.client.service.HiveTableListService;
import cn.edu.zju.webcube.server.hive.HiveTableLoader;
import cn.edu.zju.webcube.shared.db.Table;
import cn.edu.zju.webcube.shared.msg.TableInfoMessage;

import com.google.gson.Gson;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class HiveTableListServiceImpl extends RemoteServiceServlet implements HiveTableListService{
	@Override
	public ArrayList<String> getTableList()
			throws IllegalArgumentException {
		ArrayList<String> result = new ArrayList<String>();
		for(Table t : new HiveTableLoader("hive").queryAllTable()) {
			result.add(new TableInfoMessage(t).serialize());
		}
		return result;
	}
}
