package com.bevans.springboot.spring_retry_isolation_levels;

public interface MoneyTransferService {
    void resetBalances(long senderId, int senderStartingBalance, long recipientId, int recipientStartingBalance);

    void transferMoney(Long idSender, Long idRecipient, int amount);
}
