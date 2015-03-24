package cn.edu.zju.webcube.client.service;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("query")
public interface HiveQueryService extends RemoteService {
	
	boolean createCube(String sql, String cubeMsg) throws IllegalArgumentException;
	
	String submitQuery(String cubeID, ArrayList<String> ordinaryColumns, ArrayList<String> quantityColumns) throws IllegalArgumentException;
}
