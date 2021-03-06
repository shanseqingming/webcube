package cn.edu.zju.webcube.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("login")
public interface HiveLoginService extends RemoteService {
	boolean login(String connectionString, String user, String password) throws IllegalArgumentException;
}
