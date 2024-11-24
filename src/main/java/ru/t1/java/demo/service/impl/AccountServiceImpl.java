package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.aop.Metric;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.ProcessedTransactionInfo;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.kafka.KafkaProducer;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.util.AccountMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link AccountService} interface for managing accounts.
 * <p>
 * This service provides methods to perform CRUD operations on accounts,
 * update balances, and handle account-related business logic.
 * </p>
 * <p>
 * Uses {@link AccountRepository} for data persistence and {@link KafkaProducer}
 * for sending account-related events to Kafka.
 * </p>
 */
@LogDataSourceError
@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final KafkaProducer kafkaProducer;

    /**
     * Retrieves all accounts from the database.
     *
     * @return a list of accounts as {@link AccountDto}.
     */
    public List<AccountDto> findAll() {
        List<Account> accounts = accountRepository.findAll();

        return accounts.stream()
                .map(AccountMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Saves an account to the database.
     *
     * @param dto the account data to save.
     * @return the saved account as {@link AccountDto}.
     */
    @Metric(1000)
    public AccountDto save(AccountDto dto) {
        Account account = accountRepository.save(AccountMapper.toEntity(dto));
        return AccountMapper.toDto(account);
    }

    /**
     * Finds an account by its ID.
     *
     * @param id the ID of the account.
     * @return the account as {@link AccountDto}, or {@code null} if not found.
     */
    public AccountDto findById(Long id) {
        Account account = accountRepository.findById(id).orElse(null);
        return AccountMapper.toDto(account);
    }

    /**
     * Deletes an account by its ID.
     *
     * @param id the ID of the account to delete.
     */
    public void deleteById(Long id) {
        accountRepository.deleteById(id);
    }

    /**
     * Saves a list of accounts to the database.
     *
     * @param accounts the list of accounts to save.
     * @return the saved accounts as a list of {@link AccountDto}.
     */
    @Metric(1000)
    public List<AccountDto> saveAccounts(List<AccountDto> accounts) {
        List<AccountDto> savedAccounts = new ArrayList<>();
        for (AccountDto accountDto : accounts) {
            accountRepository.save(AccountMapper.toEntity(accountDto));
            savedAccounts.add(accountDto);
        }
        return savedAccounts;
    }

    /**
     * Updates the balance of an account based on a transaction.
     *
     * @param transactionDto the transaction data.
     * @param accountDto     the account to update.
     * @return the updated account as {@link AccountDto}.
     */
    public AccountDto updateBalance(TransactionDto transactionDto, AccountDto accountDto) {
        accountDto.setBalance(accountDto.getBalance() + transactionDto.getAmount());
        return save(accountDto);
    }

    /**
     * Cancels a balance update caused by a transaction.
     *
     * @param transactionDto the transaction data to reverse.
     * @return the updated account as {@link AccountDto}.
     */
    public AccountDto cancelTransactionUpdate(TransactionDto transactionDto) {
        AccountDto accountDto = findById(transactionDto.getAccountId());
        accountDto.setBalance(accountDto.getBalance() - transactionDto.getAmount());
        return save(accountDto);
    }

    /**
     * Updates the frozen amount for an account based on a transaction.
     *
     * @param transactionDto the transaction data.
     * @return the updated account as {@link AccountDto}.
     */
    public AccountDto updateFrozenAmount(TransactionDto transactionDto) {
        AccountDto accountDto = findById(transactionDto.getAccountId());
        accountDto.setBalance(accountDto.getFrozenAmount() + transactionDto.getAmount());
        return save(accountDto);
    }
}
