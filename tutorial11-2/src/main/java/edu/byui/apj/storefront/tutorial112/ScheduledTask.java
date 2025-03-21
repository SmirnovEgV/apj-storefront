package edu.byui.apj.storefront.tutorial112;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class ScheduledTask {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTask.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private static final List<String> names = Arrays.asList(
            "Alice", "Bob", "Charlie", "David", "Emma",
            "Frank", "Grace", "Henry", "Ivy", "Jack",
            "Karen", "Liam", "Mia", "Noah", "Olivia",
            "Paul", "Quinn", "Ryan", "Sophia", "Thomas"
    );

    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        log.info("Current time is now {}", dateFormat.format(new Date()));
    }

    @Scheduled(cron = "0 0 22 * * ?") // Runs every day at 10:00 PM
    public void processNamesInParallel() {
        log.info("Starting name processing task at {}", dateFormat.format(new Date()));

        // Split the list into two batches
        int mid = names.size() / 2;
        List<String> batch1 = names.subList(0, mid);
        List<String> batch2 = names.subList(mid, names.size());

        // Create an ExecutorService with 2 threads
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Submit tasks for each batch
        executor.submit(() -> processBatch(batch1));
        executor.submit(() -> processBatch(batch2));

        executor.shutdown();
        try {
            if (executor.awaitTermination(1, TimeUnit.MINUTES)) {
                log.info("All done here!");
            }
        } catch (InterruptedException e) {
            log.error("Task was interrupted", e);
        }
    }

    // Method to process a batch of names
    private void processBatch(List<String> batch) {
        for (String name : batch) {
            log.info("Processing name: {} at {}", name, dateFormat.format(new Date()));
            try {
                Thread.sleep(500); // Simulate some processing time
            } catch (InterruptedException e) {
                log.error("Thread interrupted", e);
            }
        }
    }

}
