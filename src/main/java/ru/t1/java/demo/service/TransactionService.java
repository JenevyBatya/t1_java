package ru.t1.java.demo.service;

import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Transaction;

import java.util.List;

public interface TransactionService {
    List<TransactionDto> findAll();

    TransactionDto save(TransactionDto dto);

    TransactionDto findById(Long id);

    void deleteById(Long id);
    List<TransactionDto> saveTransactions(List<TransactionDto> transactions);
}
