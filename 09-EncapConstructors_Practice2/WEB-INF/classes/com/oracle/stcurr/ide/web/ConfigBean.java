/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.stcurr.ide.web;

import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author mheimer
 */
@ApplicationScoped
public class ConfigBean {
    
    private String jvmOptions;
    private boolean enableLXC;
    private String policyFilePath;
    private int queueSize;
    private int threadCount;
    
    public String getJvmOptions() {
        return jvmOptions;
    }

    public void setJvmOptions(String jvmOptions) {
        this.jvmOptions = jvmOptions;
    }
    
    public boolean isEnableLXC() {
        return enableLXC;
    }

    public void setEnableLXC(boolean enableLXC) {
        this.enableLXC = enableLXC;
    }

    public String getPolicyFilePath() {
        return policyFilePath;
    }

    public void setPolicyFilePath(String policyFilePath) {
        this.policyFilePath = policyFilePath;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

}
