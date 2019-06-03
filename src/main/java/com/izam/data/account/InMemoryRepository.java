package com.izam.data.account;

import com.izam.app.api.ErrorResponse;
import com.izam.domain.account.Account;
import com.izam.domain.account.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class InMemoryRepository implements AccountRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryRepository.class);

    private static final Map MEMORY_STORE = new ConcurrentHashMap<String, BigDecimal>();
    final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    @Override
    @SuppressWarnings("unchecked")
    public Account getAccount(final String login) {
        return Account.builder()
                .login(login)
                .amount((BigDecimal) MEMORY_STORE.getOrDefault(login, null))
                .build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Account setMoney(final String login, final BigDecimal money) {
        return Account.builder()
                .login(login)
                .amount((BigDecimal) MEMORY_STORE.compute(login, (k, v) -> money))
                .build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public ErrorResponse transferMoney(final String from, final String to,
                                       final BigDecimal value) {
        /**
         * Transfer money from one account to another should be logical transaction:
         * if one operation has failed whole transaction failed
         */
        // logical Transaction BEGIN

        rwl.readLock().lock();
        BigDecimal amountFrom = (BigDecimal) MEMORY_STORE.getOrDefault(from, null);
        BigDecimal amountTo = (BigDecimal) MEMORY_STORE.getOrDefault(to, null);
        rwl.readLock().unlock();

        if (amountFrom == null || amountTo == null) {
            String message = String.format("Transaction from account '%s' to account '%s' failed. " +
                            "Account data not found: '%s'", from, to,
                    (amountFrom == null ? from : to));
            log.error(message);
            return new ErrorResponse(RepositoryStatus.NOT_FOUND.getCode(), message);
        }
        rwl.writeLock().lock();
        MEMORY_STORE.computeIfPresent(from, (k,v) -> amountFrom.subtract(value));
        MEMORY_STORE.computeIfPresent(to, (k,v) -> amountTo.add(value));
        rwl.writeLock().unlock();

        rwl.readLock().lock();
        BigDecimal controlAmountFrom = (BigDecimal) MEMORY_STORE.getOrDefault(from, null);
        BigDecimal controlAmountTo = (BigDecimal) MEMORY_STORE.getOrDefault(to, null);
        rwl.readLock().unlock();

        if (!controlAmountFrom.equals(amountFrom.subtract(value))) {
            String message = String.format("Transaction checking failed, account: '%s'",  from);
            log.error(message);
            return new ErrorResponse(RepositoryStatus.BAD_REQUEST.getCode(), message);
        }
        if (!controlAmountTo.equals(amountTo.add(value))) {
            String message = String.format("Transaction checking failed, account: '%s'",  to);
            log.error(message);
            return new ErrorResponse(RepositoryStatus.BAD_REQUEST.getCode(), message);
        }

        // logical Transaction END
        log.info(String.format(Locale.US,
                "Money '%.2f' has transferred from account '%s' to account '%s'",
                value, from, to));
        return new ErrorResponse(RepositoryStatus.OK.getCode(), "Success");
    }
}
