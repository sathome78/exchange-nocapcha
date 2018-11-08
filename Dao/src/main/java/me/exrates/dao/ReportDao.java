package me.exrates.dao;

import me.exrates.model.dto.BalancesReportDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportDao {
    String retrieveReportMailingTime();

    void updateReportMailingTime(String newMailTime);

    boolean isReportMailingEnabled();

    void updateReportMailingEnableStatus(boolean newStatus);

    List<String> retrieveReportSubscribersList(boolean selectWithPremissions);

    void addReportSubscriber(String email);

    void deleteReportSubscriber(String email);

    void addNewBalancesReportObject(byte[] balancesBytes, String fileName);

    List<BalancesReportDto> getBalancesReportsNames(LocalDateTime fromDate, LocalDateTime toDate);

    BalancesReportDto getBalancesReportById(int id);

    BalancesReportDto getBalancesReportByTime(LocalDateTime fromTime, LocalDateTime toTime);
}
