package me.exrates.security.config;

import me.exrates.model.enums.UserRole;
import me.exrates.security.filter.CapchaAuthorizationFilter;
import me.exrates.security.filter.LoginFailureHandler;
import me.exrates.security.filter.LoginSuccessHandler;
import me.exrates.security.filter.VerifyReCaptchaSec;
import me.exrates.security.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
@PropertySource("classpath:/merchants/perfectmoney.properties")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserDetailsServiceImpl userDetailsService;
    private
    @Value("${ipWhiteList}")
    String ipWhiteList;

    @Bean
    public VerifyReCaptchaSec verifyReCaptcha() {
        return new VerifyReCaptchaSec();
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

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(customUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http
                .csrf()
                    .ignoringAntMatchers("/chat/**")
                    .and()
                .headers()
                    .frameOptions().sameOrigin();
        http
                .authorizeRequests()
                .antMatchers("/admin/withdrawal").hasAnyAuthority(UserRole.ADMINISTRATOR.name(), UserRole.ACCOUNTANT.name())
                .antMatchers("/admin/**", "/admin").hasAnyAuthority(UserRole.ADMINISTRATOR.name(),
                UserRole.ACCOUNTANT.name(), UserRole.ADMIN_USER.name())
                .antMatchers("/companywallet").hasAnyAuthority(UserRole.ADMINISTRATOR.name(), UserRole.ACCOUNTANT.name())
                .antMatchers("/index.jsp", "/client/**", "/dashboard/**", "/registrationConfirm/**",
                        "/changePasswordConfirm/**", "/changePasswordConfirm/**", "/aboutUs", "/57163a9b3d1eafe27b8b456a.txt", "/newIpConfirm/**").permitAll()
                .antMatchers(HttpMethod.POST, "/merchants/withdrawal/request/accept",
                        "/merchants/withdrawal/request/decline").hasAnyAuthority(UserRole.ADMINISTRATOR.name(), UserRole.ACCOUNTANT.name())
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
                .antMatchers(HttpMethod.POST, "/chat/**").permitAll()
                .antMatchers(HttpMethod.POST, "/merchants/edrcoin/payment/received").permitAll()
                .antMatchers(HttpMethod.GET, "/merchants/blockchain/payment/received").permitAll()
                .antMatchers(HttpMethod.GET, "/public/**").permitAll()
                .antMatchers(HttpMethod.GET, "/news/**").authenticated()
                .antMatchers(HttpMethod.POST, "/news/addNewsVariant").authenticated()
                .antMatchers("/yandex_4b3a16d69d4869cb.html").permitAll()
                .antMatchers("/yandex_7a3c41ddb19f4716.html").permitAll()
//                .antMatchers("/login", "/register", "/create", "/forgotPassword/**", "/resetPasswordConfirm/**").anonymous()
//                .antMatchers("/updatePassword").hasAnyAuthority(UserRole.ROLE_CHANGE_PASSWORD.name())
//                .anyRequest().authenticated()
//                .anyRequest().hasAnyAuthority(UserRole.ADMINISTRATOR.name(), UserRole.ACCOUNTANT.name(), UserRole.ADMIN_USER.name(), UserRole.USER.name())
                .antMatchers("/login", "/register", "/create", "/forgotPassword/**", "/resetPasswordConfirm/**").anonymous()
                .antMatchers("/updatePassword").hasAnyAuthority(UserRole.ROLE_CHANGE_PASSWORD.name())
//                .anyRequest().authenticated()
                .anyRequest().hasAnyAuthority(UserRole.ADMINISTRATOR.name(), UserRole.ACCOUNTANT.name(), UserRole.ADMIN_USER.name(), UserRole.USER.name())
                .and()
                .exceptionHandling().accessDeniedPage("/403");
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
                .ignoringAntMatchers("/chat","/merchants/perfectmoney/payment/status",
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
                        "/merchants/yandex_kassa/payment/status");
    }

    private String buildHasIpExpression() {
        return Stream.of(ipWhiteList.split(";"))
                .map(ip -> String.format("hasIpAddress('%s')", ip))
                .collect(Collectors.joining(" or "));
    }
}