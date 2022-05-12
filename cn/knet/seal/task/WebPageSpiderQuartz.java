package cn.knet.seal.task;

import cn.knet.seal.services.WebPageSpiderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class WebPageSpiderQuartz {

    @Resource
    WebPageSpiderService webPageSpiderService;

    @Scheduled(cron = "${task.seal.pageSpider}")
    public void app() {
        try {
            webPageSpiderService.startProbe();
        } catch (Exception e) {
            log.error("*****************WebPageSpiderQuartz occur Exception ,stopped already! *******************");
            e.printStackTrace();
        }
    }

}
