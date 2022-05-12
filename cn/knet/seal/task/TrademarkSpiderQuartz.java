package cn.knet.seal.task;

import cn.knet.seal.services.TrademarkSpiderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class TrademarkSpiderQuartz {
    @Resource
    TrademarkSpiderService trademarkSpiderService;

    @Scheduled(cron = "${task.seal.tmSpider}")
    public void app() {
        try {
            trademarkSpiderService.startProbe();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("*****************TrademarkSpiderQuartz occur Exception ,stopped already! *******************");
        }
    }
}
