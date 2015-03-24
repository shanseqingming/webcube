package cn.edu.zju.webcube.client.view;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.bean.BeanFactory;
import com.smartgwt.client.widgets.tile.TileGrid;
import com.smartgwt.client.widgets.tile.TileRecord;

public class CaculusTile extends TileGrid{
	
	public interface ButtonFormMetaFactory extends BeanFactory.MetaFactory {  
        BeanFactory<ButtonForm> getButtonFormFactory();  
    }  

	public CaculusTile(){
		GWT.create(ButtonFormMetaFactory.class);  
		
		setTileWidth(100);    
        setTileHeight(30);
        
        setTitle("Create a New Query By Dragging Caculus Here.");
        setHeight(100);    
        setWidth(800);  
        //setBorder("1px solid blue"); 
        setCanAcceptDrop(true);  
        setCanDrag(true);    
        setData(new StringRecord[]{});
        setTileConstructor(ButtonForm.class.getName());   
	}
	
	
	
	class StringRecord extends TileRecord{
		
		public StringRecord(){
			
		}
		
		public StringRecord(String name){
			setCaculusName(name);
		}
		
		public void setCaculusName(String name){
			setAttribute("CaculusName", name);
		}
		
		public String getCaculusName(){
			return getAttribute("CaculusName");
		}
	}
}

