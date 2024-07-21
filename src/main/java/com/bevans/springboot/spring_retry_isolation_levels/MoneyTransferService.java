package com.bevans.springboot.spring_retry_isolation_levels;

public interface MoneyTransferService {
    void transferMoney(Long idSender, Long idRecipient, int amount);
}
