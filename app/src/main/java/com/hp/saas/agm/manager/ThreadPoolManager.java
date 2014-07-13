package com.hp.saas.agm.manager;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;


/**
 * Created by lugan on 6/10/2014.
 */
@SuppressWarnings("unused")
public class ThreadPoolManager {

    static final int DOWNLOAD_FAILED = -1;
    static final int DOWNLOAD_STARTED = 1;
    static final int DOWNLOAD_COMPLETE = 2;
    static final int DECODE_STARTED = 3;
    static final int TASK_COMPLETE = 4;

    // Sets the size of the storage that's used to cache images
    private static final int IMAGE_CACHE_SIZE = 1024 * 1024 * 4;

    // Sets the amount of time an idle thread will wait for a task before terminating
    private static final int KEEP_ALIVE_TIME = 1;

    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;

    // Sets the initial threadpool size to 8
    private static final int CORE_POOL_SIZE = 8;

    // Sets the maximum threadpool size to 8
    private static final int MAXIMUM_POOL_SIZE = 8;

    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    private static final BlockingQueue<Runnable> threadQueue;

    private static final ThreadPoolExecutor threadPool;
    private static ThreadPoolManager threadPoolManager;



    static {

        // The time unit for "keep alive" is in seconds
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

        threadQueue = new LinkedBlockingQueue<Runnable>();

        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, threadQueue);

        // Creates a single static instance of PhotoManager
        threadPoolManager = new ThreadPoolManager();
    }

    private void ThreadPoolManager() {

    }

    public static ThreadPoolManager getInstance() {
        return threadPoolManager;
    }

    static public void run(Runnable command) {
        threadPoolManager.threadPool.execute(command);
    }

    private static final ExecutorService ourThreadExecutorsService = new ThreadPoolExecutor(
            3,
            Integer.MAX_VALUE,
            5 * 60L,
            TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(),
            new ThreadFactory() {
                int i;
                public Thread newThread(Runnable r) {
                    final Thread thread = new Thread(r, "ApplicationImpl pooled thread "+i++) {
                        public void interrupt() {

                            super.interrupt();
                        }

                        public void run() {
                            try {
                                super.run();
                            }
                            catch (Throwable t) {

                            }
                        }
                    };
                    thread.setPriority(Thread.NORM_PRIORITY - 1);
                    return thread;
                }
            }
    );

    public Future<?> executeOnPooledThread(final Runnable action) {
        return ourThreadExecutorsService.submit(new Runnable() {
            public void run() {
                try {
                    action.run();
                }
                catch (Throwable t) {
                   /* LOG.error(t);*/
                }
                finally {
                    Thread.interrupted(); // reset interrupted status
                }
            }
        });
    }

    public static <T> Future<T> executeOnPooledThread(final Callable<T> action) {
        return ourThreadExecutorsService.submit(new Callable<T>() {
            public T call() {
                try {
                    return action.call();
                }
                catch (Throwable t) {
                }
                finally {
                    Thread.interrupted(); // reset interrupted status
                }
                return null;
            }
        });
    }




}
