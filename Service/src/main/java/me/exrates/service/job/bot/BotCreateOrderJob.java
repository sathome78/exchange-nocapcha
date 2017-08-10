package me.exrates.service.job.bot;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.enums.OrderType;
import me.exrates.service.BotService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class BotCreateOrderJob implements Job {

    @Autowired
    private BotService botService;

    public BotCreateOrderJob() {
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Integer currencyPairId = jobExecutionContext.getMergedJobDataMap().getInt("currencyPairId");
        OrderType orderType = OrderType.valueOf(jobExecutionContext.getMergedJobDataMap().getString("orderType"));
        System.out.printf("This is currencyPairId %s, orderType %s\n", currencyPairId, orderType.name());
        System.out.printf("Bot service: - %s", botService + "\n");
        botService.runOrderCreation(currencyPairId, orderType);

    }


}
