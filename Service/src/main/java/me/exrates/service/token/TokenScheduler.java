package me.exrates.service.token;

import me.exrates.model.TemporalToken;
import me.exrates.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Valk on 08.04.16.
 */

@Component
public class TokenScheduler {

    private static final Logger LOGGER = LogManager.getLogger(TokenScheduler.class);

    public static final String TRIGGER_GROUP = "token";
    public static final Integer TOKEN_LIFE_TIME_DAYS = 1;

    private static TokenScheduler instance = new TokenScheduler();

    private Scheduler scheduler = null;

    public static TokenScheduler getInstance() {
        return instance;
    }

    private TokenScheduler() {
        try {
            scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            LOGGER.error("error while TokenScheduler init " + e.getLocalizedMessage());
        }
    }

    @Autowired
    protected UserService userService;

    private List<TemporalToken> tokens = new ArrayList<>();

    /**
     * collects all tokens and schedules job for it deleting when it expires
     */
    public void initTrigers() {
        List<JobKey> jobsInQueue = getClearTokenJobKeys();
        Integer jobsInQueueCount = jobsInQueue == null ? 0 : jobsInQueue.size();
        tokens = userService.getAllTokens();
        try {
            for (TemporalToken token : tokens) {
                Trigger trigger = scheduler.getTrigger(new TriggerKey(String.valueOf(token.getId()), TRIGGER_GROUP));
                if (trigger == null) {
                    Date startDate = Date.from(token.getDateCreation()
                            .plusDays(TOKEN_LIFE_TIME_DAYS).atZone(ZoneId.systemDefault()).toInstant());
                    trigger = TriggerBuilder.newTrigger()
                            .withIdentity(String.valueOf(token.getId()), TRIGGER_GROUP)
                            .startAt(startDate)
                            .withSchedule(
                                    SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow()
                            )
                            .build();
                    JobDetail job = JobBuilder.newJob(ClearTokenJob.class)
                            .withIdentity(String.valueOf(token.getId() + ":" + token.getUserId() + "::" + token.getTokenType().getTokenType()), TRIGGER_GROUP)
                            .usingJobData("tokenId", token.getId())
                            .usingJobData("tokenValue", token.getValue())
                            .usingJobData("tokenUser", token.getUserId())
                            .usingJobData("tokenDateCreation", token.getDateCreation().toString())
                            .build();

                    scheduler.scheduleJob(job, trigger);
                }
            }
            LOGGER.debug(String.format("expired token scheduler: queued %s job(s)" + "\n" + "  in queue now %s jobs",
                    (scheduler.getJobKeys(GroupMatcher.jobGroupEquals(TRIGGER_GROUP)).size() - jobsInQueueCount),
                    scheduler.getJobKeys(GroupMatcher.jobGroupEquals(TRIGGER_GROUP)).size()));
        } catch (SchedulerException e) {
            LOGGER.error("error while token clean triggers init " + e.getLocalizedMessage());
        }
    }

    public List<JobKey> getClearTokenJobKeys() {
        List<JobKey> jobs = null;
        try {
            jobs = scheduler.getJobKeys(GroupMatcher.groupEquals(TRIGGER_GROUP))
                    .stream()
                    .collect(Collectors.toList());
        } catch (SchedulerException e) {
            LOGGER.error("error while token jobs list retrieving: " + e.getLocalizedMessage());
            jobs = null;
        }
        return jobs;
    }

    /**
     * deletes all jobs related with token through userId and token type
     */
    public List<JobKey> deleteJobsRelatedWithToken(TemporalToken token) {
        List<JobKey> jobs = null;
        try {
            jobs = scheduler.getJobKeys(GroupMatcher.groupEquals(TRIGGER_GROUP))
                    .stream()
                    .filter(e -> e.toString().contains(token.getUserId() + "::" + token.getTokenType().getTokenType()))
                    .collect(Collectors.toList());
            if (scheduler.deleteJobs(jobs)) {
                LOGGER.debug(String.format("removed %s jobs in the token tracking scheduler : %s" + "\n" + "  in queue now %s jobs remain",
                        jobs.size(), jobs, scheduler.getJobKeys(GroupMatcher.jobGroupEquals(TRIGGER_GROUP)).size()));
            }
        } catch (SchedulerException e) {
            LOGGER.error("error while token jobs deleting: " + e.getLocalizedMessage());
            jobs = null;
        }
        return jobs;
    }
}
