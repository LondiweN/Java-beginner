package com.oracle.stcurr.util;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * This class simplifies the running of native process for Java applications.
 * Example usage:
 * <pre>
 * List<String> cmd = new ArrayList<String>();
 * cmd.add("/bin/cat");
 * cmd.add("hosts");
 * CommandInvoker.Result result = CommandInvoker.execute(cmd, "/etc");
 * </pre>
 *
 * @author <a href="mailto:matt.heimer@osdev.org">Matthieu Heimer</a>
 */
public class CommandInvoker {

    private static long defaultTimeout = 10000;

    public static long getDefaultTimeout() {
        return defaultTimeout;
    }

    public static void setDefaultTimeout(long defaultTimeout) {
        CommandInvoker.defaultTimeout = defaultTimeout;
    }

    public static CommandResult concurrentExecute(List<String> cmd, String workingDir) {
        return concurrentExecute(cmd.toArray(new String[0]), null, new File(workingDir), null);
    }

    public static CommandResult concurrentExecute(List<String> cmd, File workingDir) {
        return concurrentExecute(cmd.toArray(new String[0]), null, workingDir, null);
    }

    public static CommandResult concurrentExecute(String[] cmd, String workingDir) {
        return concurrentExecute(cmd, null, new File(workingDir), null);
    }

    public static CommandResult concurrentExecute(List<String> cmd, List<String> env, String workingDir) {
        return concurrentExecute(cmd.toArray(new String[0]), env.toArray(new String[0]), new File(workingDir), null);
    }

    public static CommandResult concurrentExecute(List<String> cmd, List<String> env, File workingDir) {
        return concurrentExecute(cmd.toArray(new String[0]), env.toArray(new String[0]), workingDir, null);
    }

    public static CommandResult concurrentExecute(String[] cmd, String[] env, String workingDir) {
        return concurrentExecute(cmd, env, new File(workingDir), null);
    }

    public static CommandResult concurrentExecute(String[] cmd, String[] env, File workingDir) {
        return _execute(false, cmd, env, workingDir, null);
    }

    public static CommandResult concurrentExecute(List<String> cmd, String workingDir, String stdInText) {
        return concurrentExecute(cmd.toArray(new String[0]), null, new File(workingDir), stdInText);
    }

    public static CommandResult concurrentExecute(List<String> cmd, File workingDir, String stdInText) {
        return concurrentExecute(cmd.toArray(new String[0]), null, workingDir, stdInText);
    }

    public static CommandResult concurrentExecute(String[] cmd, String workingDir, String stdInText) {
        return concurrentExecute(cmd, null, new File(workingDir), stdInText);
    }

    public static CommandResult concurrentExecute(List<String> cmd, List<String> env, String workingDir, String stdInText) {
        return concurrentExecute(cmd.toArray(new String[0]), env.toArray(new String[0]), new File(workingDir), stdInText);
    }

    public static CommandResult concurrentExecute(List<String> cmd, List<String> env, File workingDir, String stdInText) {
        return concurrentExecute(cmd.toArray(new String[0]), env.toArray(new String[0]), workingDir, stdInText);
    }

    public static CommandResult concurrentExecute(String[] cmd, String[] env, String workingDir, String stdInText) {
        return concurrentExecute(cmd, env, new File(workingDir), stdInText);
    }

    /**
     * Runs a native process that may not finish before the method returns. A
     * background thread is started to execute the process. The Result can be
     * used to determine if the process has finished. All the other
     * concurrentExecute methods are convenience methods and call this method.
     */
    public static CommandResult concurrentExecute(String[] cmd, String[] env, File workingDir, String stdInText) {
        return _execute(false, cmd, env, workingDir, stdInText);
    }

    private static CommandResult _execute(boolean waitFor, String[] cmd, String[] env, File workingDir, String stdInText) {
        //mcgee
        for (int i = 0; i < cmd.length; i++) {
            cmd[i] = cmd[i].trim();
        }

        System.out.println("CMD EXECUTE: " + Arrays.toString(cmd));
        
        ProcessBuilder pb = new ProcessBuilder(cmd);
        Map<String, String> envMap = pb.environment();
        if (env != null) {
            for (String envValue : env) {
                String[] sa = envValue.split("=");
                envMap.put(sa[0], sa[1]);
            }
        }
        pb.directory(workingDir);
        pb.redirectErrorStream(true);

        Process proc;
        try {
            proc = pb.start();
        } catch (IOException ioe) {
            String commandLine = "";
            for (int i = 0; i < cmd.length; i++) {
                if (commandLine.length() > 0) {
                    commandLine += " ";
                }
                commandLine += cmd[i];
            }
            throw new RuntimeException("Unable to execute command '" + commandLine + "'", ioe);
        }

        CommandResult result = new CommandResult();

        OutputStream stdIn = proc.getOutputStream();
        InputRelay inThread = new InputRelay(stdIn, stdInText);

        InputStream stdOut = proc.getInputStream();
        OutputConsumer outThread = new OutputConsumer(stdOut, result.getOutputBuffer());

        if (waitFor) {
            long startTime = System.currentTimeMillis();
            while (inThread.isAlive() || outThread.isAlive() || !result.isFinished()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                }
                if (System.currentTimeMillis() - startTime > defaultTimeout) {
                    proc.destroy();
                }
                try {
                    result.setReturnCode(proc.exitValue());
                    result.setFinished(true);
                } catch (IllegalThreadStateException e) {
                }
            }
        } else {
            new BackgroundRunner(proc, result, inThread, outThread);
        }
        //debug code to always add full command line to output
//        result.getOutputBuffer().append("\n");
//        String commandLine = "";
//        for (int i = 0; i < cmd.length; i++) {
//            if (commandLine.length() > 0) {
//                commandLine += " ";
//            }
//            commandLine += cmd[i];
//        }
//        result.getOutputBuffer().append(commandLine);
        return result;

    }

    public static CommandResult execute(List<String> cmd, String workingDir) {
        return execute(cmd.toArray(new String[0]), null, new File(workingDir), null);
    }

    public static CommandResult execute(List<String> cmd, File workingDir) {
        return execute(cmd.toArray(new String[0]), null, workingDir, null);
    }

    public static CommandResult execute(String[] cmd, String workingDir) {
        return execute(cmd, null, new File(workingDir), null);
    }

    public static CommandResult execute(List<String> cmd, List<String> env, String workingDir) {
        return execute(cmd.toArray(new String[0]), env.toArray(new String[0]), new File(workingDir), null);
    }

    public static CommandResult execute(List<String> cmd, List<String> env, File workingDir) {
        return execute(cmd.toArray(new String[0]), env.toArray(new String[0]), workingDir, null);
    }

    public static CommandResult execute(String[] cmd, String[] env, String workingDir) {
        return execute(cmd, env, new File(workingDir), null);
    }

    public static CommandResult execute(String[] cmd, String[] env, File workingDir) {
        return _execute(true, cmd, env, workingDir, null);
    }

    public static CommandResult execute(List<String> cmd, String workingDir, String stdInText) {
        return execute(cmd.toArray(new String[0]), null, new File(workingDir), stdInText);
    }

    public static CommandResult execute(List<String> cmd, File workingDir, String stdInText) {
        return execute(cmd.toArray(new String[0]), null, workingDir, stdInText);
    }

    public static CommandResult execute(String[] cmd, String workingDir, String stdInText) {
        return execute(cmd, null, new File(workingDir), stdInText);
    }

    public static CommandResult execute(List<String> cmd, List<String> env, String workingDir, String stdInText) {
        return execute(cmd.toArray(new String[0]), env.toArray(new String[0]), new File(workingDir), stdInText);
    }

    public static CommandResult execute(List<String> cmd, List<String> env, File workingDir, String stdInText) {
        return execute(cmd.toArray(new String[0]), env.toArray(new String[0]), workingDir, stdInText);
    }

    public static CommandResult execute(String[] cmd, String[] env, String workingDir, String stdInText) {
        return execute(cmd, env, new File(workingDir), stdInText);
    }

    /**
     * Runs a native process that will finish before the method returns. All the
     * other execute methods are convenience methods and call this method.
     */
    public static CommandResult execute(String[] cmd, String[] env, File workingDir, String stdInText) {
        return _execute(true, cmd, env, workingDir, stdInText);
    }

    private static class BackgroundRunner extends Thread {

        private Thread inThread;
        private Thread outThread;
        private Process proc;
        private CommandResult result;

        public BackgroundRunner(Process proc, CommandResult result, Thread inThread, Thread outThread) {
            this.proc = proc;
            this.result = result;
            this.inThread = inThread;
            this.outThread = outThread;
            this.start();
        }

        public void run() {
            while (inThread.isAlive() || outThread.isAlive() || !result.isFinished()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                }
                try {
                    result.setReturnCode(proc.exitValue());
                    result.setFinished(true);
                } catch (IllegalThreadStateException e) {
                }
            }
        }
    }

    private static class OutputConsumer extends Thread {

        private InputStream in;
        private StringBuffer buffer;

        public OutputConsumer(InputStream in, StringBuffer buffer) {
            this.in = in;
            this.buffer = buffer;
            this.start();
        }

        public void run() {
            Reader reader = new InputStreamReader(in);
            try {
                int i = reader.read();
                while (i != -1) {
                    buffer.append((char) i);
                    i = reader.read();
                }
            } catch (IOException ioe) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
                return;
            }
        }
    }

    private static class InputRelay extends Thread {

        private OutputStream out;
        private String stdIn;

        public InputRelay(OutputStream out, String stdIn) {
            if (stdIn == null) {
                return;
            }
            this.out = out;
            this.stdIn = stdIn;
            this.start();
        }

        public void run() {
            PrintWriter printOut = new PrintWriter(out, true);
            printOut.write(stdIn);
            printOut.flush();
            printOut.close();
        }
    }
}
