/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.stcurr.util;

import com.oracle.stcurr.ide.web.ConfigBean;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * change dynamically for performance testing? setCorePoolSize(int),
 * setMaximumPoolSize(int)
 *
 * @author mheimer
 */
@ApplicationScoped
public class CommandExecutorService implements ExecutorService {

//    private static final int DEFAULT_MAX_THREADS = 100;
//    private static final int DEFAULT_MAX_QUEUE = 1000;
    private ThreadPoolExecutor tpExecutor;
    @Inject
    private ConfigBean configBean;

    public CommandExecutorService() {
    }

    @PostConstruct
    public void init() {
        tpExecutor = new ThreadPoolExecutor(configBean.getThreadCount(), configBean.getThreadCount(),
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(configBean.getQueueSize()));
        tpExecutor.prestartAllCoreThreads();
    }

    @Override
    public void execute(Runnable command) {
        tpExecutor.execute(command);
    }

    @Override
    public void shutdown() {
        tpExecutor.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return tpExecutor.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return tpExecutor.isShutdown();
    }

    public boolean isTerminating() {
        return tpExecutor.isTerminating();
    }

    @Override
    public boolean isTerminated() {
        return tpExecutor.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return tpExecutor.awaitTermination(timeout, unit);
    }

    public void setThreadFactory(ThreadFactory threadFactory) {
        tpExecutor.setThreadFactory(threadFactory);
    }

    public ThreadFactory getThreadFactory() {
        return tpExecutor.getThreadFactory();
    }

    public void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
        tpExecutor.setRejectedExecutionHandler(handler);
    }

    public RejectedExecutionHandler getRejectedExecutionHandler() {
        return tpExecutor.getRejectedExecutionHandler();
    }

    public void setCorePoolSize(int corePoolSize) {
        tpExecutor.setCorePoolSize(corePoolSize);
    }

    public int getCorePoolSize() {
        return tpExecutor.getCorePoolSize();
    }

    public boolean prestartCoreThread() {
        return tpExecutor.prestartCoreThread();
    }

    public int prestartAllCoreThreads() {
        return tpExecutor.prestartAllCoreThreads();
    }

    public boolean allowsCoreThreadTimeOut() {
        return tpExecutor.allowsCoreThreadTimeOut();
    }

    public void allowCoreThreadTimeOut(boolean value) {
        tpExecutor.allowCoreThreadTimeOut(value);
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        tpExecutor.setMaximumPoolSize(maximumPoolSize);
    }

    public int getMaximumPoolSize() {
        return tpExecutor.getMaximumPoolSize();
    }

    public void setKeepAliveTime(long time, TimeUnit unit) {
        tpExecutor.setKeepAliveTime(time, unit);
    }

    public long getKeepAliveTime(TimeUnit unit) {
        return tpExecutor.getKeepAliveTime(unit);
    }

    public BlockingQueue<Runnable> getQueue() {
        return tpExecutor.getQueue();
    }

    public boolean remove(Runnable task) {
        return tpExecutor.remove(task);
    }

    public void purge() {
        tpExecutor.purge();
    }

    public int getPoolSize() {
        return tpExecutor.getPoolSize();
    }

    public int getActiveCount() {
        return tpExecutor.getActiveCount();
    }

    public int getLargestPoolSize() {
        return tpExecutor.getLargestPoolSize();
    }

    public long getTaskCount() {
        return tpExecutor.getTaskCount();
    }

    public long getCompletedTaskCount() {
        return tpExecutor.getCompletedTaskCount();
    }

    @Override
    public String toString() {
        return tpExecutor.toString();
    }

    @Override
    public Future<?> submit(Runnable task) {
        return tpExecutor.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return tpExecutor.submit(task, result);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return tpExecutor.submit(task);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return tpExecutor.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return tpExecutor.invokeAny(tasks, timeout, unit);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return tpExecutor.invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return tpExecutor.invokeAll(tasks, timeout, unit);
    }
}
