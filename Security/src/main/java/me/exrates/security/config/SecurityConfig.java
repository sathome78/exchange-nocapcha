package me.exrates.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.model.enums.AdminAuthority;
import me.exrates.model.enums.UserRole;
import me.exrates.security.filter.*;
import me.exrates.security.service.UserDetailsServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger LOGGER = LogManager.getLogger(SecurityConfig.class);
    private static final int MAXIMUM_SESSIONS = 2;

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public SecurityConfig(PasswordEncoder passwordEncoder, UserDetailsServiceImpl userDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public LoginSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler("/dashboard");
    }

    @Bean
    public LoginFailureHandler loginFailureHandler() {
        return new LoginFailureHandler("/login?error");
    }

    @Bean
     public CapchaAuthorizationFilter customUsernamePasswordAuthenticationFilter()
            throws Exception {
        CapchaAuthorizationFilter customUsernamePasswordAuthenticationFilter = new CapchaAuthorizationFilter();
        customUsernamePasswordAuthenticationFilter
                .setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/login", "POST"));
        customUsernamePasswordAuthenticationFilter
                .setAuthenticationManager(authenticationManagerBean());
        customUsernamePasswordAuthenticationFilter
                .setUsernameParameter("username");
        customUsernamePasswordAuthenticationFilter
                .setPasswordParameter("password");
        customUsernamePasswordAuthenticationFilter
                .setAuthenticationSuccessHandler(loginSuccessHandler());
        customUsernamePasswordAuthenticationFilter
                .setAuthenticationFailureHandler(loginFailureHandler());


        return customUsernamePasswordAuthenticationFilter;
    }
    @Bean
    public QRAuthorizationFilter customQRAuthorizationFilter() {
        return new QRAuthorizationFilter();
    }

    @Bean(name = "ExratesSessionRegistry")
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    /*
    * Defines separate access denied error handling logic for XHR (AJAX requests) and usual requests
    * */
    @Bean
    public AjaxAwareAccessDeniedHandler accessDeniedHandler() {
        return new AjaxAwareAccessDeniedHandler("/403");
    }



    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(customUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(customQRAuthorizationFilter(), CapchaAuthorizationFilter.class);
        http
                .authorizeRequests()
                .antMatchers("/withdrawal/request/accept", "/withdrawal/request/decline").hasAuthority(AdminAuthority.PROCESS_WITHDRAW.name())
                .antMatchers("/admin/comments", "/admin/addComment", "/admin/deleteUserComment").hasAuthority(AdminAuthority.COMMENT_USER.name())
                .antMatchers("/unsafe/**").hasAnyAuthority(UserRole.ADMINISTRATOR.name())
                .antMatchers("/admin/administrators").hasAuthority(UserRole.ADMINISTRATOR.name())
                .antMatchers("/companywallet").hasAnyAuthority(UserRole.ADMINISTRATOR.name(), UserRole.ACCOUNTANT.name())
                .antMatchers("/merchants/bitcoin/payment/accept", "/merchants/invoice/payment/accept").hasAuthority(AdminAuthority.PROCESS_INVOICE.name())
                .antMatchers("/admin/orderdelete").hasAuthority(AdminAuthority.DELETE_ORDER.name())
                .antMatchers("/admin/expireSession").hasAuthority(AdminAuthority.MANAGE_SESSIONS.name())
                .antMatchers("/admin/editCurrencyLimits/submit",
                        "/admin/editCmnRefRoot", "/admin/editLevel").hasAuthority(AdminAuthority.SET_CURRENCY_LIMIT.name())
                .antMatchers("/admin/editAuthorities/submit").hasAuthority(AdminAuthority.MANAGE_ACCESS.name())
                .antMatchers("/admin/changeActiveBalance/submit").hasAuthority(AdminAuthority.MANUAL_BALANCE_CHANGE.name())
                .antMatchers("/admin/**", "/admin").hasAnyAuthority(UserRole.ADMINISTRATOR.name(),
                UserRole.ACCOUNTANT.name(), UserRole.ADMIN_USER.name())
                .antMatchers(HttpMethod.POST, "/admin/chat/deleteMessage").hasAnyAuthority(UserRole.ADMINISTRATOR.name(),
                UserRole.ACCOUNTANT.name(), UserRole.ADMIN_USER.name())
                .antMatchers("/", "/index.jsp", "/client/**", "/dashboard/**", "/registrationConfirm/**",
                        "/changePasswordConfirm/**", "/changePasswordConfirm/**", "/aboutUs", "/57163a9b3d1eafe27b8b456a.txt", "/newIpConfirm/**").permitAll()
                .antMatchers(HttpMethod.POST, "/merchants/withdrawal/request/accept",
                        "/merchants/withdrawal/request/decline").hasAuthority(AdminAuthority.PROCESS_WITHDRAW.name())
                .antMatchers(HttpMethod.POST, "/merchants/perfectmoney/payment/status",
                        "/merchants/perfectmoney/payment/status",
                        "/merchants/perfectmoney/payment/success",
                        "/merchants/perfectmoney/payment/failure").permitAll()
                .antMatchers(HttpMethod.POST, "/merchants/advcash/payment/status",
                        "/merchants/advcash/payment/success",
                        "/merchants/advcash/payment/failure").permitAll()
                .antMatchers(HttpMethod.POST, "/merchants/liqpay/payment/status",
                        "/merchants/liqpay/payment/success",
                        "/merchants/liqpay/payment/failure").permitAll()
                .antMatchers(HttpMethod.POST, "/merchants/nixmoney/payment/status",
                        "/merchants/nixmoney/payment/success",
                        "/merchants/nixmoney/payment/failure").permitAll()
                .antMatchers(HttpMethod.POST, "/merchants/privat24/payment/status",
                        "/merchants/privat24/payment/success",
                        "/merchants/privat24/payment/failure").permitAll()
                .antMatchers(HttpMethod.POST, "/merchants/interkassa/payment/status",
                        "/merchants/interkassa/payment/success",
                        "/merchants/interkassa/payment/failure").permitAll()
                .antMatchers(HttpMethod.POST, "/merchants/yandex_kassa/payment/status",
                        "/merchants/yandex_kassa/payment/success",
                        "/merchants/yandex_kassa/payment/failure").permitAll()
                .antMatchers(HttpMethod.POST, "/chat-en/**", "/chat-ru/**", "/chat-cn/**").permitAll()
                .antMatchers(HttpMethod.GET, "/chat-en/**", "/chat-ru/**", "/chat-cn/**", "/chat/history").permitAll()
                .antMatchers(HttpMethod.GET, "/generateReferral").permitAll()
                .antMatchers(HttpMethod.POST, "/merchants/edrcoin/payment/received").permitAll()
                .antMatchers(HttpMethod.GET, "/merchants/blockchain/payment/received").permitAll()
                .antMatchers(HttpMethod.GET, "/merchants/yandexmoney/token/access").permitAll()
                .antMatchers(HttpMethod.GET, "/rest/yandexmoney/payment/process").permitAll()
                .antMatchers(HttpMethod.GET, "/public/**").permitAll()
                .antMatchers(HttpMethod.GET, "/favicon.ico").permitAll()
                .antMatchers(HttpMethod.GET, "/news/**").permitAll()
                .antMatchers("/stickyImg").permitAll()
                .antMatchers("/simpleCaptcha").permitAll()
                .antMatchers("/botdetectcaptcha").permitAll()
                .antMatchers(HttpMethod.GET, "/com/captcha/botdetect/**").permitAll()
                .antMatchers(HttpMethod.POST, "/captchaSubmit").permitAll()
                .antMatchers(HttpMethod.POST, "/news/addNewsVariant").authenticated()
                .antMatchers("/yandex_4b3a16d69d4869cb.html").permitAll()
                .antMatchers("/yandex_7a3c41ddb19f4716.html").permitAll()
                .antMatchers("/termsAndConditions", "/privacyPolicy", "/contacts").permitAll()
                .antMatchers(HttpMethod.POST, "/sendFeedback").permitAll()
                .antMatchers(HttpMethod.POST, "/rest/user/register", "/rest/user/authenticate", "/rest/user/restorePassword").anonymous()
                .antMatchers(HttpMethod.GET, "/rest/userFiles/**/avatar/**").permitAll()

//                .antMatchers("/login", "/register", "/create", "/forgotPassword/**", "/resetPasswordConfirm/**").anonymous()
//                .antMatchers("/updatePassword").hasAnyAuthority(UserRole.ROLE_CHANGE_PASSWORD.name())
//                .anyRequest().authenticated()
//                .anyRequest().hasAnyAuthority(UserRole.ADMINISTRATOR.name(), UserRole.ACCOUNTANT.name(), UserRole.ADMIN_USER.name(), UserRole.USER.name())
                .antMatchers("/login", "/register", "/create", "/forgotPassword/**", "/resetPasswordConfirm/**", "/rest/user/resetPasswordConfirm/**").anonymous()
                .antMatchers("/updatePassword").hasAnyAuthority(UserRole.ROLE_CHANGE_PASSWORD.name())
//                .anyRequest().authenticated()
                .anyRequest().hasAnyAuthority(UserRole.ADMINISTRATOR.name(), UserRole.ACCOUNTANT.name(), UserRole.ADMIN_USER.name(), UserRole.USER.name())
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler());
      //          .accessDeniedPage("/403");
        SessionManagementConfigurer<HttpSecurity> sessionConfigurer = http.sessionManagement();
        sessionConfigurer
                .sessionAuthenticationStrategy(new NullAuthenticatedSessionStrategy())
                .maximumSessions(MAXIMUM_SESSIONS)
                .sessionRegistry(sessionRegistry())
                .expiredUrl("/dashboard")
                .maxSessionsPreventsLogin(false);

        //init and configure methods are required to instantiate the composite SessionAuthenticationStrategy, which is later passed to custom auth filter
        sessionConfigurer.init(http);
        sessionConfigurer.configure(http);
        SessionAuthenticationStrategy authenticationStrategy = http.getSharedObject(SessionAuthenticationStrategy.class);
        customUsernamePasswordAuthenticationFilter().setSessionAuthenticationStrategy(authenticationStrategy);
        customQRAuthorizationFilter().setAuthenticationStrategy(authenticationStrategy);

       http.formLogin()
                .loginPage("/login")
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
                .ignoringAntMatchers("/chat-en/**", "/chat-ru/**", "/chat-cn/**",
                        "/merchants/perfectmoney/payment/status",
                        "/merchants/perfectmoney/payment/failure",
                        "/merchants/perfectmoney/payment/success", "/merchants/advcash/payment/status",
                        "/merchants/advcash/payment/failure",
                        "/merchants/advcash/payment/success",
                        "/merchants/edrcoin/payment/received",
                        "/merchants/liqpay/payment/failure",
                        "/merchants/liqpay/payment/success",
                        "/merchants/liqpay/payment/status",
                        "/merchants/nixmoney/payment/failure",
                        "/merchants/nixmoney/payment/success",
                        "/merchants/nixmoney/payment/status",
                        "/merchants/privat24/payment/failure",
                        "/merchants/privat24/payment/success",
                        "/merchants/privat24/payment/status",
                        "/merchants/interkassa/payment/failure",
                        "/merchants/interkassa/payment/success",
                        "/merchants/interkassa/payment/status",
                        "/merchants/yandex_kassa/payment/failure",
                        "/merchants/yandex_kassa/payment/success",
                        "/merchants/yandex_kassa/payment/status",
                        "/rest/user/register", "/rest/user/authenticate", "/rest/user/restorePassword");
        http
                .headers()
                .frameOptions()
                .sameOrigin();


    }
}
