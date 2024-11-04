package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Transaction;

import java.util.List;

public interface TransactionService {
    List<Transaction> findAll();

    Transaction save(Transaction transaction);

    Transaction findById(Long id);

    void deleteById(Long id);
}
