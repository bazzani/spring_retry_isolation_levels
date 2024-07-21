package com.bevans.springboot.spring_retry_isolation_levels;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.EnableRetry;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
@Slf4j
@EnableRetry
public class SpringRetryIsolationLevelsApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringRetryIsolationLevelsApplication.class);
    private static final CountDownLatch LATCH = new CountDownLatch(5);
    private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private final MoneyTransferService moneyTransferService;

    public SpringRetryIsolationLevelsApplication(MoneyTransferService moneyTransferService) {
        this.moneyTransferService = moneyTransferService;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringRetryIsolationLevelsApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void simulateTransfers() {
        LOGGER.info("Resetting account balances");
        moneyTransferService.resetBalances(1L, 100, 2L, 0);

        // let's assume we know the account ids already
        THREAD_POOL.execute(getTransferExecutable(1L, 2L, 10));
        THREAD_POOL.execute(getTransferExecutable(1L, 2L, 5));
        THREAD_POOL.execute(getTransferExecutable(1L, 2L, 25));
        THREAD_POOL.execute(getTransferExecutable(1L, 2L, 12));
        THREAD_POOL.execute(getTransferExecutable(1L, 2L, 13));
    }

    private Runnable getTransferExecutable(long idSender, long idRecipient, int amount) {
        return () -> {
            try {
                LATCH.countDown();
                LATCH.await();

                moneyTransferService.transferMoney(idSender, idRecipient, amount);

                LOGGER.info("Money transferred successfully [idSender {} idRecipient {} amount {}]",
                        idSender, idRecipient, amount);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
