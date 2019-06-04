package com.izam.data.account;

import com.izam.app.api.ErrorResponse;
import com.izam.domain.account.Account;
import com.izam.domain.account.AccountRepository;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.concurrent.ConcurrentMap;

public class InMemoryRepository implements AccountRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryRepository.class);

    private DB db = DBMaker.memoryDB()
            .transactionEnable()
            .closeOnJvmShutdown()
            .make();

    private ConcurrentMap<String, BigDecimal> map = db
            .hashMap("MEMORY_STORE", Serializer.STRING, Serializer.BIG_DECIMAL)
            .createOrOpen();

    @Override
    public Account getAccount(final String login) {
        Account account = Account.builder()
                .login(login)
                .amount(map.getOrDefault(login, null))
                .build();
        db.commit();
        return account;
    }

    @Override
    public Account setMoney(final String login, final BigDecimal money) {
        Account account = Account.builder()
                .login(login)
                .amount(map.compute(login, (k, v) -> money))
                .build();
        db.commit();
        return account;
    }

    @Override
    public ErrorResponse transferMoney(final String from, final String to,
                                       final BigDecimal value) {
        /**
         * Transfer money from one account to another should be logical transaction:
         * if one operation has failed whole transaction failed
         */
        BigDecimal amountFrom = map.getOrDefault(from, null);
        BigDecimal amountTo = map.getOrDefault(to, null);
        if (amountFrom == null || amountTo == null) {
            String message = String.format("Transaction from account '%s' to account '%s' failed. " +
                            "Account data not found: '%s'", from, to,
                    (amountFrom == null ? from : to));
            log.error(message);
            return new ErrorResponse(RepositoryStatus.NOT_FOUND.getCode(), message);
        }

        map.computeIfPresent(from, (k, v) -> amountFrom.subtract(value));
        map.computeIfPresent(to, (k, v) -> amountTo.add(value));
        db.commit();

        // checking transaction: if checking failed = rollback!
        if (!amountFrom.subtract(value).equals(map.getOrDefault(from, null)) ||
                !amountTo.add(value).equals(map.getOrDefault(to, null))) {

            db.rollback();

            String message = String.format("Transaction checking failed, from '%s' to '%s'", from, to);
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
