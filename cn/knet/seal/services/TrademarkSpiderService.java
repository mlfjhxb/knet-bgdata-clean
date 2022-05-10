package cn.knet.seal.services;

import cn.knet.seal.entity.SlGuanwangInfo;
import cn.knet.seal.mapper.SlBigdataSpiderSourceMapper;
import cn.knet.seal.mapper.SlRegQccTrademarkMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.LongAdder;

@Service
@Slf4j
public class TrademarkSpiderService {

    @Resource
    private SlBigdataSpiderSourceMapper slBigdataSpiderSourceMapper;
    @Resource
    private SlRegQccTrademarkMapper slRegQccTrademarkMapper;

    private static LongAdder db_longAdder = new LongAdder();
    private static LongAdder sus_longAdder = new LongAdder();
    private static LongAdder error_longAdder = new LongAdder();

    public void startProbe() throws InterruptedException {
        log.info("*****************TrademarkSpiderService is starting *******************");
        long start = System.currentTimeMillis();
        /**
         * 1.0 查询出待扫描的企业总数
         */
        int spiderCnt = slBigdataSpiderSourceMapper.selectDatasCnt4TrademarkSpider();
        /**
         * 2.0 分页查询出所有的待探测的域名,多线程进行探测
         */
        //2.0.1 计算总页数
        int pagSize = 1000;
        int total = spiderCnt;
        int times = total / pagSize;
        if (total % pagSize != 0) {
            times = times + 1;
        }
        //2.0.2  初始化线程池,多任务同时进行探测
        ExecutorService pool = Executors.newFixedThreadPool(10);
        try {
            for (int pageNo = 1; pageNo <= times; pageNo++) {
                //pool.submit(new WebPageSpiderCallable(slGuanwangProbeRstMapper, slGuanwangInfoMapper, new Page<SlGuanwangInfo>(pageNo, pagSize)));
                pool.execute(new TrademarkSpiderCallable(db_longAdder, sus_longAdder, error_longAdder, slBigdataSpiderSourceMapper, slRegQccTrademarkMapper, new Page<SlGuanwangInfo>(pageNo, pagSize)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pool != null) {
                pool.shutdown();
            }
        }
        /**
         * 3.0  统计汇总 如：总条数、总时间
         */
        while (true) {
            if (pool.isTerminated()) {
                long end = System.currentTimeMillis();
                log.info(String.format("*****************TrademarkSpiderService is stopping ! total time=[%s秒]  total_domains=[%s条] success_domains=[%s条] error_domains=[%s条]*******************", (end - start) / 1000, db_longAdder.sum(), sus_longAdder.sum(), error_longAdder.sum()));
                break;
            } else {
                Thread.sleep(3000);
            }
        }

    }
}
