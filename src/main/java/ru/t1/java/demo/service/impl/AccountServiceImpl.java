package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.service.AccountService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public Account findById(Long id) {
        return accountRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        accountRepository.deleteById(id);
    }
}
