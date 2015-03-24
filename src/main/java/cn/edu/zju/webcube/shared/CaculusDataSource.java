package cn.edu.zju.webcube.shared;

import cn.edu.zju.webcube.shared.db.Column;
import cn.edu.zju.webcube.shared.db.CubeManager;
import cn.edu.zju.webcube.shared.db.MetaDataBuffer;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CaculusDataSource extends DataSource{

	public CaculusDataSource(String cubeID){
		setID("caculus");
		
		DataSourceTextField itemKey = new DataSourceTextField("pkey", null);
		itemKey.setHidden(true);
		
		itemKey.setRequired(true);
		itemKey.setPrimaryKey(true);
		 	     
	    DataSourceTextField nameField = new DataSourceTextField("CaculusName", "Caculus Name");  
	    nameField.setRequired(true);
	    
	    DataSourceTextField typeField = new DataSourceTextField("CaculusType", "Caculus Type");
	    typeField.setHidden(true);
	    
	    DataSourceTextField fkey = new DataSourceTextField("fkey", null);
	    fkey.setHidden(true);
	    fkey.setRootValue(0);
	    fkey.setForeignKey("caculus.pkey");
	    
	    setFields(itemKey, nameField, typeField, fkey);  
	    
	    setClientOnly(true);

	    parseData(cubeID);
	}
	
	public void parseData(String cubeID){
		 		 
		 int keyCount = 0;
		 
		 ListGridRecord root = new  ListGridRecord();
		 root.setAttribute("pkey", keyCount);
		 root.setAttribute("CaculusName", "Caculus");
		 root.setCanDrag(false);
		 this.addData(root);
		 
		 keyCount++;
		 ListGridRecord quantity = new  ListGridRecord();
		 quantity.setAttribute("pkey", keyCount);
		 quantity.setAttribute("CaculusName", "Aggregator");
		 quantity.setAttribute("fkey", 0);
		 quantity.setCanDrag(false);
		 this.addData(quantity);
		 
		 keyCount++;
		 ListGridRecord ordinary = new  ListGridRecord();
		 ordinary.setAttribute("pkey", keyCount);
		 ordinary.setAttribute("CaculusName", "Group By");
		 ordinary.setAttribute("fkey", 0);
		 ordinary.setCanDrag(false);
		 this.addData(ordinary);
		 
		
		 /*int count = 6;
		 for(int i=0; i<count; i++){
			 keyCount++;
			 ListGridRecord nextQuantity = new  ListGridRecord();
			 nextQuantity.setAttribute("pkey", keyCount);
			 nextQuantity.setAttribute("CaculusName", "avg(c_" + i + ")");
			 nextQuantity.setAttribute("fkey", 1);
			 nextQuantity.setAttribute("typeField", "Quantity");
			 nextQuantity.setCanDrag(true);
			 this.addData(nextQuantity);
			 
			 keyCount++;
			 nextQuantity = new  ListGridRecord();
			 nextQuantity.setAttribute("pkey", keyCount);
			 nextQuantity.setAttribute("CaculusName", "sum(c_" + i + ")");
			 nextQuantity.setAttribute("fkey", 1);
			 nextQuantity.setAttribute("typeField", "Quantity");
			 nextQuantity.setCanDrag(true);
			 this.addData(nextQuantity);
			 
			 keyCount++;
			 ListGridRecord nextOrdinary = new  ListGridRecord();
			 nextOrdinary.setAttribute("pkey", keyCount);
			 nextOrdinary.setAttribute("CaculusName", "group by (c_" + i + ")");
			 nextOrdinary.setAttribute("fkey", 2);
			 nextOrdinary.setAttribute("typeField", "Ordinary");
			 nextOrdinary.setCanDrag(true);
			 this.addData(nextOrdinary);
		 }*/

		HashMap<String, CubeManager> cubes =  MetaDataBuffer.getInstance().getCubes();
		CubeManager data = MetaDataBuffer.getInstance().getCubes().get(cubeID.trim());
		HashMap<Column, Integer> ordinaryMap =data.getInvertedMap();
		HashMap<Column, Integer> quantityMap =data.getInvertedGroup();

		for(Column my : ordinaryMap.keySet())
		{
			int contains = 0;
			for(Column quan : quantityMap.keySet()) {
				if (my.getName().equals(quan.getName().trim())) {
					contains = 1;
					break;
				}
			}
			if(contains == 0) {
				keyCount++;
				ListGridRecord nextQuantity = new ListGridRecord();
				nextQuantity.setAttribute("pkey", keyCount);
				nextQuantity.setAttribute("CaculusName", my.getName());
				nextQuantity.setAttribute("fkey", 1);
				nextQuantity.setAttribute("typeField", "Quantity");
				nextQuantity.setCanDrag(true);
				this.addData(nextQuantity);
			}
		}
		for(Column my : quantityMap.keySet())
		{
			keyCount++;
			ListGridRecord nextOrdinary = new  ListGridRecord();
			nextOrdinary.setAttribute("pkey", keyCount);
			nextOrdinary.setAttribute("CaculusName", my.getName());
			nextOrdinary.setAttribute("fkey", 2);
			nextOrdinary.setAttribute("typeField", "Ordinary");
			nextOrdinary.setCanDrag(true);
			this.addData(nextOrdinary);
		}
	}
}