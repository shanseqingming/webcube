package cn.edu.zju.webcube.server.listener;

/**
 * Created by king on 2015/2/3.
 */
import cn.edu.zju.webcube.server.utils.Props;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.net.URL;

public class ConfigListener implements ServletContextListener{
    private static final Logger logger = Logger.getLogger(ConfigListener.class);

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
        if (logger.isDebugEnabled())
        {
            logger.debug("Destroy LogListener....");
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent contextEvent) {
        ServletContext context = contextEvent.getServletContext();

	    String prefix;
	    URL resource;
	    try {
		    resource = this.getClass().getClassLoader().getResource("/");
	        if(resource == null) {
		        throw new NullPointerException("Cannot get resources directory from context");
	        }
		    prefix = resource.getPath();

	    } catch (NullPointerException e) {
		    logger.error(e.getMessage());
		    return;
	    }
	    String propsLocation = prefix + context.getInitParameter("configLocation");
	    String log4jLocation = prefix + context.getInitParameter("log4jConfigLocation");
	    Props.CUBE_META_FILE_LOCATION = prefix + context.getInitParameter("cubeMata");

	    try {
		    Props.initInstance(propsLocation);
		    PropertyConfigurator.configure(Props.getProperties(log4jLocation));
	    } catch (IOException e) {
		    logger.error(e.getMessage());
	    }

    }
}
