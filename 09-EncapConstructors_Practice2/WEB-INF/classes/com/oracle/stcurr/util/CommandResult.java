/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.stcurr.util;

/**
 * Returned by the CommandInvoker execute methods as the result of execution.
 * All process output is made available with this class and can be read while
 * the process is still executing. Standard output and error output are merged
 * into a single buffer.
 *
 * @author mheimer
 */
public class CommandResult {
    private StringBuffer outputBuffer = new StringBuffer();
    private boolean finished = false;
    private int returnCode = 0;

    public StringBuffer getOutputBuffer() {
        return outputBuffer;
    }

    public synchronized boolean isFinished() {
        return finished;
    }

    public synchronized void setFinished(boolean finished) {
        this.finished = finished;
    }

    public synchronized int getReturnCode() {
        return returnCode;
    }

    public synchronized void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }
}
