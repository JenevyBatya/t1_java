package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.service.AccountService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    @LogDataSourceError
    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @LogDataSourceError
    @Override
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @LogDataSourceError
    @Override
    public Account findById(Long id) {
        return accountRepository.findById(id).orElse(null);
    }

    @LogDataSourceError
    @Override
    public void deleteById(Long id) {
        accountRepository.deleteById(id);
    }
}
