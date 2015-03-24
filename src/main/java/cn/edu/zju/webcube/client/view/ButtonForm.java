package cn.edu.zju.webcube.client.view;

import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;

public class ButtonForm extends DynamicForm{
	
	public ButtonForm(){
		setOverflow(Overflow.HIDDEN);  
        setHeight(10);  
        setWidth(30);   
        setCanAcceptDrop(true);
        setCanDrag(true); 
        setBorder("1px solid blue");
        
        StaticTextItem caculusName = new StaticTextItem("CaculusName"); 
        caculusName.setShowTitle(false);
        caculusName.setHeight("*");
        caculusName.setWidth("*");

        setFields(caculusName);
	}
}
