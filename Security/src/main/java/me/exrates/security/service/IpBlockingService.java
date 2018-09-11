package me.exrates.security.service;


import me.exrates.security.ipsecurity.IpTypesOfChecking;

public interface IpBlockingService {

    void checkIp(String ip, IpTypesOfChecking typeOfChecking);

    void failureProcessing(String ipAddress, IpTypesOfChecking typeOfChecking);

    void successfulProcessing(String ipAddress, IpTypesOfChecking typeOfChecking);
}
