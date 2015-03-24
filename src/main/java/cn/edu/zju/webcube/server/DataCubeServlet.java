package cn.edu.zju.webcube.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.edu.zju.webcube.server.hive.HiveCubeLoader;
import cn.edu.zju.webcube.shared.db.Column;
import cn.edu.zju.webcube.shared.db.CubeManager;
import cn.edu.zju.webcube.shared.db.CubeResult;
import cn.edu.zju.webcube.shared.json.view.CubeManagerView;
import cn.edu.zju.webcube.shared.json.view.CubeResultView;
import cn.edu.zju.webcube.shared.json.view.Response;
import cn.edu.zju.webcube.shared.json.view.ReturnCode;
import org.apache.log4j.Logger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;



public class DataCubeServlet extends HttpServlet {

	private static final long serialVersionUID = 8264007175728684353L;
	HiveCubeLoader cubeLoader = null;
	private static final Logger logger = Logger.getLogger(DataCubeServlet.class);

	@Override
	public void init(ServletConfig config) throws ServletException{
		super.init(config);
		cubeLoader =  new HiveCubeLoader("hive");
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
		resp.setHeader("Pragma", "no-cache");
		resp.setHeader("Cache-Control", "no-cache");
		resp.setHeader("Access-Control-Allow-Origin", "null");
		resp.setDateHeader("Expires", 0);

		String method = req.getParameter("method");
		Response response = Response.getMethodNotFoundResp(null);
		
		if(method == null){
			this.write2out(out, response);
			return;
		}
		
		if(method.equalsIgnoreCase("queryallcubes")) {
			//http://127.0.0.1:8888/webcube/datacube?method=queryallcubes
			response = getAllCubes();
			
		} else if(method.equalsIgnoreCase("getcube")) {
			//http://127.0.0.1:8888/webcube/datacube?method=getcube&cubeID=pzftest_location
			String cubeid = req.getParameter("cubeID");
			if(cubeid == null) {
				response = Response.getParaErrResp(null);
			} else {
				response = getCube(cubeid);	
			}
			
		} else if(method.equalsIgnoreCase("querycardinalityofdimension")) {
			//http://127.0.0.1:8888/webcube/datacube?method=querycardinalityofdimension&column=time.year.string.group.null
			String column = req.getParameter("column");
			if(column == null) {
				response = Response.getParaErrResp(null);
			} else {
				response = queryCardinalityOfDimension(column);
			}
			
		} else if(method.equalsIgnoreCase("queryValuesOfDimension")) {
			//http://127.0.0.1:8888/webcube/datacube?method=queryValuesOfDimension&column=time.year.string.group.null&offset=0&count=9
			String column = req.getParameter("column");
			String offset = req.getParameter("offset");
			String count = req.getParameter("count");
			
			if(column == null || offset == null || count == null) {
				response = Response.getParaErrResp(null);
			} else {
				response = queryValuesOfDimension(column, offset, count);				
			}
			
		} else if(method.equalsIgnoreCase("queryCube")) {
			//http://127.0.0.1:8888/webcube/datacube?method=queryCube&cubeID=pzftest3&columns=[time.year.string.group.null,time.month.string.group.null,sales_fact.profit.float.quantity.sum]
			//http://127.0.0.1:8888/webcube/datacube?method=queryCube&cubeID=pzftest3&columns=[time.year.string.group.null,time.month.string.group.null,sales_fact.profit.float.quantity.sum]&operation=plus
			String cubeid = req.getParameter("cubeID");
			String columns = req.getParameter("columns");
			String operation = req.getParameter("operation");
			
			if(columns == null || cubeid == null ) {
				response = Response.getParaErrResp(null);
			} else {
				response = queryCube(cubeid, columns, operation);
			}
			
		}
		
		this.write2out(out, response);
	}
	
	//读取所有datacube
	private Response<CubeManagerView> getAllCubes() {
		//http://127.0.0.1:8888/webcube/datacube?method=queryallcubes
		ArrayList<CubeManager> cubes = cubeLoader.getAllCubes();
		ArrayList<CubeManagerView> cubeviews = new ArrayList<CubeManagerView>();
		for(CubeManager cube : cubes) {
			cubeviews.add(new CubeManagerView(cube));
		}
		 
		return new Response<CubeManagerView>(cubeviews, ReturnCode.SUCESS.getCode());
	}
	
	private Response<CubeManagerView> getCube(String cubeid) {
		CubeManager cube = cubeLoader.getCube(cubeid);
		if(cube == null) {
			return Response.getEmptyResultResp(null);
		} else {
			CubeManagerView cvv = new CubeManagerView(cube);
			return new Response<CubeManagerView>(cvv, ReturnCode.SUCESS.getCode());
		}
	}
	
	//获得某一个属性的全部取值的个数
	private Response<Integer> queryCardinalityOfDimension(String column) {
		int num = cubeLoader.queryCardinalityOfDimension(new Column(column));
		String tmp[] = column.split("\\.");
		return new Response<Integer>(num, ReturnCode.SUCESS.getCode(),tmp[1]);
	}
	
	private Response<String> queryValuesOfDimension(String column, String offset, String count) {
	//private Response<ArrayList<String>> queryValuesOfDimension(String column, String offset, String count) {
		Column c = new Column(column);
		ArrayList<String> values = cubeLoader.queryValuesOfDimension(c, Integer.valueOf(offset), Integer.valueOf(count));
		
		if(values == null) {
			return Response.getEmptyResultResp(null);
		}
		return new Response<String>(values, ReturnCode.SUCESS.getCode());
	}
	
	//private Response<CubeResultView> queryCube(String cubeID, String column, String operation) {
	private Response<ArrayList<ArrayList<String>>> queryCube(String cubeID, String column, String operation) {
		//如果没有group列，则返回错误
		//1个或者3个极其以上，普通查询
		//两个group列，如果op有值则按op来,如果没有的话，则进行普通查询
		// ["time.year.string.group.null","time.month.string.group.null","sales_fact.profit.float.quantity.sum"]
		Gson gson = new Gson();
		ArrayList<Column> groups = new ArrayList<Column>();
		ArrayList<Column> quantities = new ArrayList<Column>();
		ArrayList<CubeResult> results = null;
		
		
		
		ArrayList<String> columsStr = gson.fromJson(column,
				new TypeToken<ArrayList<String>>() {
				}.getType());
		
		for(String cstr : columsStr) {
			System.out.print(cstr+"\n");
			Column c = new Column(cstr);
			if(c.isOrdinary()) {
				groups.add(c);
			} else {
				quantities.add(c);
			}
		}
		
		CubeResultView crv = null;
		if(groups.size() == 2 && operation != null && (!operation.equalsIgnoreCase("null"))) { 
			
			if(operation.equalsIgnoreCase("plus")) {
				results = cubeLoader.queryCubePlus(cubeID, groups, quantities);
				
			} else if (operation.equalsIgnoreCase("star")) {
				results = cubeLoader.queryCubeStar(cubeID, groups, quantities);
				
			} else if (operation.equalsIgnoreCase("slash")) {
				results = cubeLoader.queryCubeSlash(cubeID, groups, quantities);
				
			} else {
				return Response.getParaErrResp(crv.getResults());
			}
			
		} else {	
			results = cubeLoader.queryCube(cubeID, groups, quantities);
		}
		
		if(results == null) {
			return Response.getEmptyResultResp(crv.getResults());
		}
		
		crv = new CubeResultView(results, groups, quantities);

		//return new Response<CubeResultView>(crv, ReturnCode.SUCESS.getCode());
		return new Response<ArrayList<ArrayList<String>>>(crv.getResults(), ReturnCode.SUCESS.getCode());
	}
	
	private  String toJson(Response response) {
		Gson gson = new Gson();
		return gson.toJson(response);
	}
	
	private void write2out(PrintWriter out, Response response) {
		out.write(toJson(response));
	}
}




