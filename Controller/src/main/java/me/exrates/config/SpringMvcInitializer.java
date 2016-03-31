package me.exrates.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;


public class SpringMvcInitializer 
       extends AbstractAnnotationConfigDispatcherServletInitializer {

	private static final Logger logger = LogManager.getLogger(SpringMvcInitializer.class);

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class[] { WebAppConfig.class };
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return null;
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		super.onStartup(servletContext);
		String activeProfile = System.getProperty("profile_active");
		if (activeProfile == null) {
			activeProfile = "dev";
		}
		logger.info("Active profile :" + activeProfile);
		servletContext.setInitParameter("spring.profile.active" , activeProfile);
	}
}