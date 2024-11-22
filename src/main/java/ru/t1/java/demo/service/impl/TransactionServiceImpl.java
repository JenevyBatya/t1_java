package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.aop.Metric;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.dto.TransactionInfoDto;
import ru.t1.java.demo.kafka.KafkaProducer;
import ru.t1.java.demo.kafka.KafkaTransactionConsumer;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.enums.AccountStatus;
import ru.t1.java.demo.model.enums.TransactionStatus;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.AccountMapper;
import ru.t1.java.demo.util.TransactionMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@LogDataSourceError
@RequiredArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final KafkaProducer kafkaProducer;
    private final AccountService accountService;

    @Override
    public List<TransactionDto> findAll() {
        List<Transaction> transactions = transactionRepository.findAll();

        return transactions.stream()
                .map(TransactionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Metric(1000)
    @Override
    public TransactionDto save(TransactionDto dto) {
        Transaction transaction = transactionRepository.save(TransactionMapper.toEntity(dto));
        return TransactionMapper.toDto(transaction);
    }

    @Override
    public TransactionDto findById(Long id) {
        Transaction transaction = transactionRepository.findById(id).orElse(null);
        return TransactionMapper.toDto(transaction);
    }

    @Override
    public void deleteById(Long id) {
        transactionRepository.deleteById(id);
    }

    @Metric(1000)
    @Override
    public List<TransactionDto> saveTransactions(List<TransactionDto> transactions) {
        List<TransactionDto> savedAccounts = new ArrayList<>();
        for (TransactionDto transactionDto : transactions) {
            AccountDto accountDto = accountService.findById(transactionDto.getAccountId());
            if (accountDto.getStatus().equals(AccountStatus.OPEN)) {
                transactionDto.setStatus(TransactionStatus.REQUESTED);
                accountService.updateBalance(transactionDto, accountDto);
                savedAccounts.add(save(transactionDto));

                sendTransactionalInfo(accountDto, transactionDto);
            }

        }
        return savedAccounts;
    }

    @Override
    public void registerTransaction(TransactionDto transactionDto) {
        kafkaProducer.sendTo("t1_demo_transactions", transactionDto);
    }

    private void sendTransactionalInfo(AccountDto accountDto, TransactionDto transactionDto){
        TransactionInfoDto infoDto = new TransactionInfoDto(
                accountDto.getClientId(),
                accountDto.getId(),
                transactionDto.getId(),
                transactionDto.getTime(),
                transactionDto.getAmount(),
                accountDto.getBalance());
        kafkaProducer.sendTo("t1_demo_transaction_accept", infoDto);

    }
}
