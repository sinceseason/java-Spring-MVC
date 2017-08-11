package com.xty.platform.controller;

import com.xty.common.JsonOpera;
import com.xty.jms.util.TopicSender;
import com.xty.platModel.basic.User;
import com.xty.platform.service.LogService;
import com.xty.platform.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by Shadow on 2017/6/13.
 */
public class BasicController {
    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected HttpSession session;
    @Autowired
    protected TopicSender topicSender;

    @Autowired
    protected LogService logService;
    @Autowired
    protected UserService userService;


    protected final Log logger = LogFactory.getLog(getClass());

    protected JsonOpera jo = new JsonOpera();

    protected String getToken() {
        return request.getHeader("token");
    }

    protected User getLoginedUser() {
        return (User)this.request.getSession().getAttribute(request.getHeader("token"));
    }
}
