package com.bevans.springboot.spring_retry_isolation_levels;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MoneyTransferServiceJpa implements MoneyTransferService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MoneyTransferServiceJpa.class);

    private final BankAccountRepository bankAccountRepository;

    public MoneyTransferServiceJpa(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRES_NEW)
    public void transferMoney(Long idSender, Long idRecipient, int amount) {
        // let's omit the validating class and some validation right here
        final var senderAccount = bankAccountRepository.findById(idSender)
                .orElseThrow(() -> new RuntimeException("The transfer failed, a sender with id [%s] was not found.".formatted(idSender)));
        final var recipientAccount = bankAccountRepository.findById(idRecipient)
                .orElseThrow(() -> new RuntimeException("The transfer failed, a recipient with id [%s] was not found.".formatted(idRecipient)));

        checkBalanceBeforeMoneyTransfer(senderAccount, amount);

        senderAccount.setBalance(senderAccount.getBalance() - amount);
        recipientAccount.setBalance(recipientAccount.getBalance() + amount);

        LOGGER.info("Money transfer attempt [idSender {} idRecipient {} amountOfMoney {}]",
                idSender, idRecipient, amount);
    }

    private void checkBalanceBeforeMoneyTransfer(BankAccount bankAccount, int amount) {
        if (bankAccountRepository == null) {
            throw new RuntimeException("BankAccount is null");
        }

        if (bankAccount.getBalance() <= 0 || bankAccount.getBalance() - amount < 0) {
            throw new RuntimeException("The balance of BankAccount [%s] is insufficient".formatted(bankAccount));
        }
    }
}
