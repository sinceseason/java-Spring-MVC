package com.xty.platform.thread;


import com.xty.jms.util.TopicSender;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Date;

/**
 * Created by Shadow on 2017/5/26.
 */
public class ThreadListener implements ServletContextListener {
    protected AutowireCapableBeanFactory ctx;

    @Override
    public void contextInitialized (ServletContextEvent servletContextEvent) {
        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(servletContextEvent.getServletContext());
        TopicSender topicSender = (TopicSender)context.getBean("topicSender");

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
