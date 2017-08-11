package com.xty.platform.realtime;

import com.xty.jms.util.TopicSender;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Shadow on 2017/7/28.
 */
public class Launcher {
    private Logger logger = Logger.getLogger(this.getClass());

    public void activeThread() {
        try {

        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getMessage());
        }
    }

    private TopicSender topicSender;

    public TopicSender getTopicSender() {
        return topicSender;
    }

    public void setTopicSender(TopicSender topicSender) {
        this.topicSender = topicSender;
    }

    @Autowired
    public Launcher(TopicSender topicSender) {
        this.setTopicSender(topicSender);

        activeThread();
    }
}
