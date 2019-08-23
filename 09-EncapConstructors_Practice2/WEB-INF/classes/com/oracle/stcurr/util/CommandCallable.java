/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.stcurr.util;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

/**
 *
 * @author mheimer
 */
public class CommandCallable implements Callable<CommandResult> {

    protected List<String> cmdParts;
    protected File workingDir;

    public CommandCallable(List<String> cmdParts, File workingDir) {
        this.cmdParts = cmdParts;
        this.workingDir = workingDir;
    }

    @Override
    public CommandResult call() {
        return CommandInvoker.execute(cmdParts, workingDir);
    }
}
