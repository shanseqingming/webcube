package cn.edu.zju.webcube.client.view;

import cn.edu.zju.webcube.client.service.HiveQueryServiceAsync;
import cn.edu.zju.webcube.shared.CubeData;
import cn.edu.zju.webcube.shared.TableListDataSource;
import cn.edu.zju.webcube.shared.db.CubeRecord;
import cn.edu.zju.webcube.shared.db.MetaDataBuffer;
import cn.edu.zju.webcube.shared.db.TableRecord;

import com.smartgwt.client.types.DragDataAction;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.SplitPane;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.smartgwt.client.widgets.viewer.DetailViewerField;

/**
 * UI for building and viewing cubes
 * @author wusai
 *
 */
public class CubeView {
	
	
	private TableListDataSource tableListDataSource = null;
	
	private CubeRecord[] records = null;
	
	private JoinConditionView joinView = null;
	
	private TableTile tableGrid = null;
	
	public  HiveQueryServiceAsync queryService;
	
	public CubeView(){
		tableListDataSource = TableListDataSource.getInstance();
		tableListDataSource.parseData();
		records = CubeData.getRecords();
	}
	
	public void setQueryService(HiveQueryServiceAsync service){
		queryService = service;
	}
	
	public Canvas createSplitPane() {    
		final SplitPane splitPane = new SplitPane();    //the biggest framework
        splitPane.setAutoDraw(Boolean.FALSE);    
        splitPane.setNavigationTitle("Database Tables");  
        splitPane.setListTitle("Data Cubes");
        splitPane.setDetailTitle("Create New Cubes by Dragging Tables Below");
        splitPane.setShowLeftButton(false);    
        splitPane.setShowRightButton(false);    
        splitPane.setBorder("1px solid blue");    
    
        
        
        //final DetailViewer detailPane = new DetailViewer();    
        //detailPane.setDataSource(ItemSupplyXmlDS.getInstance());    
        //detailPane.setAutoDraw(Boolean.FALSE);    
    
        //final ListGrid listPane = new ListGrid();    
        //listPane.setDataSource(ItemSupplyXmlDS.getInstance());    
        //listPane.setAutoDraw(Boolean.FALSE);    
        TileGridWithButton tileGrid = new TileGridWithButton(this);    //show the cubes that already exists
        tileGrid.setTileWidth(230);  
        tileGrid.setTileHeight(230);  
        tileGrid.setHeight(250);  
        tileGrid.setWidth100();  
        tileGrid.setCanReorderTiles(true);  
        tileGrid.setShowAllRecords(true);  
        tileGrid.setTitle("Data Cubes");
        tileGrid.setData(records);  
        
        DetailViewerField pictureField = new DetailViewerField("picture");  //picture of the cube
        pictureField.setType("image");  
        pictureField.setImageURLPrefix("./");  
        pictureField.setImageWidth(150);  
        pictureField.setImageHeight(150);  
  
        DetailViewerField cubeName = new DetailViewerField("cubename");  
        DetailViewerField cubeTables = new DetailViewerField("cubetable");  
        
  
        tileGrid.setFields(pictureField, cubeName, cubeTables);  //These three fractions show the cubes that has been built
  
        
            
        final TreeGrid navigationPane = new TreeGrid();    //The left navigation field
        navigationPane.setAutoDraw(Boolean.TRUE);    
        navigationPane.setDataSource(tableListDataSource);  //load data to the navigation field
        navigationPane.setAutoFetchData(Boolean.TRUE);    
        navigationPane.setLoadDataOnDemand(true); 
        navigationPane.setCanEdit(false); 
        navigationPane.setCanReorderRecords(true); 
        navigationPane.setCanAcceptDroppedRecords(false); 
        //navigationPane.setCanDrag(true);
        //navigationPane.setCanDrop(true);
        navigationPane.setCanDragRecordsOut(true);
        navigationPane.setDragDataAction(DragDataAction.COPY);
        navigationPane.setTreeFieldTitle("Table Name");
        //navigationPane.setShowHeader(Browser.getIsDesktop()); 
        TreeGridField nameField = new TreeGridField("Name", "Table Name", 200); 
        nameField.setTreeField(true);
        navigationPane.setFields(nameField);
        TreeGridField valField = new TreeGridField("Type", "Type", 100); 
        navigationPane.setFields(nameField,  valField);
        
        
        VLayout detailLayout = new VLayout();
        detailLayout.setWidth100();
        detailLayout.setHeight100();
        
        joinView = new JoinConditionView(this);
        
        tableGrid = new TableTile(this);     //The field that tables are dragged to
        tableGrid.setTileWidth(240);  
        tableGrid.setTileHeight(400);  
        tableGrid.setHeight100();  
        tableGrid.setWidth100();  
        tableGrid.setCanAcceptDrop(true);  
        tableGrid.setCanDrag(true);  
        tableGrid.setShowAllRecords(true);
        tableGrid.setData(new TableRecord[]{});
        tableGrid.setTitle("Create New Cubes by Dragging Tables Here");
        
        DetailViewerField pictureField2 = new DetailViewerField("tablepicture");  //Picture of the tables that are dragged
        pictureField2.setType("image");  
        pictureField2.setImageURLPrefix("./");  
        pictureField2.setImageWidth(220);  
        pictureField2.setImageHeight(160);  
  
        DetailViewerField tableName = new DetailViewerField("Name");
        tableGrid.setFields(pictureField2, tableName);  

        detailLayout.addMember(joinView);
        detailLayout.addMember(tableGrid);
            
        //splitPane.setDetailPane(detailPane);
        //splitPane.setDetailPane(tableGrid);    
        splitPane.setDetailPane(detailLayout);
        splitPane.setNavigationPane(navigationPane);    
        splitPane.setListPane(tileGrid);    
        
        
        splitPane.setWidth100();
        splitPane.setHeight100();
        
        return splitPane;    
	}
	
	public void reset(){
		joinView.reset();
		tableGrid.reset();
		MetaDataBuffer.getInstance().clearNewCube();
	}
	
	/*
	public void addNewJoinTable(Table table){
		//PrimaryKeyDataSource.getInstance().addRecord(table);
		ForeignKeyDataSource.getInstance().addRecord(table);
	}
	*/
	
	public void enableJoinSetting(){
		joinView.enableEdit();
		//joinView.redraw();
	}
	
	/**
	 * submit a job to build the cube
	 */
	public void submitCubeJob(){
		
	}
}
