/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.stcurr.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mheimer
 */
public class LXCCommandCallable extends CommandCallable {

    public LXCCommandCallable(List<String> cmdParts, File workingDir) {
        super(cmdParts, workingDir);
    }
    
    //lxc-execute -n test -f /u01/chroot/java-lxc.conf -s lxc.mount.entry='/tmp/test1234 /u01/chroot/template/home/java none rw,bind 0 0' -- su java --command='ls /home/java'
    
    @Override
    public CommandResult call() {
        List<String> lxCmdParts = new ArrayList<>();
        lxCmdParts.add("sudo");
        lxCmdParts.add("lxc-execute");
        lxCmdParts.add("-n");
        lxCmdParts.add(Thread.currentThread().getName());
        lxCmdParts.add("-f");
        lxCmdParts.add("/u01/chroot/java-lxc.conf");
        lxCmdParts.add("-s");
        lxCmdParts.add("lxc.mount.entry=" + workingDir.getAbsolutePath() + " /u01/chroot/template/home/java none rw,bind 0 0");
        lxCmdParts.add("--");
        lxCmdParts.add("su");
        lxCmdParts.add("-");
        lxCmdParts.add("java");
        StringBuilder origCmd = new StringBuilder();
        for(String part: cmdParts) {
            origCmd.append(part);
            origCmd.append(" ");
        }
        //TODO check for funky stuff once we all cmd args
        //for now the user has no influence on cmd except which classname gets selected
        lxCmdParts.add("--command=" + origCmd.toString().trim());
        cmdParts = lxCmdParts;
        return super.call();
    }
}
