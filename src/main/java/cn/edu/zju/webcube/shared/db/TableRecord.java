package cn.edu.zju.webcube.shared.db;

import com.smartgwt.client.widgets.tile.TileRecord;

/**
 * the tile record in the detailed panel
 * @author wusai
 *
 */
public class TableRecord extends TileRecord{

	public TableRecord(){
		
	}
	
	public TableRecord(String name){
		setName(name);
		setTablePicture();
	}
	
	public void setName(String name){
		setAttribute("Name", name);  
	}
	
	public String getName(){
		return getAttribute("Name");
	}
	
	public void setTablePicture(){
		setAttribute("tablepicture", "table.jpg");
	}
	
	public String getTablePicture(){
		return getAttribute("tablepicture");
	}
}
