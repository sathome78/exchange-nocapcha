package me.exrates.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by Valk on 07.05.2016.
 */
public class CandleChartItemDto {
    private LocalDateTime beginPeriod;
    private LocalDateTime endPeriod;
    private BigDecimal openRate;
    private BigDecimal closeRate;
    private BigDecimal lowRate;
    private BigDecimal highRate;
    private BigDecimal baseVolume;

    /*getters setters*/

    public LocalDateTime getBeginPeriod() {
        return beginPeriod;
    }

    public void setBeginPeriod(LocalDateTime beginPeriod) {
        this.beginPeriod = beginPeriod;
    }

    public LocalDateTime getEndPeriod() {
        return endPeriod;
    }

    public void setEndPeriod(LocalDateTime endPeriod) {
        this.endPeriod = endPeriod;
    }

    public BigDecimal getOpenRate() {
        return openRate;
    }

    public void setOpenRate(BigDecimal openRate) {
        this.openRate = openRate;
    }

    public BigDecimal getCloseRate() {
        return closeRate;
    }

    public void setCloseRate(BigDecimal closeRate) {
        this.closeRate = closeRate;
    }

    public BigDecimal getLowRate() {
        return lowRate;
    }

    public void setLowRate(BigDecimal lowRate) {
        this.lowRate = lowRate;
    }

    public BigDecimal getHighRate() {
        return highRate;
    }

    public void setHighRate(BigDecimal highRate) {
        this.highRate = highRate;
    }

    public BigDecimal getBaseVolume() {
        return baseVolume;
    }

    public void setBaseVolume(BigDecimal baseVolume) {
        this.baseVolume = baseVolume;
    }
}
