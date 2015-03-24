package cn.edu.zju.webcube.shared.json.view;

import java.util.ArrayList;


public class Response<T> {
	private ArrayList<T> result;
	private int code;
	
	private String name;
	
	public Response(ArrayList<T> list, int code) {
		this.result = list;
		this.code = code;
	}
	
	public Response(T t, int code) {
		result = new ArrayList<T>();
		result.add(t);
		this.code = code;
	}

	public Response(T t,int code,String name) {
		result = new ArrayList<T>();
		result.add(t);
		this.code = code;
		this.name = name;
	}
	
	public ArrayList<T> getList() {
		return result;
	}

	public void setList(ArrayList<T> list) {
		this.result = list;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	
	
	public static <T> Response<T> getMethodNotFoundResp(T t) {
		return new Response<T>(t, ReturnCode.METHOD_NOT_FOUND.getCode());
	}
	
	
	public static <T> Response<T> getParaErrResp(T t) {
		return new Response<T>(t, ReturnCode.PARA_ERROR.getCode());
	}
	
	public static <T> Response<T> getEmptyResultResp(T t) {
		return new Response<T>(t, ReturnCode.EMPTY_RESULT.getCode());
	}
}
