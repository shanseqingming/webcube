package cn.edu.zju.webcube.client.view;

import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * 
 * @author wusai
 *
 */
public class ResultListView extends VLayout{
	
	final CaculusTile tile;
	
	final ListGrid queryResult;
	
	private QueryView parent = null;
	
	public ResultListView(QueryView parent){
		
		this.parent = parent;
		
		setWidth100();
		setHeight100();
		
		tile = new CaculusTile();
		
		VLayout right = new VLayout();
		
		right.setWidth(60);
		right.setHeight(100);
		//form.setNumCols(2);
		//form.setColWidths(400, "*");
		
		final IButton submit = new IButton("submit");
		submit.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				//parent.submitQuery();
			}
			
		});
		
		final IButton reset = new IButton("reset");
		
		
		right.addMember(submit);
		right.addMember(reset);
		
		HLayout hlayout = new HLayout();
		hlayout.setWidth100();
		hlayout.setHeight(100);
		hlayout.addMember(tile);
		hlayout.addMember(right);
		hlayout.setBorder("1px solid grey");
		
		queryResult = new ListGrid();
		queryResult.setWidth100(); 
		queryResult.setAutoFitMaxRecords(10);  
		queryResult.setAutoFitData(Autofit.VERTICAL);  
		queryResult.setCanEdit(false);  
		
		addMember(hlayout);
		//addChild(queryResult);
	}

	/*
	private String getQuery(){
		RecordList records = tile.getRecordList();
		for(int i=0; i<records)
	}
	*/
}
