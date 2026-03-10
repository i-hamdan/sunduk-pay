package com.bxb.sunduk_pay.util;
import lombok.extern.log4j.Log4j2;

/**
 * Utility class used to measure execution time of a code block.
 *
 * <p>This class works like a simple stopwatch where you can start the timer
 * before executing a task and stop it after the task is completed to measure
 * the elapsed time in milliseconds.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     Stopwatch stopwatch = new Stopwatch();
 *     stopwatch.start();
 *
 *     // execute some operation
 *
 *     stopwatch.stop();
 *     long timeTaken = stopwatch.getElapsedTime();
 * </pre>
 *
 * This is commonly used for performance monitoring, debugging,
 * and measuring API or method execution time.
 */
@Log4j2
public class Stopwatch {
    
    long startTime;
    long stopTime;
    
    /**
     * Starts the stopwatch by recording the current system time.
     */
    public void start() {
        startTime = System.currentTimeMillis();
        log.debug("Stopwatch started at {}", startTime);
    }
    
    /**
     * Stops the stopwatch by recording the current system time.
     */
    public void stop() {
        stopTime = System.currentTimeMillis();
        log.debug("Stopwatch stopped at {}", stopTime);
    }
    
    /**
     * Returns the elapsed time between start and stop in milliseconds.
     *
     * @return elapsed time in milliseconds
     */
    public long getElapsedTime(){
        long elapsedTime = stopTime - startTime;
        log.debug("Elapsed time calculated: {} ms", elapsedTime);
        return elapsedTime;
    }
}
