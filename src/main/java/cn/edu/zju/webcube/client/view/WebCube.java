package cn.edu.zju.webcube.client.view;


import java.util.ArrayList;

import cn.edu.zju.webcube.client.service.HiveCubeListService;
import cn.edu.zju.webcube.client.service.HiveCubeListServiceAsync;
import cn.edu.zju.webcube.client.service.HiveLoginService;
import cn.edu.zju.webcube.client.service.HiveLoginServiceAsync;
import cn.edu.zju.webcube.client.service.HiveQueryService;
import cn.edu.zju.webcube.client.service.HiveQueryServiceAsync;
import cn.edu.zju.webcube.client.service.HiveTableListService;
import cn.edu.zju.webcube.client.service.HiveTableListServiceAsync;
import cn.edu.zju.webcube.shared.db.MetaDataBuffer;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.core.Rectangle;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.Page;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.AnimationCallback;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.validator.RegExpValidator;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler; 
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WebCube implements EntryPoint {
	
	/**
	 * 
	 */
	VLayout vLayoutAlignCenter = null;
	
	/**
	 * login form
	 */
	DynamicForm form = null;
	
	/**
	 * submit button
	 */
	Button submit = null;
	
	/**
	 * Create a remote service proxy to talk to the server-side service.
	 */
	private final HiveLoginServiceAsync loginService = GWT
			.create(HiveLoginService.class);
	
	private final HiveTableListServiceAsync tableService = GWT
			.create(HiveTableListService.class);
	
	private final HiveCubeListServiceAsync cubeService = GWT
			.create(HiveCubeListService.class);
	
	public final HiveQueryServiceAsync queryService = GWT
			.create(HiveQueryService.class);
	
	private boolean serviceReturnFlag = false;
	
	/**
	 * cache the metadata of database in the client side
	 */
	//private final MetaDataBuffer databuffer = new MetaDataBuffer();

	@Override
	public void onModuleLoad() {
		// TODO Auto-generated method stub
		
		vLayoutAlignCenter = new VLayout();  
        // Specifying the width creates space within which to center the members.  
		vLayoutAlignCenter.setWidth100();  
		vLayoutAlignCenter.setHeight100();   
		vLayoutAlignCenter.setDefaultLayoutAlign(Alignment.CENTER); // As promised!
		vLayoutAlignCenter.setMembersMargin(10);
		
	
		Img welcome_img = new Img("big-data-analytics.png", 1024, 300);   
	    vLayoutAlignCenter.addMember(welcome_img);
	    
	    //DataSource dataSource = new DataSource();
	    
	    form = new DynamicForm();  
	    form.setWidth(500);
	    form.setHeight(160);
	    form.setNumCols(2);  
        form.setColWidths(150, "*");
	    form.setIsGroup(true);
	    form.setGroupTitle("Please fill in the login information");
	    form.setStyleName("loginform");
	    form.setBorder("1px dotted grey");
	    //form.setLayoutAlign(Alignment.CENTER);
	    
	    /*
        DataSourceTextField hostName = new DataSourceTextField("ip", "Hive Master IP", 100, true);
        DataSourceTextField port = new DataSourceTextField("port", "PORT", 5, true);
        DataSourceTextField userName = new DataSourceTextField("user", "User Name", 50, false);
        
        
        RegExpValidator ipValidator = new RegExpValidator();  
        ipValidator.setErrorMessage("Invalid IP address");  
        ipValidator.setExpression("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");  
        hostName.setValidators(ipValidator);
        
        RegExpValidator portValidator = new RegExpValidator();  
        portValidator.setErrorMessage("Invalid Port Number");  
        portValidator.setExpression("\\d{1,5}");  
        port.setValidators(portValidator);
        
        DataSourcePasswordField passwordField = new DataSourcePasswordField("password", "Password", 20, false);  
        DataSourceTextField dbName = new DataSourceTextField("dbname", "Database Name:", 50, true);
        */
	    
	    TextItem hostName = new TextItem();
	    hostName.setTitle("Hive Master IP");
	    hostName.setWidth("*");
	    hostName.setName("host");
	    
	    RegExpValidator ipValidator = new RegExpValidator();  
        ipValidator.setErrorMessage("Invalid IP address");  
        ipValidator.setExpression("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
        			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");  
        hostName.setValidators(ipValidator);
        
        TextItem port = new TextItem();
        port.setTitle("Port");
        port.setWidth("*");
        port.setName("port");
	    
        RegExpValidator portValidator = new RegExpValidator();  
        portValidator.setErrorMessage("Invalid Port Number");  
        portValidator.setExpression("^([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$");  
        port.setValidators(portValidator);
        
        TextItem userName = new TextItem();
        userName.setTitle("User Name:");
        userName.setWidth("*");
        userName.setName("user");
        
        PasswordItem password = new PasswordItem();
        password.setTitle("Password:");
        password.setWidth("*");
        password.setName("password");
        
        TextItem dbName = new TextItem();
        dbName.setTitle("Database Name:");
        dbName.setWidth("*");
        dbName.setName("dbname");
        
        
        submit = new Button();   
        submit.setTitle("Submit");  
        submit.setShowRollOver(true);
        submit.setShowDisabled(true);
        submit.setShowDown(true);
        submit.setLeft(300);
        submit.setHeight(30);
        
        submit.addClickHandler(new LoginHandler());  
  
        //form.setUseAllDataSourceFields(true);  
        form.setFields(hostName, port, userName, password, dbName);
        
        form.setValue("host", "127.0.0.1");  
        form.setValue("port", "10000");  
        form.setValue("password", "");  
        form.setValue("dbname", "default");  
        form.setValue("user", "");  
        form.setValue("password", "");  
      
        vLayoutAlignCenter.addMember(form);
        vLayoutAlignCenter.addMember(submit);

	    vLayoutAlignCenter.draw(); 
	        
	}
	
	
	class LoginHandler implements ClickHandler{
		
		@Override
		public void onClick(ClickEvent event) {
			// TODO Auto-generated method stub
			boolean result = form.validate(true);
			if(result){
				performLogin();
			}
		}  
		
		private void performLogin(){
			serviceReturnFlag = false;
			submit.setDisabled(true);
			String host = (String) form.getField("host").getValue();
			String port = (String)form.getField("port").getValue();
			String dbname = (String)form.getField("dbname").getValue();
			String user = (String)form.getField("user").getValue();
			String password = (String)form.getField("password").getValue();
			
			MetaDataBuffer.getInstance().cleanCube();

			
			String collectionString = "jdbc:hive2://" + host + ":" + port + "/" + dbname;
			System.out.print("first : " + collectionString +"\n");   //test
			loginService.login(collectionString, user, password, 
					new AsyncCallback<Boolean>() {
				@Override
				public void onFailure(Throwable caught) {
					// Show the RPC error message to the user
					
					SC.confirm("Fail to connect to server!!!"+caught, new BooleanCallback() {

						@Override
						public void execute(Boolean value) {
							// TODO Auto-generated method stub
							//currently, nothing to do
						}
						
					});
				}

				@Override
				public void onSuccess(Boolean result) {
					/*
					SC.confirm("Successfully connect to server!!!", new BooleanCallback() {

						@Override
						public void execute(Boolean value) {
							// TODO Auto-generated method stub
							//currently, nothing to do
						}
						
					});
					*/
					if(result == false){
						SC.confirm("Fail to connect to server!!!", new BooleanCallback() {

							@Override
							public void execute(Boolean value) {
								// TODO Auto-generated method stub
								//currently, nothing to do
							}
							
						});
					}
					else{	
						serviceReturnFlag = true;
						performGetTableList();
						//SC.confirm("flag:" + serviceReturnFlag, null);
					}}
			});	
			
		}
		
		private void performGetTableList(){
			serviceReturnFlag = false;
			tableService.getTableList(new AsyncCallback<ArrayList<String>>(){

				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
					SC.confirm("Fail to get the table list", new BooleanCallback() {

						@Override
						public void execute(Boolean value) {
							// TODO Auto-generated method stub
							//currently, nothing to do
						}
						
					});
				}

				@Override
				public void onSuccess(ArrayList<String> result) {
					// TODO Auto-generated method stub
					serviceReturnFlag = true;
					if(result!=null){
						//SC.confirm("process the table list result:"+result.size(), null);
						for(int i=0; i<result.size(); i++){
							MetaDataBuffer.getInstance().acceptTableInfoMessage(result.get(i));
						}
					}
					performGetCubeList();	
				}
				
			});
		}
		
		private void performGetCubeList(){
			serviceReturnFlag = false;
			cubeService.getCubeList(new AsyncCallback<ArrayList<String>>(){

				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
					SC.confirm("Fail to get the table list", new BooleanCallback() {

						@Override
						public void execute(Boolean value) {
							// TODO Auto-generated method stub
							//currently, nothing to do
						}
						
					});
				}

				@Override
				public void onSuccess(ArrayList<String> result) {
					// TODO Auto-generated method stub
					serviceReturnFlag = true;
					if(result!=null){
						for(int i=0; i<result.size(); i++){
							MetaDataBuffer.getInstance().acceptCubeInfoMessage(result.get(i));
							System.out.print(result.get(i));
						}
					}
					drawNewWindow();
				}
				
			});
		}
		
		private void drawNewWindow(){
			submit.setDisabled(false);
			final CubeView view = new CubeView();
			view.setQueryService(queryService);
			
			Rectangle rect = submit.getPageRect();
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
                            Rectangle targetRect = submit.getPageRect();
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
	}
	
	class FullScreenApplication extends Window {

        public FullScreenApplication(CubeView view) {
            setTitle("Cube View");
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
