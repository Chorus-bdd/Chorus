/**
 * MIT License
 *
 * Copyright (c) 2019 Chorus BDD Organisation.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.chorusbdd.chorus.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 14-Dec-2010
 * Time: 18:31:25
 *
 * An easy way to create Executors which name the threads, and configure other aspects of the threads created.
 * For some reason the factory methods on Executors don't give you this option
 *
 * Also adds monitoring for unexpected shutdown of executor threads
 */
public class NamedExecutors {

    private static class LogOnErrorThreadGroup extends ThreadGroup {
        public LogOnErrorThreadGroup() {
            super("JTimeSeriesLoggingThreadGroup");
        }

        public void uncaughtException(Thread t, Throwable e) {
            super.uncaughtException(t, e);
            if ( ! (e instanceof ThreadDeath)) {
                System.err.println("An Uncaught Exception Shut Down Thread " + t.getName() + e);
            }
        }
    }
    private static ThreadGroup threadDeathLoggingThreadGroup = new LogOnErrorThreadGroup();

    public static ThreadConfigurer DAEMON_THREAD_CONFIGURER = new ThreadConfigurer() {
        public void configureNewThread(Thread t) {
            t.setDaemon(true);
        }
    };

    public static ThreadConfigurer DEFAULT_THREAD_CONFIGURER = new ThreadConfigurer() {
        public void configureNewThread(Thread t) {
        }
    };

    public static ExecutorService newFixedThreadPool(String executorName, int nThreads) {
        return Executors.newFixedThreadPool(nThreads, new NamedThreadFactory(executorName + "-FixedThreadPool(" + nThreads + ")", DEFAULT_THREAD_CONFIGURER));
    }

    public static ExecutorService newSingleThreadExecutor(String executorName) {
        return Executors.newSingleThreadExecutor(new NamedThreadFactory(executorName + "-SingleThreadExecutor", DEFAULT_THREAD_CONFIGURER));
    }

    public static ExecutorService newCachedThreadPool(String executorName) {
        return Executors.newCachedThreadPool(new NamedThreadFactory(executorName + "-CachedThreadPool", DEFAULT_THREAD_CONFIGURER));
    }

    public static ScheduledExecutorService newSingleThreadScheduledExecutor(String executorName) {
        return Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(executorName + "-SingleThreadScheduledExecutor", DEFAULT_THREAD_CONFIGURER));
    }

    public static ScheduledExecutorService newScheduledThreadPool(String executorName, int corePoolSize, ThreadConfigurer threadConfigurer) {
        return Executors.newScheduledThreadPool(corePoolSize, new NamedThreadFactory(executorName + "-ScheduledThreadPool(" + corePoolSize + ")", threadConfigurer));
    }

    public static ExecutorService newFixedThreadPool(String executorName, int nThreads, ThreadConfigurer threadConfigurer) {
        return Executors.newFixedThreadPool(nThreads, new NamedThreadFactory(executorName + "-FixedThreadPool(" + nThreads + ")", threadConfigurer));
    }

    public static ExecutorService newSingleThreadExecutor(String executorName, ThreadConfigurer threadConfigurer) {
        return Executors.newSingleThreadExecutor(new NamedThreadFactory(executorName + "-SingleThreadExecutor", threadConfigurer));
    }

    public static ExecutorService newCachedThreadPool(String executorName, ThreadConfigurer threadConfigurer) {
        return Executors.newCachedThreadPool(new NamedThreadFactory(executorName + "-CachedThreadPool", threadConfigurer));
    }

    public static ScheduledExecutorService newSingleThreadScheduledExecutor(String executorName, ThreadConfigurer threadConfigurer) {
        return Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(executorName + "-SingleThreadScheduledExecutor", threadConfigurer));
    }

    public static ScheduledExecutorService newScheduledThreadPool(String executorName, int corePoolSize) {
        return Executors.newScheduledThreadPool(corePoolSize, new NamedThreadFactory(executorName + "-ScheduledThreadPool(" + corePoolSize + ")", DEFAULT_THREAD_CONFIGURER));
    }

    private static class NamedThreadFactory implements ThreadFactory {

        private ThreadGroup threadGroup;
        private AtomicInteger threadNumber;
        private String name;
        private ThreadConfigurer threadConfigurer;

        public NamedThreadFactory(String name, ThreadConfigurer threadConfigurer) {
            this.threadConfigurer = threadConfigurer;
            threadNumber = new AtomicInteger(0);
            this.name = name;
            threadGroup = threadDeathLoggingThreadGroup;
        }

        public Thread newThread(Runnable r) {
            Thread thread = new Thread(threadGroup, r, new StringBuilder(name).append("-").append(threadNumber.getAndIncrement()).toString());
            threadConfigurer.configureNewThread(thread);
            return thread;
        }
    }

    public static interface ThreadConfigurer {
        void configureNewThread(Thread t);
    }

}
