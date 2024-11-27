package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.aop.Metric;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.ProcessedTransactionInfo;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.dto.TransactionInfoDto;
import ru.t1.java.demo.kafka.KafkaProducer;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.enums.AccountStatus;
import ru.t1.java.demo.model.enums.TransactionStatus;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.TransactionMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link TransactionService} interface.
 * Provides functionality for managing transactions, interacting with accounts,
 * and sending transaction-related information to Kafka topics.
 */
@LogDataSourceError
@RequiredArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final KafkaProducer kafkaProducer;
    private final AccountService accountService;

    /**
     * Retrieves all transactions from the database.
     *
     * @return a list of {@link TransactionDto} representing all transactions.
     */
    @Override
    public List<TransactionDto> findAll() {
        List<Transaction> transactions = transactionRepository.findAll();
        return transactions.stream()
                .map(TransactionMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Saves a transaction to the database and returns the saved entity as a DTO.
     *
     * @param dto the transaction data to save.
     * @return the saved transaction as a {@link TransactionDto}.
     */
    @Metric(1000)
    @Override
    public TransactionDto save(TransactionDto dto) {
        Transaction transaction = transactionRepository.save(TransactionMapper.toEntity(dto));
        return TransactionMapper.toDto(transaction);
    }

    /**
     * Retrieves a transaction by its ID.
     *
     * @param id the ID of the transaction.
     * @return the transaction as a {@link TransactionDto}, or {@code null} if not found.
     */
    @Override
    public TransactionDto findById(Long id) {
        Transaction transaction = transactionRepository.findById(id).orElse(null);
        return TransactionMapper.toDto(transaction);
    }

    /**
     * Deletes a transaction by its ID.
     *
     * @param id the ID of the transaction to delete.
     */
    @Override
    public void deleteById(Long id) {
        transactionRepository.deleteById(id);
    }

    /**
     * Saves a list of transactions to the database.
     *
     * @param transactions the list of {@link TransactionDto} to save.
     * @return a list of saved {@link TransactionDto}.
     */
    @Override
    public List<TransactionDto> saveTransactions(List<TransactionDto> transactions) {
        List<TransactionDto> transactionDtos = new ArrayList<>();
        for (TransactionDto transactionDto : transactions) {
            transactionDtos.add(save(transactionDto));
        }
        return transactionDtos;
    }

    /**
     * Registers a list of transactions in the system by validating the associated account
     * and sending transaction information to the necessary Kafka topics.
     *
     * @param transactions the list of transactions to register.
     */
    @Metric(1000)
    @Override
    public void registerTransaction(List<TransactionDto> transactions) {
        for (TransactionDto transactionDto : transactions) {
            AccountDto accountDto = accountService.findById(transactionDto.getAccountId());
            if (accountDto.getStatus().equals(AccountStatus.OPEN)) {
                transactionDto.setStatus(TransactionStatus.REQUESTED);
                TransactionDto savedTransaction = save(transactionDto);
                accountService.updateBalance(savedTransaction, accountDto);
                sendTransactionalInfo(accountDto, savedTransaction);
            }
        }
    }

    /**
     * Sends a transaction to the Kafka topic `t1_demo_transactions`.
     *
     * @param transactionDto the transaction to send.
     */
    @Override
    public void sendTransaction(TransactionDto transactionDto) {
        kafkaProducer.sendTo("t1_demo_transactions", transactionDto);
    }

    /**
     * Sends transactional information to the Kafka topic `t1_demo_transaction_accept`.
     *
     * @param accountDto      the account related to the transaction.
     * @param transactionDto  the transaction to send.
     */
    private void sendTransactionalInfo(AccountDto accountDto, TransactionDto transactionDto) {
        TransactionInfoDto infoDto = new TransactionInfoDto(
                accountDto.getClientId(),
                accountDto.getId(),
                transactionDto.getId(),
                transactionDto.getTime(),
                transactionDto.getAmount(),
                accountDto.getBalance()
        );
        kafkaProducer.sendTo("t1_demo_transaction_accept", infoDto);
    }

    /**
     * Processes the result of a transaction from the Kafka topic `t1_demo_transaction_result`.
     * Updates the transaction's status based on the result.
     *
     * @param info the transaction processing result.
     */
    public void processResult(ProcessedTransactionInfo info) {
        switch (info.getStatus()) {
            case ACCECPTED -> processAccepted(info);
            case BLOCKED -> processBlocked(info);
            case REJECTED -> processRejected(info);
        }
    }

    /**
     * Processes a transaction with the status `BLOCKED`.
     *
     * @param info the transaction processing result.
     */
    private void processBlocked(ProcessedTransactionInfo info) {
        TransactionDto transactionDto = findById(info.getTransactionId());
        transactionDto.setStatus(TransactionStatus.BLOCKED);
        save(transactionDto);
        accountService.cancelTransactionUpdate(transactionDto);
        accountService.updateFrozenAmount(transactionDto);
    }

    /**
     * Processes a transaction with the status `ACCEPTED`.
     *
     * @param info the transaction processing result.
     */
    private void processAccepted(ProcessedTransactionInfo info) {
        TransactionDto transactionDto = findById(info.getTransactionId());
        transactionDto.setStatus(TransactionStatus.ACCECPTED);
        save(transactionDto);
    }

    /**
     * Processes a transaction with the status `REJECTED`.
     *
     * @param info the transaction processing result.
     */
    private void processRejected(ProcessedTransactionInfo info) {
        TransactionDto transactionDto = findById(info.getTransactionId());
        transactionDto.setStatus(TransactionStatus.REJECTED);
        save(transactionDto);
        accountService.cancelTransactionUpdate(transactionDto);
    }
}
