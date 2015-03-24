package cn.edu.zju.webcube.shared.db;

import com.smartgwt.client.widgets.tile.TileRecord;

/**
 * show the cube in the UI
 * @author wusai
 *
 */
public class CubeRecord extends TileRecord{

	public CubeRecord(){
		
	}
	
	public CubeRecord(String name, String tables){
		setCubeName(name);
		setCubeTable(tables);
		setPicture();
	}
	
	public void setCubeName(String name){
		setAttribute("cubename", name);  
	}
	
	public String getCubeName(){
		return getAttribute("cubename");
	}
	
	public void setCubeTable(String tables){
		setAttribute("cubetable", tables);
	}
	
	public String getCubeTable(){
		return getAttribute("cubetable");
	}
	
	public void setPicture(){
		setAttribute("picture", "cube.png");
	}
	
	public String getPicture(){
		return getAttribute("picture");
	}
}
