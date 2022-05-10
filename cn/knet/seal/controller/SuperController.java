package cn.knet.seal.controller;

import cn.knet.seal.entity.KnetUser;
import cn.knet.seal.utils.StringPropertyEditor;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author dcx
 * @create 2019-11-25 16:36
 */
public class SuperController {
    /**
     * 消除提交参数空格
     *
     * @param binder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringPropertyEditor(true));
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(true);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    protected KnetUser getCurrentLoginUser(HttpServletRequest request) {
        return (KnetUser) SecurityUtils.getSubject().getSession().getAttribute("user");
    }
}
