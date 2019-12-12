package com.mq.ftp.jobs;

import com.mq.ftp.service.FTPClientService;
import com.mq.ftp.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.io.File;

/**
 * 定时读取ftp
 *
 * @author wenlinzou
 */
@Slf4j
@Component
public class Ftp2DbJobs {

    @Resource
    FTPClientService ftpClientService;

    @Scheduled(cron = "${task.ftp.cron}")
    public void executeInit() {
        StopWatch stopwatch = new StopWatch();
        stopwatch.start();
        int rows = 0;
        String yesterdayPath = DateUtils.getBeforeDay();
        try {
            rows = ftpClientService.readFtpFile(yesterdayPath);
        } catch (Exception e) {
            log.error("job error={}", e);
        } finally {
            long totalTime = stopwatch.getTotalTimeMillis();
            log.info("day={}同步数据完毕耗时{}ms, 处理数据共={}条", yesterdayPath, totalTime, rows);
        }
    }

}
