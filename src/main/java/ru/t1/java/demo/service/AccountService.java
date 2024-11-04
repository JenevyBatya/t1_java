package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Account;

import java.util.List;

public interface AccountService {
    List<Account> findAll();

    Account save(Account account);

    Account findById(Long id);

    void deleteById(Long id);
}
