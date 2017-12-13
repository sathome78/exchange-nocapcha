package me.exrates.dao;

import java.util.List;

public interface ReportDao {
    String retrieveReportMailingTime();

    void updateReportMailingTime(String newMailTime);

    boolean isReportMailingEnabled();

    void updateReportMailingEnableStatus(boolean newStatus);

    List<String> retrieveReportSubscribersList();

    void addReportSubscriber(String email);

    void deleteReportSubscriber(String email);
}
