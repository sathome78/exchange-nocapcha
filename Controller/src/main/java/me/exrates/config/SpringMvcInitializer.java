package me.exrates.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;


public class SpringMvcInitializer 
       extends AbstractAnnotationConfigDispatcherServletInitializer {

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
		String activeProfile = System.getenv("EXRATES_PROFILE");
		if (activeProfile == null) {
			activeProfile = "prod";
		}
		System.out.println(activeProfile);
		servletContext.setInitParameter("spring.profile.active",activeProfile);
	}
}