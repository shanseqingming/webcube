package cn.edu.zju.webcube.client.service;

import java.util.ArrayList;


import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("tablelist")
public interface HiveTableListService extends RemoteService{
	ArrayList<String> getTableList() throws IllegalArgumentException;
}
