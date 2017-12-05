package me.exrates.security.service;


public interface IpBlockingService {
    void checkIp(String ip);

    void processLoginFailure(String ipAddress);

    void processLoginSuccess(String ipAddress);
}
