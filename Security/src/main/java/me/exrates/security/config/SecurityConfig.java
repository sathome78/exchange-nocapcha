package me.exrates.security.config;

import me.exrates.model.enums.UserRole;
import me.exrates.security.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
@PropertySource("classpath:/${spring.profile.active}/merchants/perfectmoney.properties")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    private @Value("${ipWhiteList}") String ipWhiteList;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

    //// TODO: 3/4/16 Access to perfectmoney[status/success/failure] need to be protected by list of while ip addrs
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/admin/**", "/admin").hasAnyAuthority(UserRole.ADMINISTRATOR.name(),
                UserRole.ACCOUNTANT.name(), UserRole.ADMIN_USER.name())
                .antMatchers("/index.jsp","/client/**","/dashboard/**","/registrationConfirm/**").permitAll()
                .antMatchers(HttpMethod.POST,"/merchants/perfectmoney/payment/status",
                        "/merchants/perfectmoney/payment/success",
                        "/merchants/perfectmoney/payment/failure").permitAll()
                .antMatchers("/login","/register","/create","/forgotPassword","/resetPassword").anonymous()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().accessDeniedPage("/403");
        http.formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/mywallets")
                .failureUrl("/loginfailed")
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll();

        http.logout()
                .permitAll()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .and()
	    .csrf()
            .ignoringAntMatchers("/merchants/perfectmoney/payment/status",
                    "/merchants/perfectmoney/payment/failure",
                    "/merchants/perfectmoney/payment/success");
    }

    private String buildHasIpExpression() {
        return Stream.of(ipWhiteList.split(";"))
                .map(ip -> String.format("hasIpAddress('%s')", ip))
                .collect(Collectors.joining(" or "));
    }
 }