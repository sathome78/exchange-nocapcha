package me.exrates.config;

import java.util.Locale;

import me.exrates.controllers.validators.FormValidation;
import me.exrates.daos.CommissionDao;
import me.exrates.daos.CurrencyDao;
import me.exrates.daos.OrderDao;
import me.exrates.daos.UserDao;
import me.exrates.daos.WalletDao;
import me.exrates.impl.CommissionDaoImpl;
import me.exrates.impl.CurrencyDaoImpl;
import me.exrates.impl.OrderDaoImpl;
import me.exrates.impl.UserDaoImpl;
import me.exrates.impl.WalletDaoImpl;
import me.exrates.secure.service.UserDetailsServiceImpl;
import me.exrates.secure.service.UserSecureService;
import me.exrates.secure.service.UserSecureServiceImpl;
import me.exrates.services.OrderService;
import me.exrates.services.OrderServiceImpl;
import me.exrates.services.UserService;
import me.exrates.services.UserServiceImpl;
import me.exrates.services.WalletService;
import me.exrates.services.WalletServiceImpl;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
 
@Configuration
@EnableWebMvc
@ComponentScan({ "me.exrates" })
@Import({ me.exrates.secure.config.SecurityConfig.class })
public class WebAppConfig extends WebMvcConfigurerAdapter {
 
    @Bean(name = "dataSource")
	public DriverManagerDataSource dataSource() {
	    DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
	    driverManagerDataSource.setDriverClassName("com.mysql.jdbc.Driver");
	    driverManagerDataSource.setUrl("jdbc:mysql://localhost/birzha");
	    driverManagerDataSource.setUsername("root");
	    driverManagerDataSource.setPassword("root");
	    return driverManagerDataSource;
	}
	
	@Bean
	public InternalResourceViewResolver viewResolver() {
	    InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
	    viewResolver.setViewClass(JstlView.class);
	    viewResolver.setPrefix("/WEB-INF/jsp/");
	    viewResolver.setSuffix(".jsp");
	    return viewResolver;
	}
	
	@Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
    @Bean
    public LocaleResolver localeResolver(){
	CookieLocaleResolver resolver = new CookieLocaleResolver();
	resolver.setDefaultLocale(new Locale("ru"));
	resolver.setCookieName("myAppLocaleCookie");
	resolver.setCookieMaxAge(3600);
	return resolver;
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
	LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
	interceptor.setParamName("locale");
	registry.addInterceptor(interceptor);
    }
    
    @Bean
	public PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}
	
  //Services
    
    @Bean
	public UserDetailsService getUserDetailsService(){
	    return new UserDetailsServiceImpl();
	}
	
	@Bean
    public UserService getUserService(){
        return new UserServiceImpl();
    }
	 
	 @Bean
	 public UserSecureService getUserSecureService(){
	        return new UserSecureServiceImpl();
	    }
	 
	 @Bean
	    public WalletService getWalletService(){
	        return new WalletServiceImpl();
	    }
	 
	 @Bean
	    public OrderService getOrderService(){
	        return new OrderServiceImpl();
	    }
	
  //DAO
	 @Bean
	    public UserDao getUserDao(){
	        return new UserDaoImpl();
	    }
	 
	 @Bean
	    public WalletDao getWalletDao(){
	        return new WalletDaoImpl();
	    }
	 @Bean
	    public OrderDao getOrderDao(){
	        return new OrderDaoImpl();
	    }
	 @Bean
	    public CurrencyDao getCurrencyDao(){
	        return new CurrencyDaoImpl();
	    }
	 @Bean
	    public CommissionDao getCommissionDao(){
	        return new CommissionDaoImpl();
	    }
 
	 
  //Validation
	 @Bean
	    public FormValidation getFormValidation(){
	        return new FormValidation();
	    }
 
}