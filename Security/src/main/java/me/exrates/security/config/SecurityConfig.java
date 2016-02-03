package me.exrates.security.config;

<<<<<<< HEAD
import javax.servlet.http.HttpServletRequest;

import me.exrates.security.service.UserDetailsServiceImpl;

=======
import me.exrates.security.service.UserDetailsServiceImpl;
>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
<<<<<<< HEAD
 
    @Autowired
    PasswordEncoder passwordEncoder;
	
    @Autowired
    UserDetailsServiceImpl userDetailsService;
	
   	@Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }
 
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        		.authorizeRequests()
	                .antMatchers("/admin/**", "/admin").access("hasRole('ADMINISTRATOR')")
	                .antMatchers("/index.jsp").permitAll()
	                .antMatchers("/login","/register","/create").anonymous()
	                .antMatchers("/*.html").permitAll()
	                .anyRequest().authenticated()
                .and()
                .exceptionHandling().accessDeniedPage("/403");
       
       http.formLogin()
=======

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/admin/**", "/admin").access("hasRole('ADMINISTRATOR')")
                .antMatchers("/index.jsp").permitAll()
                .antMatchers("/login","/register","/create").anonymous()
                .antMatchers("/*.html").permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().accessDeniedPage("/403");

        http.formLogin()
>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1
                .loginPage("/login")
                .defaultSuccessUrl("/personalpage")
                .failureUrl("/loginfailed")
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll();
<<<<<<< HEAD
 
=======

>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1
        http.logout()
                .permitAll()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .and()
<<<<<<< HEAD
	    .csrf(); 
    }
 
    
 
=======
                .csrf();
    }
>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1
}