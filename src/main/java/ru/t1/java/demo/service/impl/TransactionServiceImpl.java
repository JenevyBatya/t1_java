package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.AccountMapper;
import ru.t1.java.demo.util.TransactionMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @LogDataSourceError
    @Override
    public List<TransactionDto> findAll() {
        List<Transaction> transactions = transactionRepository.findAll();

        return transactions.stream()
                .map(TransactionMapper::toDto)
                .collect(Collectors.toList());
    }

    @LogDataSourceError
    @Override
    public TransactionDto save(TransactionDto dto) {
        Transaction transaction = transactionRepository.save(TransactionMapper.toEntity(dto));
        return TransactionMapper.toDto(transaction);
    }

    @LogDataSourceError
    @Override
    public TransactionDto findById(Long id) {
        Transaction transaction = transactionRepository.findById(id).orElse(null);
        return TransactionMapper.toDto(transaction);
    }

    @LogDataSourceError
    @Override
    public void deleteById(Long id) {
        transactionRepository.deleteById(id);
    }

    @Override
    public List<TransactionDto> saveTransactions(List<TransactionDto> transactions) {
        List<TransactionDto> savedAccounts = new ArrayList<>();
        for (TransactionDto transactionDto : transactions) {
            transactionRepository.save(TransactionMapper.toEntity(transactionDto));
            savedAccounts.add(transactionDto);
        }
        return savedAccounts;
    }
}
