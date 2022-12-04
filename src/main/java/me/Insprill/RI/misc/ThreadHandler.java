package me.Insprill.RI.misc;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class ThreadHandler {

    /**
     * @param nameFormat Name format
     * @return new ThreadFactory with formatted Thread names.
     */
    public static ThreadFactory createThreadFactory(String nameFormat) {
        return new ThreadFactoryBuilder().setNameFormat(nameFormat).build();
    }

    /**
     * @param nameFormat Name format
     * @param priority   Thread priority.
     * @return new ThreadFactory with formatted Thread names and the specified priority.
     */
    public static ThreadFactory createThreadFactory(String nameFormat, int priority) {
        return new ThreadFactoryBuilder().setNameFormat(nameFormat).setPriority(priority).build();
    }

    /**
     * Shuts down the given ExecutorService.
     *
     * @param executorService The ExecutorService to shutdown.
     * @param timeout         Time in seconds to wait before forcefully terminating the ExecutorService.
     */
    public static void shutdownExecutor(ExecutorService executorService, int timeout) {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(timeout, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(timeout, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

}
