package com.oracle.stcurr.ide.web;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Web application lifecycle listener.
 *
 * @author mheimer
 */
@WebListener()
public class ConfigServletListener implements ServletContextListener {

    @Inject
    private ConfigBean configBean;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        String param = context.getInitParameter("jvm-options");
        configBean.setJvmOptions(param);
        param = context.getInitParameter("policy-file");
        configBean.setPolicyFilePath(param);
        param = context.getInitParameter("enable-lxc");
        configBean.setEnableLXC(Boolean.parseBoolean(param));
        param = context.getInitParameter("queue-size");
        try {
            configBean.setQueueSize(Integer.parseInt(param));
        } catch (NumberFormatException ex) {
            configBean.setQueueSize(1000);
        }
        param = context.getInitParameter("thread-count");
        try {
            configBean.setThreadCount(Integer.parseInt(param));
        } catch (NumberFormatException ex) {
            configBean.setThreadCount(100);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
