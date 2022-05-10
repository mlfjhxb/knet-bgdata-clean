package cn.knet.seal.services;

import cn.knet.domain.util.RestTemplateUtils;
import cn.knet.domain.util.UUIDGenerator;
import cn.knet.seal.entity.SlBigdataSpiderSource;
import cn.knet.seal.entity.SlRegQccTrademark;
import cn.knet.seal.mapper.SlBigdataSpiderSourceMapper;
import cn.knet.seal.mapper.SlRegQccTrademarkMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

@Slf4j
public class TrademarkSpiderCallable implements Runnable {

    private SlRegQccTrademarkMapper slRegQccTrademarkMapper;

    private SlBigdataSpiderSourceMapper slBigdataSpiderSourceMapper;

    private Page searchPage;

    private LongAdder sus_longAdder = new LongAdder();
    private LongAdder db_longAdder = new LongAdder();
    private LongAdder error_longAdder = new LongAdder();

    public TrademarkSpiderCallable(LongAdder db_longAdder, LongAdder sus_longAdder, LongAdder error_longAdder, SlBigdataSpiderSourceMapper slBigdataSpiderSourceMapper, SlRegQccTrademarkMapper slRegQccTrademarkMapper, Page searchPage) {
        this.slBigdataSpiderSourceMapper = slBigdataSpiderSourceMapper;
        this.slRegQccTrademarkMapper = slRegQccTrademarkMapper;
        this.searchPage = searchPage;
        this.db_longAdder = db_longAdder;
        this.sus_longAdder = sus_longAdder;
        this.error_longAdder = error_longAdder;
    }


    @Override
    public void run() {
        IPage iPage = slBigdataSpiderSourceMapper.selectDatas4TrademarkSpider(searchPage);
        int dbListSize = 0;
        List<SlBigdataSpiderSource> slBigdataSpiderSources = null;
        if (iPage != null) {
            slBigdataSpiderSources = iPage.getRecords();
            dbListSize = iPage.getRecords().size();
        }
        log.info(String.format("*********** thread_task:[%s] is starting..... page_no:[%s] records:[%s] ***********", Thread.currentThread().getName() + "_" + Thread.currentThread().getId(), searchPage.getCurrent(), dbListSize));
        if (slBigdataSpiderSources != null && slBigdataSpiderSources.size() > 0) {
            for (SlBigdataSpiderSource source : slBigdataSpiderSources) {
                String url="http://v.juhe.cn/trademark/marklist";
                try {
                    db_longAdder.increment();
                    /**
                     * 调用聚合数据接口
                     */
                    ArrayList<SlRegQccTrademark> slRegQccTrademarks = callJuheApi(url,source.getOrgName());
                    /**
                     * 商标数据入库
                     */
                    if (slRegQccTrademarks != null && slRegQccTrademarks.size() > 0) {
                        for (SlRegQccTrademark slRegQccTrademark : slRegQccTrademarks) {
                            slRegQccTrademarkMapper.insert(slRegQccTrademark);
                        }
                    } else {
                        log.error(String.format("******[v.juhe.cn_error_nodata] org_name=%s trademark_size=0", source.getOrgName()));
                    }
                    sus_longAdder.increment();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(String.format("******[v.juhe.cn_error] org_name=[%s]调用接口[%s]时，出现调用异常",source.getOrgName(),url));
                    error_longAdder.increment();
                }
            }
            log.info(String.format("*********** thread_task:[%s] is stopping..... total_size:[%s] success_size:[%s] error_size:[%s]***********", Thread.currentThread().getName() + "_" + Thread.currentThread().getId(), db_longAdder.intValue(), sus_longAdder.intValue(), error_longAdder.intValue()));
        }
    }


    /**
     * 调用聚合数据接口
     *
     * @param orgname 企业名称
     * @return
     * @throws Exception
     */
    private ArrayList<SlRegQccTrademark> callJuheApi(String url,String orgname) throws Exception {
        MultiValueMap<String, String> vars = new LinkedMultiValueMap<>();
        vars.add("key", "5647e4c665f9ad64c20c29bdc6698bea");
        vars.add("applicantCn", orgname);
        Map<String, Object> rst = RestTemplateUtils.doGet(url,vars);
        ArrayList<SlRegQccTrademark> slRegQccTrademarks = new ArrayList<>();
        if ("Success".equalsIgnoreCase((String) rst.get("reason"))) {
            Map<String, Object> result = (Map<String, Object>) rst.get("result");
            ArrayList<Map<String, Object>> dataList = (ArrayList<Map<String, Object>>) result.get("data");
            if (dataList != null && dataList.size() > 0) {
                for (Map<String, Object> data : dataList) {
                    SlRegQccTrademark slRegQccTrademark = new SlRegQccTrademark();
                    slRegQccTrademark.setId(UUIDGenerator.getUUID());
                    slRegQccTrademark.setAgent((String) data.get("agent"));
                    slRegQccTrademark.setAnnouncementDate((String) data.get("announcementDate"));
                    slRegQccTrademark.setAnnouncementIssue((String) data.get("announcementIssue"));
                    slRegQccTrademark.setAppDate((String) data.get("appDate"));
                    slRegQccTrademark.setApplicantCn((String) data.get("applicantCn"));
                    slRegQccTrademark.setCurrentStatus((String) data.get("currentStatus"));
                    slRegQccTrademark.setIntCls((String) data.get("intCls"));
                    slRegQccTrademark.setRegDate((String) data.get("regDate"));
                    slRegQccTrademark.setRegIssue((String) data.get("regIssue"));
                    slRegQccTrademark.setRegNo((String) data.get("regNo"));
                    slRegQccTrademark.setTmImg((String) data.get("tmImg"));
                    slRegQccTrademark.setTmName((String) data.get("tmName"));
                    slRegQccTrademarks.add(slRegQccTrademark);
                }
            }
        }
        return slRegQccTrademarks;
    }
}
