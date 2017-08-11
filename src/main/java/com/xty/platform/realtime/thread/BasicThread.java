package com.xty.platform.realtime.thread;


import com.xty.common.JsonOpera;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.SimpleDateFormat;

/**
 * Created by Shadow on 2017/6/1.
 */
public class BasicThread extends Thread {
    protected final Log logger = LogFactory.getLog(getClass());
    public JsonOpera jo = new JsonOpera(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    @Override
    public void run() {
        super.run();
    }
}
