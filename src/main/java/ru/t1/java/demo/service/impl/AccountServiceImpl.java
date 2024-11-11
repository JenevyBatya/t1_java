package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.util.AccountMapper;
import ru.t1.java.demo.util.ClientMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    @LogDataSourceError
    @Override
    public List<AccountDto> findAll() {
        List<Account> accounts = accountRepository.findAll();

        return accounts.stream()
                .map(AccountMapper::toDto) // Здесь вызывается нестатический метод toDto через экземпляр accountMapper
                .collect(Collectors.toList());
    }

    @LogDataSourceError
    @Override
    public AccountDto save(AccountDto dto) {
        Account account = accountRepository.save(AccountMapper.toEntity(dto));
        return AccountMapper.toDto(account);
    }

    @LogDataSourceError
    @Override
    public AccountDto findById(Long id) {
        Account account = accountRepository.findById(id).orElse(null);
        return AccountMapper.toDto(account);
    }

    @LogDataSourceError
    @Override
    public void deleteById(Long id) {
        accountRepository.deleteById(id);
    }
}
