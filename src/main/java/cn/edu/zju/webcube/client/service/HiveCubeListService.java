package cn.edu.zju.webcube.client.service;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("cubelist")
public interface HiveCubeListService extends RemoteService {
	ArrayList<String> getCubeList() throws IllegalArgumentException;
}
