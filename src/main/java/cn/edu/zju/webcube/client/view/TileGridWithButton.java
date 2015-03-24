package cn.edu.zju.webcube.client.view;


import com.smartgwt.client.core.Rectangle;
import com.smartgwt.client.util.Page;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.AnimationCallback;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.tile.TileGrid;
import com.smartgwt.client.widgets.tile.TileRecord;

public class TileGridWithButton extends TileGrid{
	
	private CubeView parent = null;
	
	public TileGridWithButton(CubeView parent){
		this.parent = parent;
	}

	 @Override
     public Canvas getTile(int idx) {
         Canvas canvas = super.getTile(idx);
         
         TileRecord record = getTileRecord(canvas);
         final String id = record.getAttribute("cubename");

         final IButton button1 = new IButton("Details");
         button1.setHeight(20);
		 button1.setWidth(80);
		 button1.setTop(190);
		 button1.setLeft(20);
		 button1.addClickHandler(new ClickHandler(){
			 @Override
	            public void onClick(ClickEvent clickEvent) {
	                SC.confirm(id, null);
	            }
		 });
		 
		 final IButton button2 = new IButton("Query");
         button2.setHeight(20);
		 button2.setWidth(80);
		 button2.setTop(190);
		 button2.setLeft(120);
		 
		 button2.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				final QueryView view = new QueryView(parent.queryService, id);
				
				 Rectangle rect = button2.getPageRect();
		         final Canvas animateOutline = new Canvas();
		         animateOutline.setBorder("2px solid black");
		         animateOutline.setTop(rect.getTop());
		         animateOutline.setLeft(rect.getLeft());
		         animateOutline.setWidth(rect.getWidth());
		         animateOutline.setHeight(rect.getHeight());

		         animateOutline.show();
		         animateOutline.animateRect(0, 0, Page.getWidth(), Page.getHeight(), new AnimationCallback() {
		             @Override
					public void execute(boolean earlyFinish) {
		            	 
		                 animateOutline.hide();
		                 final FullScreenApplication appWindow = new FullScreenApplication(view);
		                 appWindow.addCloseClickHandler(new CloseClickHandler() {
		                     @Override
							public void onCloseClick(CloseClickEvent event) {
		                         animateOutline.setRect(0, 0, Page.getWidth(), Page.getHeight());
		                         animateOutline.show();
		                         appWindow.destroy();
		                         Rectangle targetRect = button2.getPageRect();
		                         animateOutline.animateRect(targetRect.getLeft(), targetRect.getTop(), targetRect.getWidth(),
		                                 targetRect.getHeight(), new AnimationCallback() {
		                                     @Override
											public void execute(boolean earlyFinish) {
		                                         animateOutline.hide();
		                                     }
		                                 }, 500);

		                     }
		                 });
		                 appWindow.show();
		             }
		         }, 500
		         );
			}
			 
		 });

		 canvas.addChild(button1);
		 canvas.addChild(button2);
         return canvas;
     }
	 
	 class FullScreenApplication extends Window {

	        public FullScreenApplication(QueryView view) {
	            setTitle("Query View");
	            setWidth100();
	            setHeight100();
	            setShowMinimizeButton(false);
	            setShowCloseButton(true);
	            setCanDragReposition(false);
	            setCanDragResize(false);
	            setShowShadow(false);
	            addItem(view.createSplitPane());
	        }
	    }
}
