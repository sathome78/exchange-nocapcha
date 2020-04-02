package me.exrates.config;

import co.elastic.apm.attach.ElasticApmAttacher;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.log4j.Log4j2;
import me.exrates.aspect.LoggingAspect;
import me.exrates.config.ext.JsonMimeInterceptor;
import me.exrates.controller.filter.LoggingFilter;
import me.exrates.controller.handler.ChatWebSocketHandler;
import me.exrates.controller.interceptor.MDCInterceptor;
import me.exrates.controller.interceptor.SecurityInterceptor;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.converter.CurrencyPairConverter;
import me.exrates.model.dto.MosaicIdDto;
import me.exrates.model.enums.ChatLang;
import me.exrates.security.config.SecurityConfig;
import me.exrates.service.BitcoinService;
import me.exrates.service.MoneroService;
import me.exrates.service.NamedParameterJdbcTemplateWrapper;
import me.exrates.service.achain.AchainContract;
import me.exrates.service.binance.BinTokenService;
import me.exrates.service.binance.BinTokenServiceImpl;
import me.exrates.service.binance.BinanceCurrencyService;
import me.exrates.service.binance.BinanceCurrencyServiceImpl;
import me.exrates.service.binance.BinanceService;
import me.exrates.service.binance.BinanceServiceImpl;
import me.exrates.service.ethereum.EthTokenService;
import me.exrates.service.ethereum.EthTokenServiceImpl;
import me.exrates.service.ethereum.EthereumCommonService;
import me.exrates.service.ethereum.EthereumCommonServiceImpl;
import me.exrates.service.ethereum.ExConvert;
import me.exrates.service.handler.RestResponseErrorHandler;
import me.exrates.service.impl.BitcoinServiceImpl;
import me.exrates.service.impl.HCXPServiceImpl;
import me.exrates.service.impl.MoneroServiceImpl;
import me.exrates.service.job.QuartzJobFactory;
import me.exrates.service.nem.XemMosaicService;
import me.exrates.service.nem.XemMosaicServiceImpl;
import me.exrates.service.properties.InOutProperties;
import me.exrates.service.qtum.QtumTokenService;
import me.exrates.service.qtum.QtumTokenServiceImpl;
import me.exrates.service.stellar.StellarAsset;
import me.exrates.service.token.TokenScheduler;
import me.exrates.service.util.ChatComponent;
import me.exrates.service.util.RequestUtil;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.flywaydb.core.Flyway;
import org.nem.core.model.primitive.Supply;
import org.quartz.Scheduler;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.zeromq.ZMQ;

import javax.annotation.PostConstruct;
import javax.servlet.annotation.MultipartConfig;
import javax.sql.DataSource;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.io.FileInputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Log4j2(topic = "config")
@EnableAsync
@Configuration
@EnableWebMvc
@EnableTransactionManagement
@EnableScheduling
@EnableAspectJAutoProxy
@ComponentScan({"me.exrates"})
@Import(
        {
                SecurityConfig.class,
                WebSocketConfig.class,
                CryptocurrencyConfig.class
        }
)
@PropertySource(value = {
        "classpath:/db.properties",
        "classpath:/uploadfiles.properties",
        "classpath:/news.properties",
        "classpath:/mail.properties",
        "classpath:/angular.properties",
        "classpath:/twitter.properties",
        "classpath:/angular.properties",
        "classpath:/merchants/stellar.properties",
        "classpath:/geetest.properties",
        "classpath:/merchants/qiwi.properties"})
@MultipartConfig(location = "/tmp")
public class WebAppConfig extends WebMvcConfigurerAdapter {


    public static final String NODE_TOKEN = "TEMPO_TOKEN";
    private final InOutProperties inOutProperties;
    @Value("${news.ext.locationDir}")
    String newsExtLocationDir;
    @Value("${mail_support.host}")
    String mailSupportHost;
    @Value("${mail_support.port}")
    String mailSupportPort;
    @Value("${mail_support.protocol}")
    String mailSupportProtocol;
    @Value("${mail_support.user}")
    String mailSupportUser;
    @Value("${mail_support.password}")
    String mailSupportPassword;
    @Value("${mail_mandrill.host}")
    String mailMandrillHost;
    @Value("${mail_mandrill.port}")
    String mailMandrillPort;
    @Value("${mail_mandrill.protocol}")
    String mailMandrillProtocol;
    @Value("${mail_mandrill.user}")
    String mailMandrillUser;
    @Value("${mail_mandrill.password}")
    String mailMandrillPassword;
    @Value("${mail_info.host}")
    String mailInfoHost;
    @Value("${mail_info.port}")
    String mailInfoPort;
    @Value("${mail_info.protocol}")
    String mailInfoProtocol;
    @Value("${mail_info.user}")
    String mailInfoUser;
    @Value("${mail_info.password}")
    String mailInfoPassword;
    @Value("${mail_ses.host}")
    String mailSesHost;
    @Value("${mail_ses.port}")
    String mailSesPort;
    @Value("${mail_ses.protocol}")
    String mailSesProtocol;
    @Value("${mail_ses.user}")
    String mailSesUser;
    @Value("${mail_ses.password}")
    String mailSesPassword;
    @Value("${mail_send_grid.host}")
    String mailSendGridHost;
    @Value("${mail_send_grid.port}")
    String mailSendGridPort;
    @Value("${mail_send_grid.protocol}")
    String mailSendGridProtocol;
    @Value("${mail_send_grid.user}")
    String mailSendGridUser;
    @Value("${mail_send_grid.password}")
    String mailSendGridPassword;
    @Value("${db.properties.file}")
    private String dbPropertiesFile;
    @Value("${db.properties.outer.file}")
    private Boolean isOuterFile;
    private
    @Value("${upload.userFilesDir}")
    String userFilesDir;
    private
    @Value("${upload.userFilesLogicalDir}")
    String userFilesLogicalDir;
    private
    @Value("${news.locationDir}")
    String newsLocationDir;
    private
    @Value("${news.urlPath}")
    String newsUrlPath;
    @Value("${news.newstopic.urlPath}")
    private String newstopicUrlPath;
    @Value("${news.materialsView.urlPath}")
    private String materialsViewUrlPath;
    @Value("${news.webinar.urlPath}")
    private String webinarUrlPath;
    @Value("${news.event.urlPath}")
    private String eventUrlPath;
    @Value("${news.feastDay.urlPath}")
    private String feastDayUrlPath;
    @Value("${news.page.urlPath}")
    private String pageUrlPath;
    @Value("${angular.allowed.origins}")
    private String[] angularAllowedOrigins;
    @Value("${angular.allowed.methods}")
    private String[] angularAllowedMethods;
    @Value("${angular.allowed.headers}")
    private String[] angularAllowedHeaders;
    @Value("${twitter.appId}")
    private String twitterConsumerKey;
    @Value("${twitter.appSecret}")
    private String twitterConsumerSecret;
    @Value("${twitter.accessToken}")
    private String twitterAccessToken;
    @Value("${twitter.accessTokenSecret}")
    private String twitterAccessTokenSecret;
    @Value("${geetest.captchaId}")
    private String gtCaptchaId;
    @Value("${geetest.privateKey}")
    private String gtPrivateKey;
    @Value("${geetest.newFailback}")
    private String gtNewFailback;
    @Value("${qiwi.client.id}")
    private String qiwiClientId;
    @Value("${qiwi.client.secret}")
    private String qiwiClientSecret;
    private String dbMasterUser;
    private String dbMasterPassword;
    private String dbMasterUrl;
    private String dbMasterClassname;
    private String dbSlaveUser;
    private String dbSlavePassword;
    private String dbSlaveUrl;
    private String dbSlaveClassname;
    private String dbSlaveForReportsUser;
    private String dbSlaveForReportsPassword;
    private String dbSlaveForReportsUrl;
    private String dbSlaveForReportsClassname;
    /***stellarAssets****/
    private @Value("${stellar.slt.emitter}")
    String SLT_EMMITER;

    public WebAppConfig(InOutProperties inOutProperties) {
        this.inOutProperties = inOutProperties;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @PostConstruct
    public void init() {
        ElasticApmAttacher.attach();
        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        List<String> arguments = runtimeMxBean.getInputArguments();
        log.debug(String.join("; ", arguments));
        Properties properties = new Properties();
        try {
            if (isOuterFile) {
                properties.load(new FileInputStream(dbPropertiesFile));
            } else {
                properties.load(getClass().getClassLoader().getResourceAsStream(dbPropertiesFile));
            }
        } catch (Exception e) {
            throw new RuntimeException("Db properties not loaded");
        }
        dbMasterUser = properties.getProperty("db.master.user");
        dbMasterPassword = properties.getProperty("db.master.password");
        dbMasterUrl = properties.getProperty("db.master.url");
        dbMasterClassname = properties.getProperty("db.master.classname");
        dbSlaveUser = properties.getProperty("db.slave.user");
        dbSlavePassword = properties.getProperty("db.slave.password");
        dbSlaveUrl = properties.getProperty("db.slave.url");
        dbSlaveClassname = properties.getProperty("db.slave.classname");
        dbSlaveForReportsUser = properties.getProperty("db.slave.user");
        dbSlaveForReportsPassword = properties.getProperty("db.slave.password");
        dbSlaveForReportsUrl = properties.getProperty("db.slave.url");
        dbSlaveForReportsClassname = properties.getProperty("db.slave.classname");
    }

    /*@Bean(name = "dataSource")*/
    public DataSource dataSource() {
        final BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(dbMasterClassname);
        dataSource.setUrl(dbMasterUrl);
        dataSource.setUsername(dbMasterUser);
        dataSource.setPassword(dbMasterPassword);
        return dataSource;
    }

    @Bean(name = "masterHikariDataSource")
    public DataSource masterHikariDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(dbMasterClassname);
        hikariConfig.setJdbcUrl(dbMasterUrl);
        hikariConfig.setUsername(dbMasterUser);
        hikariConfig.setPassword(dbMasterPassword);
        hikariConfig.setMaximumPoolSize(50);
        DataSource dataSource = new HikariDataSource(hikariConfig);
//        Flyway flyway = new Flyway();
//        flyway.setDataSource(dataSource);
//        flyway.setBaselineOnMigrate(true);
//        flyway.setOutOfOrder(true);
//        flyway.repair();
//        flyway.migrate();
        return dataSource;
    }

    @Bean(name = "slaveHikariDataSource")
    public DataSource slaveHikariDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(dbSlaveClassname);
        hikariConfig.setJdbcUrl(dbSlaveUrl);
        hikariConfig.setUsername(dbSlaveUser);
        hikariConfig.setPassword(dbSlavePassword);
        hikariConfig.setMaximumPoolSize(50);
        hikariConfig.setReadOnly(true);
        return new HikariDataSource(hikariConfig);
    }

    @Bean(name = "slaveForReportsDataSource")
    public DataSource slaveForReportsDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(dbSlaveForReportsClassname);
        hikariConfig.setJdbcUrl(dbSlaveForReportsUrl);
        hikariConfig.setUsername(dbSlaveForReportsUser);
        hikariConfig.setPassword(dbSlaveForReportsPassword);
        hikariConfig.setMaximumPoolSize(50);
        hikariConfig.setReadOnly(true);
        return new HikariDataSource(hikariConfig);
    }

    @Primary
    @DependsOn("masterHikariDataSource")
    @Bean(name = "masterTemplate")
    public NamedParameterJdbcTemplate masterNamedParameterJdbcTemplate(@Qualifier("masterHikariDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplateWrapper(dataSource);
    }

    @DependsOn("slaveHikariDataSource")
    @Bean(name = "slaveTemplate")
    public NamedParameterJdbcTemplate slaveNamedParameterJdbcTemplate(@Qualifier("slaveHikariDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplateWrapper(dataSource);
    }

    @DependsOn("slaveForReportsDataSource")
    @Bean(name = "slaveForReportsTemplate")
    public NamedParameterJdbcTemplate slaveForReportsTemplate(@Qualifier("slaveForReportsDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplateWrapper(dataSource);
    }

    @Primary
    @DependsOn("masterHikariDataSource")
    @Bean
    public JdbcTemplate jdbcTemplate(@Qualifier("masterHikariDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Primary
    @Bean(name = "masterTxManager")
    public PlatformTransactionManager masterPlatformTransactionManager() {
        return new DataSourceTransactionManager(masterHikariDataSource());
    }

    @Bean(name = "slaveTxManager")
    public PlatformTransactionManager slavePlatformTransactionManager() {
        return new DataSourceTransactionManager(slaveHikariDataSource());
    }

    @Bean(name = "transactionManagerForReports")
    public PlatformTransactionManager transactionManagerForReports() {
        return new DataSourceTransactionManager(slaveForReportsDataSource());
    }

    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/WEB-INF/jsp/");
        viewResolver.setSuffix(".jsp");
        viewResolver.setExposedContextBeanNames("captchaProperties");
        return viewResolver;
    }

    @Bean(name = "captchaProperties")
    public PropertiesFactoryBean captchaProperties() {
        PropertiesFactoryBean prop = new PropertiesFactoryBean();
        prop.setLocation(new ClassPathResource("captcha.properties"));
        return prop;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/api/**")
                .allowedOrigins(angularAllowedOrigins)
                .allowedMethods(angularAllowedMethods)
                .allowedHeaders(angularAllowedHeaders);
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }

    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setDefaultLocale(new Locale("en"));
        resolver.setCookieName("myAppLocaleCookie");
        resolver.setCookieMaxAge(3600);
        return resolver;
    }

    @Bean(name = "AcceptHeaderLocaleResolver")
    public LocaleResolver localeResolverRest() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(new Locale("en"));
        return resolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/client/**").addResourceLocations("/client/");
        registry.addResourceHandler("/favicon.ico").addResourceLocations("/client/img/");
        registry.addResourceHandler("/**").addResourceLocations("/public/");
        registry.addResourceHandler(newsUrlPath + "/**").addResourceLocations("file:" + newsLocationDir);
        registry.addResourceHandler(userFilesLogicalDir + "/**").addResourceLocations("file:" + userFilesDir);
        registry.addResourceHandler(newstopicUrlPath + "/**").addResourceLocations("file:" + newsExtLocationDir);
        registry.addResourceHandler(materialsViewUrlPath + "/**").addResourceLocations("file:" + newsExtLocationDir);
        registry.addResourceHandler(webinarUrlPath + "/**").addResourceLocations("file:" + newsExtLocationDir);
        registry.addResourceHandler(eventUrlPath + "/**").addResourceLocations("file:" + newsExtLocationDir);
        registry.addResourceHandler(feastDayUrlPath + "/**").addResourceLocations("file:" + newsExtLocationDir);
        registry.addResourceHandler(pageUrlPath + "/**").addResourceLocations("file:" + newsExtLocationDir);
        registry.addResourceHandler("/rest" + userFilesLogicalDir + "/**").addResourceLocations("file:" + userFilesDir);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("locale");
        registry.addInterceptor(interceptor);
        registry.addInterceptor(new SecurityInterceptor());
        registry.addInterceptor(new MDCInterceptor());
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setDefaultTimeout(120_000L);
        super.configureAsyncSupport(configurer);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(name = "SupportMailSender")
    public JavaMailSenderImpl javaMailSenderImpl() {
        final JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
        mailSenderImpl.setHost(mailSupportHost);
        mailSenderImpl.setPort(Integer.parseInt(mailSupportPort));
        mailSenderImpl.setProtocol(mailSupportProtocol);
        mailSenderImpl.setUsername(mailSupportUser);
        mailSenderImpl.setPassword(mailSupportPassword);
        final Properties javaMailProps = new Properties();
        javaMailProps.put("mail.smtp.auth", true);
        javaMailProps.put("mail.smtp.starttls.enable", false);
        javaMailProps.put("mail.smtp.ssl.trust", mailSupportHost);
        mailSenderImpl.setJavaMailProperties(javaMailProps);
        return mailSenderImpl;
    }

    @Bean(name = "SesMailSender")
    public JavaMailSenderImpl sesMailSenderImpl() {
        final JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
        mailSenderImpl.setHost(mailSesHost);
        mailSenderImpl.setPort(Integer.parseInt(mailSesPort));
        mailSenderImpl.setProtocol(mailSesProtocol);
        mailSenderImpl.setUsername(mailSesUser);
        mailSenderImpl.setPassword(mailSesPassword);
        final Properties javaMailProps = mailSenderImpl.getJavaMailProperties();
        javaMailProps.put("mail.smtp.auth", true);
        javaMailProps.put("mail.smtp.starttls.enable", true);
        javaMailProps.put("mail.smtp.ssl.trust", mailSesHost);
        return mailSenderImpl;
    }

    @Bean(name = "SendGridMailSender")
    public JavaMailSenderImpl javaSendGridMailSenderImpl() {
        final JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
        mailSenderImpl.setHost(mailSendGridHost);
        mailSenderImpl.setPort(Integer.parseInt(mailSendGridPort));
        mailSenderImpl.setProtocol(mailSendGridProtocol);
        mailSenderImpl.setUsername(mailSendGridUser);
        mailSenderImpl.setPassword(mailSendGridPassword);
        final Properties javaMailProps = new Properties();
        javaMailProps.put("mail.smtp.auth", true);
        javaMailProps.put("mail.smtp.starttls.enable", false);
        javaMailProps.put("mail.smtp.ssl.trust", mailSendGridHost);
        return mailSenderImpl;
    }

    @Bean(name = "MandrillMailSender")
    public JavaMailSenderImpl mandrillMailSenderImpl() {
        final JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
        mailSenderImpl.setHost(mailMandrillHost);
        mailSenderImpl.setPort(Integer.parseInt(mailMandrillPort));
        mailSenderImpl.setProtocol(mailMandrillProtocol);
        mailSenderImpl.setUsername(mailMandrillUser);
        mailSenderImpl.setPassword(mailMandrillPassword);
        final Properties javaMailProps = new Properties();
        javaMailProps.put("mail.smtp.auth", true);
        javaMailProps.put("mail.smtp.starttls.enable", true);
        javaMailProps.put("mail.smtp.ssl.trust", mailMandrillHost);
        mailSenderImpl.setJavaMailProperties(javaMailProps);
        return mailSenderImpl;
    }

    @Bean(name = "InfoMailSender")
    public JavaMailSenderImpl infoMailSenderImpl() {
        final JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
        mailSenderImpl.setHost(mailInfoHost);
        mailSenderImpl.setPort(Integer.parseInt(mailInfoPort));
        mailSenderImpl.setProtocol(mailInfoProtocol);
        mailSenderImpl.setUsername(mailInfoUser);
        mailSenderImpl.setPassword(mailInfoPassword);
        final Properties javaMailProps = new Properties();
        javaMailProps.put("mail.smtp.auth", true);
        javaMailProps.put("mail.smtp.starttls.enable", true);
        javaMailProps.put("mail.smtp.ssl.trust", mailInfoHost);
        mailSenderImpl.setJavaMailProperties(javaMailProps);
        return mailSenderImpl;
    }

    @Bean(name = "tokenScheduler", initMethod = "init", destroyMethod = "destroy")
    public TokenScheduler tokenScheduler() {
        return new TokenScheduler();
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new CurrencyPairConverter());
        super.addFormatters(registry);
    }

    @Bean
    public EnumMap<ChatLang, ChatWebSocketHandler> handlers() {
        final EnumMap<ChatLang, ChatWebSocketHandler> handlers = new EnumMap<>(ChatLang.class);
        for (ChatLang lang : ChatLang.values()) {
            handlers.put(lang, new ChatWebSocketHandler());
        }
        return handlers;
    }

    @Bean
    public EnumMap<ChatLang, ChatComponent> chatComponents() {
        final EnumMap<ChatLang, ChatComponent> handlers = new EnumMap<>(ChatLang.class);
        for (ChatLang lang : ChatLang.values()) {
            final ChatComponent chatComponent = new ChatComponent(new ReentrantReadWriteLock(), new TreeSet<>());
            handlers.put(lang, chatComponent);
        }
        return handlers;
    }

    @Bean(name = "multipartResolver")
    public StandardServletMultipartResolver resolver() {
        return new StandardServletMultipartResolver();
    }

    @Bean
    public LoggingAspect loggingAspect() {
        return new LoggingAspect();
    }

    @Bean(name = "nsrServiceImpl")
    @Conditional(MonolitConditional.class)
    public BitcoinService nsrService() {
        return new BitcoinServiceImpl("merchants/nushares_wallet.properties",
                "NuShares", "NSR", 30, 20, false, false);
    }

    @Bean(name = "amlServiceImpl")
    @Conditional(MonolitConditional.class)
    public BitcoinService amlService() {
        return new BitcoinServiceImpl("merchants/aml_wallet.properties",
                "AML", "ABTC", 30, 20, false);
    }

    @Bean(name = "bbccServiceImpl")
    @Conditional(MonolitConditional.class)
    public BitcoinService bbccService() {
        return new BitcoinServiceImpl("merchants/bbcc_wallet.properties",
                "BBX", "BBX", 30, 20, false, false, false);
    }

    @Bean(name = "hsrServiceImpl")
    @Conditional(MonolitConditional.class)
    public BitcoinService hcasheService() {
        return new BitcoinServiceImpl("merchants/hsr_wallet.properties",
                "HSR", "HSR", 30, 20, false, false);
    }

    @Bean(name = "ethereumServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthereumCommonService ethereumService() {
        return new EthereumCommonServiceImpl("merchants/ethereum.properties",
                "Ethereum", "ETH", 15);
    }

    @Bean(name = "ethereumClassicServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthereumCommonService ethereumClassicService() {
        return new EthereumCommonServiceImpl("merchants/ethereumClassic.properties",
                "Ethereum Classic", "ETC", 400);
    }

    @Bean(name = "etzServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthereumCommonService etzService() {
        return new EthereumCommonServiceImpl("merchants/etherzero.properties",
                "EtherZero", "ETZ", 40);
    }

    @Bean(name = "cloServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthereumCommonService cloService() {
        return new EthereumCommonServiceImpl("merchants/callisto.properties",
                "CLO", "CLO", 300);
    }

    @Bean(name = "b2gServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthereumCommonService b2gService() {
        return new EthereumCommonServiceImpl("merchants/bitcoiin2g.properties",
                "B2G", "B2G", 400);
    }

    @Bean(name = "golServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthereumCommonService golService() {
        return new EthereumCommonServiceImpl("merchants/goldiam.properties",
                "GOL", "GOL", 40);
    }

    @Bean(name = "cnetServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthereumCommonService cnetService() {
        return new EthereumCommonServiceImpl("merchants/contractnet.properties",
                "CNET", "CNET", 110);
    }

    @Bean(name = "ntyServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthereumCommonService ntyService() {
        return new EthereumCommonServiceImpl("merchants/nexty.properties",
                "NTY", "NTY", 40);
    }

//    @Bean(name = "eosServiceImpl")
//    @Conditional(MonolitConditional.class)
//    public EthTokenService EosService() {
//        List<String> tokensList = new ArrayList<>();
//        tokensList.add("0x86fa049857e0209aa7d9e616f7eb3b3b78ecfdb0");
//        return new EthTokenServiceImpl(
//                tokensList,
//                "EOS",
//                "EOS", true, ExConvert.Unit.ETHER);
//    }

    @Bean(name = "etherincServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthereumCommonService etherincService() {
        return new EthereumCommonServiceImpl("merchants/eti.properties",
                "ETI", "ETI", 50);
    }

    @Bean(name = "repServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService RepService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x1985365e9f78359a9b6ad760e32412f4a445e862");
        return new EthTokenServiceImpl(
                tokensList,
                "REP",
                "REP", false, ExConvert.Unit.ETHER);
    }

    @Bean(name = "golemServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService GolemService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xa74476443119a942de498590fe1f2454d7d4ac0d");
        return new EthTokenServiceImpl(
                tokensList,
                "Golem",
                "GNT", false, ExConvert.Unit.ETHER);
    }

    @Bean(name = "omgServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService OmgService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xd26114cd6ee289accf82350c8d8487fedb8a0c07");
        return new EthTokenServiceImpl(
                tokensList,
                "OmiseGo",
                "OMG", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "atlServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService ATLANTService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x78b7fada55a64dd895d8c8c35779dd8b67fa8a05");
        return new EthTokenServiceImpl(
                tokensList,
                "ATLANT",
                "ATL", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "bitRentServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService BitRentService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x1fe70be734e473e5721ea57c8b5b01e6caa52686");
        return new EthTokenServiceImpl(
                tokensList,
                "BitRent",
                "RNTB", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "nioServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService NioService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x5554e04e76533e1d14c52f05beef6c9d329e1e30");
        return new EthTokenServiceImpl(
                tokensList,
                "NIO",
                "NIO", true, ExConvert.Unit.WEI);
    }

    @Bean(name = "gosServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService GosService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x5ce8e61f28f5948de4913bcaada90039481f1f53");
        return new EthTokenServiceImpl(
                tokensList,
                "GOS",
                "GOS", true, ExConvert.Unit.MWEI);
    }

    @Bean(name = "bptnServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService BptnRentService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x6c22b815904165f3599f0a4a092d458966bd8024");
        return new EthTokenServiceImpl(
                tokensList,
                "BPTN",
                "BPTN", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "nbcServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService NbcService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x9f195617fa8fbad9540c5d113a99a0a0172aaedc");
        return new EthTokenServiceImpl(
                tokensList,
                "NBC",
                "NBC", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "taxiServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService taxiRentService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x8409e9c7d23ae978e809866abf46ac2e116f4d0e");
        return new EthTokenServiceImpl(
                tokensList,
                "TAXI",
                "TAXI", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "nbtkServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService nbtkRentService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xb0e6f83eba6a4ea20617e134b1aee30fcb0ac634");
        return new EthTokenServiceImpl(
                tokensList,
                "NBTK",
                "NBTK", false, ExConvert.Unit.WEI);
    }

    @Bean(name = "ucashServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService ucashService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x92e52a1a235d9a103d970901066ce910aacefd37");
        return new EthTokenServiceImpl(
                tokensList,
                "UCASH",
                "UCASH", true, ExConvert.Unit.AIWEI);
    }

    @Bean(name = "nacServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService nacService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x8d80de8a78198396329dfa769ad54d24bf90e7aa");
        return new EthTokenServiceImpl(
                tokensList,
                "NAC",
                "NAC", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "echtServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService echtService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x1a2277c83930b7a64c3e3d5544eaa8c4f946b1b7");
        return new EthTokenServiceImpl(
                tokensList,
                "ECHT",
                "ECHT", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "idhServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService idhService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x5136c98a80811c3f46bdda8b5c4555cfd9f812f0");
        return new EthTokenServiceImpl(
                tokensList,
                "IDH",
                "IDH", false, ExConvert.Unit.MWEI);
    }

    @Bean(name = "cobcServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService cobcService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x6292cec07c345c6c6953e9166324f58db6d9f814");
        return new EthTokenServiceImpl(
                tokensList,
                "COBC",
                "COBC", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "bcsServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService bcsService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x98bde3a768401260e7025faf9947ef1b81295519");
        return new EthTokenServiceImpl(
                tokensList,
                "BCS",
                "BCS", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "uqcServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService uqcService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xd01db73e047855efb414e6202098c4be4cd2423b");
        return new EthTokenServiceImpl(
                tokensList,
                "UQC",
                "UQC", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "inoServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService inoService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xc9859fccc876e6b4b3c749c5d29ea04f48acb74f");
        return new EthTokenServiceImpl(
                tokensList,
                "INO",
                "INO", true, ExConvert.Unit.WEI);
    }

    @Bean(name = "profitServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService profitService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xe540935cabf4c2bac547c8067cbbc2991d030122");
        return new EthTokenServiceImpl(
                tokensList,
                "PROFIT",
                "PROFIT", false, ExConvert.Unit.ETHER);
    }

    @Bean(name = "ormeServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService ormeService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x516e5436bafdc11083654de7bb9b95382d08d5de");
        return new EthTokenServiceImpl(
                tokensList,
                "ORME",
                "ORME", true, ExConvert.Unit.AIWEI);
    }

    @Bean(name = "bezServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService bezService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x8a1e3930fde1f151471c368fdbb39f3f63a65b55");
        return new EthTokenServiceImpl(
                tokensList,
                "BEZ",
                "BEZ", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "simServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService simService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xd7cd762f3ebc2c9a3d9bcf0133e06d04c59a1f7d");
        return new EthTokenServiceImpl(
                tokensList,
                "SIM",
                "SIM", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "amnServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService amnService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x737f98ac8ca59f2c68ad658e3c3d8c8963e40a4c");
        return new EthTokenServiceImpl(
                tokensList,
                "AMN",
                "AMN", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "getServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService getService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x8a854288a5976036a725879164ca3e91d30c6a1b");
        return new EthTokenServiceImpl(
                tokensList,
                "GET",
                "GET", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "flotServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService flotService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x049399a6b048d52971f7d122ae21a1532722285f");
        return new EthTokenServiceImpl(
                tokensList,
                "FLOT",
                "FLOT", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "vdgServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService vdgService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x57c75eccc8557136d32619a191fbcdc88560d711");
        return new EthTokenServiceImpl(
                tokensList,
                "VDG",
                "VDG", true, ExConvert.Unit.WEI);
    }

    @Bean(name = "dgtxServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService dgtxService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x1c83501478f1320977047008496dacbd60bb15ef");
        return new EthTokenServiceImpl(
                tokensList,
                "DGTX",
                "DGTX", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "droneServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService droneService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x131f193692b5cce8c87d12ff4f7aa1d4e1668f1e");
        return new EthTokenServiceImpl(
                tokensList,
                "DRONE",
                "DRONE", true, ExConvert.Unit.WEI);
    }

    @Bean(name = "wdscServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService wdscService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x170cf89358ce17742955ea43927da5fc1e8e1211");
        return new EthTokenServiceImpl(
                tokensList,
                "WDSC",
                "WDSC", true, ExConvert.Unit.ETHER, new BigInteger("1"));
    }

    @Bean(name = "fsbtServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService fsbtService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x1ed7ae1f0e2fa4276dd7ddc786334a3df81d50c0");
        return new EthTokenServiceImpl(
                tokensList,
                "FSBT",
                "FSBT", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "iprServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService iprService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x069bc4608a8764924ab991cb9eb6d6b6caad74c8");
        return new EthTokenServiceImpl(
                tokensList,
                "IPR",
                "IPR", false, ExConvert.Unit.ETHER);
    }

    @Bean(name = "casServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService casService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xe8780b48bdb05f928697a5e8155f672ed91462f7");
        return new EthTokenServiceImpl(
                tokensList,
                "CAS",
                "CAS", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "tnrServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService tnrService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x543199bfd8c343fadd8c1a2bc289e876c588c8e5");
        return new EthTokenServiceImpl(
                tokensList,
                "TNR",
                "TNR", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "inkServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService InkService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xf4c90e18727c5c76499ea6369c856a6d61d3e92e");
        return new EthTokenServiceImpl(
                tokensList,
                "Ink",
                "INK", true, ExConvert.Unit.GWEI);
    }

    @Bean(name = "rthServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService rthService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x3fd8f39a962efda04956981c31ab89fab5fb8bc8");
        return new EthTokenServiceImpl(
                tokensList,
                "RTH",
                "RTH", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "spdServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService SpdService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x1dea979ae76f26071870f824088da78979eb91c8");
        return new EthTokenServiceImpl(
                tokensList,
                "SPD",
                "SPD", false, ExConvert.Unit.ETHER);
    }

    @Bean(name = "mtcServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService MtcService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x905e337c6c8645263d3521205aa37bf4d034e745");
        return new EthTokenServiceImpl(
                tokensList,
                "MTC",
                "MTC", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "hstServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService hstService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x554c20b7c486beee439277b4540a434566dc4c02");
        return new EthTokenServiceImpl(
                tokensList,
                "HST",
                "HST", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "dtrcServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService DtrcService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xc20464e0c373486d2b3335576e83a218b1618a5e");
        return new EthTokenServiceImpl(
                tokensList,
                "DTRC",
                "DTRC", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "ceekServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService CeekService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xb056c38f6b7dc4064367403e26424cd2c60655e1");
        return new EthTokenServiceImpl(
                tokensList,
                "CEEK",
                "CEEK", false, ExConvert.Unit.ETHER);
    }

    @Bean(name = "anyServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService anyService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xdf67cf04f1f268e431bfecf2c76843afb8e536c1");
        return new EthTokenServiceImpl(
                tokensList,
                "ANY",
                "ANY", false, ExConvert.Unit.AIWEI);
    }

    @Bean(name = "tgameServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService tgameService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xf8e06e4e4a80287fdca5b02dccecaa9d0954840f");
        return new EthTokenServiceImpl(
                tokensList,
                "TGAME",
                "TGAME", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "mtlServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService mtlServiceImpl() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xf433089366899d83a9f26a773d59ec7ecf30355e");
        return new EthTokenServiceImpl(
                tokensList,
                "MTL",
                "MTL", true, ExConvert.Unit.AIWEI);
    }

    @Bean(name = "leduServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService leduService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x5b26c5d0772e5bbac8b3182ae9a13f9bb2d03765");
        return new EthTokenServiceImpl(
                tokensList,
                "LEDU",
                "LEDU", true, ExConvert.Unit.AIWEI);
    }

    @Bean(name = "adbServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService adbService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x2baac9330cf9ac479d819195794d79ad0c7616e3");
        return new EthTokenServiceImpl(
                tokensList,
                "ADB",
                "ADB", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "mocoServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService mocoService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x06a8f2bcc622ac55d596ea02ce5bb5f318f485e9");
        return new EthTokenServiceImpl(
                tokensList,
                "MoCo",
                "MoCo", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "cedexServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService cedexService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xf4065e4477e91c177ded71a7a6fb5ee07dc46bc9");
        return new EthTokenServiceImpl(
                tokensList,
                "CEDEX",
                "CEDEX", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "gstServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService gstService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x67a9099f0008c35c61c00042cd9fb03684451097");
        return new EthTokenServiceImpl(
                tokensList,
                "GST",
                "GST", false, ExConvert.Unit.ETHER);
    }

    @Bean(name = "satServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService satService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xc56b13ebbcffa67cfb7979b900b736b3fb480d78");
        return new EthTokenServiceImpl(
                tokensList,
                "SAT",
                "SAT", true, ExConvert.Unit.AIWEI);
    }

    @Bean(name = "cheServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService cheService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x632f62fcf63cb56380ffd27d63afcf5f1349f73f");
        return new EthTokenServiceImpl(
                tokensList,
                "CHE",
                "CHE", false, ExConvert.Unit.AIWEI);
    }

    @Bean(name = "daccServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService daccService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xf8c595d070d104377f58715ce2e6c93e49a87f3c");
        return new EthTokenServiceImpl(
                tokensList,
                "DACC",
                "DACC", true, ExConvert.Unit.MWEI);
    }

    @Bean(name = "engtServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService engtService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x5dbac24e98e2a4f43adc0dc82af403fca063ce2c");
        return new EthTokenServiceImpl(
                tokensList,
                "ENGT",
                "ENGT", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "tavittServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService tavittService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xdd690d8824c00c84d64606ffb12640e932c1af56");
        return new EthTokenServiceImpl(
                tokensList,
                "TAVITT",
                "TAVITT", true, ExConvert.Unit.AIWEI);
    }

    @Bean(name = "umtServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService umtService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xc6be00f7ed386015a3c751d38c126c62f231138d");
        return new EthTokenServiceImpl(
                tokensList,
                "UMT",
                "UMT", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "maspServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService maspService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xce958ecf2c752c74973e89674faa30404b15a498");
        return new EthTokenServiceImpl(
                tokensList,
                "MASP",
                "MASP", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "skillServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService skillService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x417d6feeae8b2fcb73d14d64be7f192e49431978");
        return new EthTokenServiceImpl(
                tokensList,
                "SKILL",
                "SKILL", false, ExConvert.Unit.ETHER);
    }

    @Bean(name = "storServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService storService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xa3ceac0aac5c5d868973e546ce4731ba90e873c2");
        return new EthTokenServiceImpl(
                tokensList,
                "STOR",
                "STOR", true, ExConvert.Unit.AIWEI);
    }

    @Bean(name = "quintServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService quintService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x45b73654e464945a268032cdcb8d551fe8b733ca");
        return new EthTokenServiceImpl(
                tokensList,
                "QUiNT",
                "QUiNT", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "ttcServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService ttcService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x53e28b07e0795869b727ee4d585b3c025b016952");
        return new EthTokenServiceImpl(
                tokensList,
                "TTC",
                "TTC", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "bfgServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService bfgService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x2ee3b3804f695355ddc4f8e1c54654416d7ee95a");
        return new EthTokenServiceImpl(
                tokensList,
                "BFG",
                "BFG", false, ExConvert.Unit.AIWEI);
    }

    @Bean(name = "jetServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService jetService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x8727c112c712c4a03371ac87a74dd6ab104af768");
        return new EthTokenServiceImpl(
                tokensList,
                "JET",
                "JET", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "patServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService patService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xf3b3cad094b89392fce5fafd40bc03b80f2bc624");
        return new EthTokenServiceImpl(
                tokensList,
                "PAT",
                "PAT", false, ExConvert.Unit.ETHER);
    }

    @Bean(name = "mtvServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService mtvService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x07a7ed332c595b53a317afcee50733af571475e7");
        return new EthTokenServiceImpl(
                tokensList,
                "eMTV",
                "eMTV", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "kwattServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService kwattService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x241ba672574a78a3a604cdd0a94429a73a84a324");
        return new EthTokenServiceImpl(
                tokensList,
                "KWATT",
                "KWATT", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "tusdServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService tusdService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x0000000000085d4780b73119b644ae5ecd22b376");
        return new EthTokenServiceImpl(
                tokensList,
                "TUSD",
                "TUSD", false, ExConvert.Unit.ETHER);
    }

    @Bean(name = "fpwrServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService fpwrService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xdd92e60563250012ee1c4cb4b26810c45a0591da");
        return new EthTokenServiceImpl(
                tokensList,
                "FPWR",
                "FPWR", false, ExConvert.Unit.ETHER);
    }

    @Bean(name = "crbtServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService crbtService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x6b7734c5ecc51116b806e2ea6decbb3b97f4f92e");
        return new EthTokenServiceImpl(
                tokensList,
                "CRBT",
                "CRBT", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "hiveServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService hiveService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x895f5d0b8456b980786656a33f21642807d1471c");
        return new EthTokenServiceImpl(
                tokensList,
                "HIVE",
                "HIVE", false, ExConvert.Unit.AIWEI);
    }

    @Bean(name = "cmitServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService cmitService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xe11609b9a51caf7d32a55896386ac52ed90e66f1");
        return new EthTokenServiceImpl(
                tokensList,
                "CMIT",
                "CMIT", false, ExConvert.Unit.AIWEI);
    }

    @Bean(name = "hdrServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService hdrService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x52494fbffe10f8c29411521040ae8618c334981e");
        return new EthTokenServiceImpl(
                tokensList,
                "HDR",
                "HDR", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "racServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService racService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x342ba159f988f24f0b033f3cc5232377ee500543");
        return new EthTokenServiceImpl(
                tokensList,
                "RAC",
                "RAC", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "iqnServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService iqnService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x0db8d8b76bc361bacbb72e2c491e06085a97ab31");
        return new EthTokenServiceImpl(
                tokensList,
                "IQN",
                "IQN", false, ExConvert.Unit.ETHER);
    }

    @Bean(name = "gexServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService gexService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xdac15794f0fadfdcf3a93aeaabdc7cac19066724");
        return new EthTokenServiceImpl(
                tokensList,
                "GEX",
                "GEX", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "ixeServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService ixeService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x7a07e1a0c2514d51132183ecfea2a880ec3b7648");
        return new EthTokenServiceImpl(
                tokensList,
                "IXE",
                "IXE", false, ExConvert.Unit.ETHER);
    }

    @Bean(name = "nerServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService nerService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xee5dfb5ddd54ea2fb93b796a8a1b83c3fe38e0e6");
        return new EthTokenServiceImpl(
                tokensList,
                "NER",
                "NER", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "phiServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService phiService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x13c2fab6354d3790d8ece4f0f1a3280b4a25ad96");
        return new EthTokenServiceImpl(
                tokensList,
                "PHI",
                "PHI", true, ExConvert.Unit.ETHER);
    }

    //todo: disable to debug
    @Bean(name = "retServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService retService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xd7394087e1dbbe477fe4f1cf373b9ac9459565ff");
        return new EthTokenServiceImpl(
                tokensList,
                "RET",
                "RET", true, ExConvert.Unit.AIWEI);
    }

    @Bean(name = "mftuServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService mftuService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x05d412ce18f24040bb3fa45cf2c69e506586d8e8");
        return new EthTokenServiceImpl(
                tokensList,
                "MFTU",
                "MFTU", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "gigcServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService gigcService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xbf8aa0617df5c542f533b0e95fe2f877906ac327");
        return new EthTokenServiceImpl(
                tokensList,
                "GIGC",
                "GIGC", false, ExConvert.Unit.AIWEI);
    }

    @Bean(name = "swmServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService swmService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x9e88613418cf03dca54d6a2cf6ad934a78c7a17a");
        return new EthTokenServiceImpl(
                tokensList,
                "SWM",
                "SWM", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "ticServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService ticService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x72430a612adc007c50e3b6946dbb1bb0fd3101d1");
        return new EthTokenServiceImpl(
                tokensList,
                "TIC",
                "TIC", true, ExConvert.Unit.AIWEI);
    }

    @Bean(name = "bncServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService bncService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xef51c9377feb29856e61625caf9390bd0b67ea18");
        return new EthTokenServiceImpl(
                tokensList,
                "BNC",
                "BNC", true, ExConvert.Unit.AIWEI);
    }

    @Bean(name = "wtlServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService wtlService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x9a0587eae7ef64b2b38a10442a44cfa43edd7d2a");
        return new EthTokenServiceImpl(
                tokensList,
                "WTL",
                "WTL", false, ExConvert.Unit.ETHER);
    }

    @Bean(name = "uDOOServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService uDOOService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x0df721639ca2f7ff0e1f618b918a65ffb199ac4e");
        return new EthTokenServiceImpl(
                tokensList,
                "uDOO",
                "uDOO", false, ExConvert.Unit.ETHER);
    }

    @Bean(name = "xauServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService xauService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xadf07ae026c660968223f9f376a928523f248b69");
        return new EthTokenServiceImpl(
                tokensList,
                "XAU",
                "XAU", true, ExConvert.Unit.TWINKY);
    }

    @Bean(name = "usdcServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService usdcService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48");
        return new EthTokenServiceImpl(
                tokensList,
                "USDC",
                "USDC", false, ExConvert.Unit.MWEI);
    }

    @Bean(name = "ttpServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService ttpService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x38f22479795a1a51ccd1e5a41f09c7525fb27318");
        return new EthTokenServiceImpl(
                tokensList,
                "TTP",
                "TTP", false, ExConvert.Unit.FINNEY);
    }

    @Bean(name = "mgxServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService mgxService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xc79d440551a03f84f863b1f259f135794c8a7190");
        return new EthTokenServiceImpl(
                tokensList,
                "MGX",
                "MGX", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "vaiServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService vaiService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xd4078bdb652610ad5383a747d130cbe905911102");
        return new EthTokenServiceImpl(
                tokensList,
                "VAI",
                "VAI", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "uncServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService uncService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x882fbbe226f293037fa5c06459b1f4e871b70e94");
        return new EthTokenServiceImpl(
                tokensList,
                "UNC",
                "UNC", false, ExConvert.Unit.ETHER);
    }

    @Bean(name = "modlServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService modlService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x2dc059881eef90b12e4d770364f4b14af82c5b9c");
        return new EthTokenServiceImpl(
                tokensList,
                "MODL",
                "MODL", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "ecteServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService ecteService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xe9fa21e671bcfb04e6868784b89c19d5aa2424ea");
        return new EthTokenServiceImpl(
                tokensList,
                "ECTE",
                "ECTE", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "s4fServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService s4fService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xaec7d1069e3a914a3eb50f0bfb1796751f2ce48a");
        return new EthTokenServiceImpl(
                tokensList,
                "S4F",
                "S4F", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "mncServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService mncService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x9f0f1be08591ab7d990faf910b38ed5d60e4d5bf");
        return new EthTokenServiceImpl(
                tokensList,
                "MNC",
                "MNC", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "tcatServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService tcatService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xaff84e86d72edb971341a6a66eb2da209446fa14");
        return new EthTokenServiceImpl(
                tokensList,
                "TCAT",
                "TCAT", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "htServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService htService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x6f259637dcd74c767781e37bc6133cd6a68aa161");
        return new EthTokenServiceImpl(
                tokensList,
                "HT",
                "HT", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "edtServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService edtService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x3766a0d0c661094c02d5f11c74f2aa92228b1548");
        return new EthTokenServiceImpl(
                tokensList,
                "EDT",
                "EDT", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "poaServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService poaService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x6758b7d441a9739b98552b373703d8d3d14f9e62");
        return new EthTokenServiceImpl(
                tokensList,
                "POA",
                "POA", false, ExConvert.Unit.ETHER);
    }

//    @Bean(name = "zilServiceImpl")
//    @Conditional(MonolitConditional.class)
//    public EthTokenService zilService() {
//        List<String> tokensList = new ArrayList<>();
//        tokensList.add("0x05f4a42e251f2d52b8ed15e9fedaacfcef1fad27");
//        return new EthTokenServiceImpl(
//                tokensList,
//                "ZIL",
//                "ZIL", true, ExConvert.Unit.SZABO);
//    }

    @Bean(name = "mcoServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService mcoService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xb63b606ac810a52cca15e44bb630fd42d8d1d83d");
        return new EthTokenServiceImpl(
                tokensList,
                "MCO",
                "MCO", true, ExConvert.Unit.AIWEI);
    }

    @Bean(name = "manaServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService manaService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x0f5d2fb29fb7d3cfee444a200298f468908cc942");
        return new EthTokenServiceImpl(
                tokensList,
                "MANA",
                "MANA", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "wabiServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService wabiService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x286bda1413a2df81731d4930ce2f862a35a609fe");
        return new EthTokenServiceImpl(
                tokensList,
                "WaBi",
                "WaBi", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "npxsServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService npxsServiceImpl() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xa15c7ebe1f07caf6bff097d8a589fb8ac49ae5b3");
        return new EthTokenServiceImpl(tokensList, "NPXS", "NPXS", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "qkcServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService qkcServiceImpl() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xea26c4ac16d4a5a106820bc8aee85fd0b7b2b664");
        return new EthTokenServiceImpl(tokensList, "QKC", "QKC", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "hotServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService hotServiceImpl() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x6c6ee5e31d828de241282b9606c8e98ea48526e2");
        return new EthTokenServiceImpl(tokensList, "HOT", "HOT", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "zrxServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService zrxServiceImpl() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xe41d2489571d322189246dafa5ebde1f4699f498");
        return new EthTokenServiceImpl(tokensList, "ZRX", "ZRX", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "batServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService batServiceImpl() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x0d8775f648430679a709e98d2b0cb6250d2887ef");
        return new EthTokenServiceImpl(tokensList, "BAT", "BAT", false, ExConvert.Unit.ETHER);
    }

    @Bean(name = "rdnServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService rdnServiceImpl() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x255aa6df07540cb5d3d297f0d0d4d84cb52bc8e6");
        return new EthTokenServiceImpl(tokensList, "RDN", "RDN", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "hniServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService hniServiceImpl() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xd6cb175719365a2ea630f266c53ddfbe4e468e25");
        return new EthTokenServiceImpl(tokensList, "HNI", "HNI", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "eltServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService eltServiceImpl() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x45d0bdfdfbfd62e14b64b0ea67dc6eac75f95d4d");
        return new EthTokenServiceImpl(tokensList, "ELT", "ELT", false, ExConvert.Unit.AIWEI);
    }

    @Bean(name = "renServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService renServiceImpl() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x408e41876cccdc0f92210600ef50372656052a38");
        return new EthTokenServiceImpl(tokensList, "REN", "REN", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "metServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService metServiceImpl() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xa3d58c4e56fedcae3a7c43a725aee9a71f0ece4e");
        return new EthTokenServiceImpl(tokensList, "MET", "MET", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "pltcServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService pltcServiceImpl() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x0c6e8a8358cbde54f8e4cd7f07d5ac38aec8c5a4");
        return new EthTokenServiceImpl(tokensList, "PLTC", "PLTC", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "vrbsServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService vrbsServiceImpl() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x0e08b02d89ca66cf157c6658c02933ef0bc38cb6");
        return new EthTokenServiceImpl(tokensList, "VRBS", "VRBS", false, ExConvert.Unit.ETHER);
    }

    @Bean(name = "zubeServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService zubeServiceImpl() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xc5e017450346e4f9a2e477519d65affcfc90586a");
        return new EthTokenServiceImpl(tokensList, "ZUBE", "ZUBE", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "elcServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService elcServiceImpl() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x2ab2ffaa942851922a50fd640893f5c42b82474e");
        return new EthTokenServiceImpl(tokensList, "ELC", "ELC", false, ExConvert.Unit.AIWEI);
    }

    @Bean(name = "tttServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService tttServiceImpl() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x317572aabc73d59fc55f923750d1c51680fd28b4");
        return new EthTokenServiceImpl(tokensList, "TTT", "TTT", false, ExConvert.Unit.AIWEI);
    }

    @Bean(name = "rebServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService rebServiceImpl() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x61383ac89988b498df5363050ff07fe5c52ecdda");
        return new EthTokenServiceImpl(tokensList, "REB", "REB", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "rvcServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService rvcServiceImpl() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xa3ebd756729904ba2a39289751d96d9b2eac793b");
        return new EthTokenServiceImpl(tokensList, "RVC", "RVC", false, ExConvert.Unit.ETHER);
    }

    @Bean(name = "bioServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService bioServiceImpl() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xf18432ef894ef4b2a5726f933718f5a8cf9ff831");
        return new EthTokenServiceImpl(tokensList, "BIO", "BIO", false, ExConvert.Unit.AIWEI);
    }

    @Bean(name = "vraServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService vraServiceImpl() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xdf1d6405df92d981a2fb3ce68f6a03bac6c0e41f");
        return new EthTokenServiceImpl(tokensList, "VRA", "VRA", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "katServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService katServiceImpl() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xa858bc1b71a895ee83b92f149616f9b3f6afa0fb");
        return new EthTokenServiceImpl(tokensList, "KAT", "KAT", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "etaServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService etaServiceImpl() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x9195e00402abe385f2d00a32af40b271f2e87925");
        return new EthTokenServiceImpl(tokensList, "ETA", "ETA", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "brcServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService brcServiceImpl() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x21ab6c9fac80c59d401b37cb43f81ea9dde7fe34");
        return new EthTokenServiceImpl(tokensList, "BRC", "BRC", true, ExConvert.Unit.AIWEI);
    }

    @Bean(name = "gnyServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService gnyServiceImpl() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x247551f2eb3362e222c742e9c788b8957d9bc87e");
        return new EthTokenServiceImpl(tokensList, "GNY", "GNY", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "novaServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService novaServiceImpl() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x72fbc0fc1446f5accc1b083f0852a7ef70a8ec9f");
        return new EthTokenServiceImpl(tokensList, "NOVA", "NOVA", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "fstServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService fstServiceImpl() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x310c93dfc1c5e34cdf51678103f63c41762089cd");
        return new EthTokenServiceImpl(tokensList, "FST", "FST", true, ExConvert.Unit.MWEI);
    }

    @Bean(name = "rvtServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService rvtServiceImpl() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x4eef32781db07a9b7d9d36bb9ba81fa08af9d3ab");
        return new EthTokenServiceImpl(tokensList, "RVT", "RVT", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "linaServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService linaServiceImpl() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xc05d14442a510de4d3d71a3d316585aa0ce32b50");
        return new EthTokenServiceImpl(tokensList, "LINA", "LINA", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "gapiServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService gapiServiceImpl() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x1ac8bd74065e11c07c0fa3687c0dcfb86af76d46");
        return new EthTokenServiceImpl(tokensList, "GAPI", "GAPI", false, ExConvert.Unit.ETHER);
    }

    @Bean(name = "embrServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService embrServiceImpl() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x07f74c8480ccfee0d4f803e9bdde8383748b40de");
        return new EthTokenServiceImpl(tokensList, "EMBR", "EMBR", true, ExConvert.Unit.AIWEI);
    }

    @Bean(name = "usdtServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService usdtServiceImpl() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xdac17f958d2ee523a2206206994597c13d831ec7");
        return new EthTokenServiceImpl(tokensList, "USDT", "USDT", true, ExConvert.Unit.MWEI);
    }

    @Bean(name = "asgServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService asgServiceImpl() {
        List<String> tokensList = ImmutableList.of("0x7a3d3c4f30c46f51b814bee23d970a7c9b757a32");
        return new EthTokenServiceImpl(tokensList, "ASG", "ASG", false, ExConvert.Unit.ETHER);
    }

    @Bean(name = "vinciServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService vinciServiceImpl() {
        List<String> tokensList = ImmutableList.of("0x3db99ab08006aefcc9600972eca8c202396b4300");
        return new EthTokenServiceImpl(tokensList, "VINCI", "VINCI", false, ExConvert.Unit.ETHER);
    }

    @Bean(name = "acuServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService acuServiceImpl() {
        List<String> tokensList = ImmutableList.of("0xd536bbd5414a8c2beed82a63737b9327d2fa35a6");
        return new EthTokenServiceImpl(tokensList, "ACU", "ACU", false, ExConvert.Unit.ETHER);
    }

    @Bean(name = "mexcServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService mexcServiceImpl() {
        List<String> tokensList = ImmutableList.of("0x7de2d123042994737105802d2abd0a10a7bde276");
        return new EthTokenServiceImpl(tokensList, "MEXC", "MEXC", false, ExConvert.Unit.ETHER);
    }

    @Bean(name = "czoServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService czoServiceImpl() {
        List<String> tokensList = ImmutableList.of("0x029e7fe6fe4674af2f82c46f99b7a5cffe8041d4");
        return new EthTokenServiceImpl(tokensList, "CZO", "CZO", false, ExConvert.Unit.ETHER);
    }

    @Bean(name = "birdServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService birdServiceImpl() {
        List<String> tokensList = ImmutableList.of("0x026e62dded1a6ad07d93d39f96b9eabd59665e0d");
        return new EthTokenServiceImpl(tokensList, "BIRD", "BIRD", false, ExConvert.Unit.ETHER);
    }

    @Bean(name = "usdaServiceImpl")
    @Conditional(MonolitConditional.class)
    public EthTokenService usdaServiceImpl() {
        List<String> tokensList = ImmutableList.of("0x3c7b464376db7c9927930cf50eefdea2eff3a66a");
        return new EthTokenServiceImpl(tokensList, "USDA", "USDA", false, ExConvert.Unit.AIWEI);
    }

    @Bean
    @Conditional(MonolitConditional.class)
    public BinanceCurrencyService binanceCurrencyService() {
        return new BinanceCurrencyServiceImpl("merchants/binance.properties");
    }

    @Bean(name = "binanceServiceImpl")
    @Conditional(MonolitConditional.class)
    public BinanceService binanceService() {
        return new BinanceServiceImpl("merchants/binance.properties", "BinanceBlockchain", 40);
    }

    @Bean(name = "bnbServiceImpl")
    @Conditional(MonolitConditional.class)
    public BinTokenService bnbService() {
        return new BinTokenServiceImpl("merchants/binance.properties", "BinanceCoin", "BNB");
    }

    @Bean(name = "arnServiceImpl")
    @Conditional(MonolitConditional.class)
    public BinTokenService arnService() {
        return new BinTokenServiceImpl("merchants/binance.properties", "ARN", "ARN");
    }

    //    Qtum tokens:
    @Bean(name = "spcServiceImpl")
    @Conditional(MonolitConditional.class)
    public QtumTokenService spcService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("57931faffdec114056a49adfcaa1caac159a1a25");

        return new QtumTokenServiceImpl(tokensList, "SPC", "SPC", ExConvert.Unit.AIWEI);
    }

    @Bean(name = "hlcServiceImpl")
    @Conditional(MonolitConditional.class)
    public QtumTokenService hlcService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("b27d7bf95b03e02b55d5eb63d3f1692762101bf9");

        return new QtumTokenServiceImpl(tokensList, "HLC", "HLC", ExConvert.Unit.GWEI);
    }

    //**** Monero ****/
    @Bean(name = "moneroServiceImpl")
    @Conditional(MonolitConditional.class)
    public MoneroService moneroService() {
        return new MoneroServiceImpl("merchants/monero.properties",
                "Monero", "XMR", 15, 12);
    }

    @Bean(name = "ditcoinServiceImpl")
    @Conditional(MonolitConditional.class)
    public MoneroService ditcoinService() {
        return new MoneroServiceImpl("merchants/ditcoin.properties",
                "DIT", "DIT", 20, 8);
    }

    @Bean(name = "sumoServiceImpl")
    @Conditional(MonolitConditional.class)
    public MoneroService sumoService() {
        return new MoneroServiceImpl("merchants/sumokoin.properties",
                "SUMO", "SUMO", 20, 9);
    }

    @Bean(name = "hcxpServiceImpl")
    @Conditional(MonolitConditional.class)
    public MoneroService hcxpService() {
        return new HCXPServiceImpl("merchants/hcxp.properties",
                "HCXP", "HCXP", 20, 6);
    }

    /***tokens based on xem mosaic)****/
    @Bean(name = "dimCoinServiceImpl")
    @Conditional(MonolitConditional.class)
    public XemMosaicService dimCoinService() {
        return new XemMosaicServiceImpl(
                "DimCoin",
                "DIM",
                new MosaicIdDto("dim", "coin"),
                1000000,
                6,
                new Supply(9000000000L),
                10);
    }

    @Bean(name = "npxsDimServiceImpl")
    @Conditional(MonolitConditional.class)
    public XemMosaicService npxsService() {
        return new XemMosaicServiceImpl(
                "NPXSXEM",
                "NPXSXEM",
                new MosaicIdDto("pundix", "npxs"),
                1000000,
                6,
                new Supply(7170733206L),
                0);
    }

    @Bean(name = "dimEurServiceImpl")
    @Conditional(MonolitConditional.class)
    public XemMosaicService dimEurService() {
        return new XemMosaicServiceImpl(
                "DIM.EUR",
                "DIM.EUR",
                new MosaicIdDto("dim", "eur"),
                100,
                2,
                new Supply(81000000000L),
                10);
    }

    @Bean(name = "dimUsdServiceImpl")
    @Conditional(MonolitConditional.class)
    public XemMosaicService dimUsdService() {
        return new XemMosaicServiceImpl(
                "DIM.USD",
                "DIM.USD",
                new MosaicIdDto("dim", "usd"),
                100,
                2,
                new Supply(81000000000L),
                10);
    }

    @Bean(name = "digicServiceImpl")
    @Conditional(MonolitConditional.class)
    public XemMosaicService digicService() {
        return new XemMosaicServiceImpl(
                "DIGIT",
                "DIGIT",
                new MosaicIdDto("digit", "coin"),
                1000000,
                6,
                new Supply(8999999999L),
                10);
    }

    @Bean(name = "rwdsServiceImpl")
    @Conditional(MonolitConditional.class)
    public XemMosaicService rwdsService() {
        return new XemMosaicServiceImpl(
                "RWDS",
                "RWDS",
                new MosaicIdDto("rewards4earth", "erth"),
                1000000,
                6,
                new Supply(100000000L),
                0);
    }

    @Bean(name = "darcServiceImpl")
    @Conditional(MonolitConditional.class)
    public XemMosaicService darcService() {
        return new XemMosaicServiceImpl(
                "DARC",
                "DARC",
                new MosaicIdDto("darc_totalsupply", "darc"),
                1000000,
                6,
                new Supply(1000000000L),
                0);
    }

    @Bean(name = "sltStellarService")
    @Conditional(MonolitConditional.class)
    public StellarAsset sltStellarService() {
        return new StellarAsset("SLT",
                "SLT",
                "SLT",
                SLT_EMMITER);
    }

    @Bean(name = "ternStellarService")
    @Conditional(MonolitConditional.class)
    public StellarAsset ternStellarService() {
        return new StellarAsset("TERN",
                "TERN",
                "TERN",
                "GDGQDVO6XPFSY4NMX75A7AOVYCF5JYGW2SHCJJNWCQWIDGOZB53DGP6C");
    }

    @Bean("vexaniumContract")
    @Conditional(MonolitConditional.class)
    public AchainContract achainContractService() {
        return new AchainContract("ACT9XnhX5FtQqGFAa3KgrgkPCCEDPmuzgtSx", "VEX", "VEX", "Vexanium_Token");
    }

    @Bean(name = "vntStellarService")
    @Conditional(MonolitConditional.class)
    public StellarAsset vntStellarService() {
        return new StellarAsset("VNT",
                "VNT",
                "VNT",
                "GC2YBPMNHBHW7R7D2MFRH5RDLC6FGJDCBH7FRSNCHC5326ALOYWGMXLO");
    }

    @Bean
    @Primary
    @Conditional(MonolitConditional.class)
    public RestTemplate restTemplate() {
        HttpClientBuilder b = HttpClientBuilder.create();
        HttpClient client = b.build();
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new RestResponseErrorHandler());
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(client);
        requestFactory.setConnectionRequestTimeout(25000);
        requestFactory.setReadTimeout(25000);
        restTemplate.setRequestFactory(requestFactory);

        return restTemplate;
    }

    @Bean("qiwiRestTemplate")
    @Conditional(MonolitConditional.class)
    public RestTemplate qiwiRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(RequestUtil.getSSlFactory());
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(qiwiClientId, qiwiClientSecret));
        return restTemplate;
    }

    @Bean("inoutRestTemplate")
    @Conditional(MicroserviceConditional.class)
    public RestTemplate inoutRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(new JsonMimeInterceptor()));

        return restTemplate;
    }

    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext) {
        QuartzJobFactory jobFactory = new QuartzJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public SchedulerFactoryBean botSchedulerFactoryBean(ApplicationContext applicationContext) {
        return createSchedulerFactory(applicationContext, "botScheduler");
    }

    @Bean
    public SchedulerFactoryBean reportSchedulerFactoryBean(ApplicationContext applicationContext) {
        return createSchedulerFactory(applicationContext, "reportScheduler");
    }

    private SchedulerFactoryBean createSchedulerFactory(ApplicationContext applicationContext, String schedulerName) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setOverwriteExistingJobs(true);
        factory.setJobFactory(jobFactory(applicationContext));
        factory.setSchedulerName(schedulerName);
        return factory;
    }

    @Bean
    public Scheduler botOrderCreationScheduler(ApplicationContext applicationContext) {
        return botSchedulerFactoryBean(applicationContext).getScheduler();
    }

    @Bean
    public Scheduler reportScheduler(ApplicationContext applicationContext) {
        return reportSchedulerFactoryBean(applicationContext).getScheduler();
    }


    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public ZMQ.Context zmqContext() {
        return ZMQ.context(1);
    }


    @Bean
    public Twitter twitter() {
        return new TwitterTemplate(
                twitterConsumerKey,
                twitterConsumerSecret,
                twitterAccessToken,
                twitterAccessTokenSecret);
    }


    @Bean
    public Client client() {
        Client build = ClientBuilder.newBuilder().build();
        build.register(new LoggingFilter());
        return build;
    }
}
