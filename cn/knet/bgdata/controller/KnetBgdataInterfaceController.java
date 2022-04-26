package cn.knet.bgdata.controller;

import cn.knet.bgdata.dto.MsgData;
import cn.knet.bgdata.enums.MsgCodeEnum;
import cn.knet.bgdata.service.KnetBgdataInterfaceService;
import cn.knet.bgdata.utils.BusinessException;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
public class KnetBgdataInterfaceController {

    @Autowired
    private KnetBgdataInterfaceService knetBgdataInterfaceService;

    /**
     * 跳转到index页面
     */
    @RequestMapping(value = {"/index", "/"}, method = {RequestMethod.GET, RequestMethod.HEAD})
    public String index(HttpServletRequest request, Model model) {
        return "index";
    }

    @RequestMapping(value = "/search", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String search(String searchKey, @RequestParam(value = "searchScope[]", required = false) String[] searchScope, String areas,
                         @RequestParam(value = "foundDates[]", required = false) String[] foundDates, @RequestParam(value = "capitals[]", required = false) String[] capitals,
                         @RequestParam(value = "corpTypes[]", required = false) String[] corpTypes, @RequestParam(value = "domains[]", required = false) String[] domains, int page, int pageSize) {

        String rstMsg = null;
        try {
            rstMsg = knetBgdataInterfaceService.startCHSearch(searchKey, searchScope, areas, foundDates, capitals, corpTypes, domains, page, pageSize);
        } catch (BusinessException e) {
            e.printStackTrace();
            JSONObject jsonObject = (JSONObject) JSONObject.toJSON(new MsgData(MsgCodeEnum.error.getValue(), e.getMessage()));
            return jsonObject.toString();
        }

        return rstMsg;
    }
    @RequestMapping(value = "/searchT", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String searchSimple(String searchKey, String scope, String areas,
                         String s_foundDates, String s_capitals,
                         String s_corpTypes, String  s_domains, int page, int pageSize) {

        String rstMsg = null;
        try {
            String [] searchScope=new String []{};
            String [] foundDates=new String []{};
            String [] capitals=new String []{};
            String [] corpTypes=new String []{};
            String [] domains=new String []{};
            if(StringUtils.isNotBlank(scope)){
                searchScope=scope.split(",");
            }
            if(StringUtils.isNotBlank(s_foundDates)){
                foundDates=s_foundDates.split(",");
            }
            if(StringUtils.isNotBlank(s_capitals)){
                capitals=s_capitals.split(",");
            }
            if(StringUtils.isNotBlank(s_corpTypes)){
                corpTypes=s_corpTypes.split(",");
            }
            if(StringUtils.isNotBlank(s_domains)){
                domains=s_domains.split(",");
            }
            rstMsg = knetBgdataInterfaceService.startCHSearchT(searchKey, searchScope, areas, foundDates, capitals, corpTypes, domains, page, pageSize);
        } catch (BusinessException e) {
            e.printStackTrace();
            JSONObject jsonObject = (JSONObject) JSONObject.toJSON(new MsgData(MsgCodeEnum.error.getValue(), e.getMessage()));
            return jsonObject.toString();
        }

        return rstMsg;
    }


}
