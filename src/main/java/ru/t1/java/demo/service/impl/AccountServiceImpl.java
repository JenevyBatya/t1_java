package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.aop.Metric;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.kafka.KafkaProducer;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.util.AccountMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@LogDataSourceError
@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final KafkaProducer kafkaProducer;
    public List<AccountDto> findAll() {
        List<Account> accounts = accountRepository.findAll();

        return accounts.stream()
                .map(AccountMapper::toDto)
                .collect(Collectors.toList());
    }
   
    public void registerAccount(AccountDto dto) {
        kafkaProducer.sendTo("t1_demo_accounts", dto);
    }

   
    public void registerFromDataGenerator(AccountDto accountDto) {

    }
    @Metric(1000)
    public AccountDto save(AccountDto dto) {
        Account account = accountRepository.save(AccountMapper.toEntity(dto));
//        kafkaProducer.sendTo("t1_demo_accounts", dto);
        return AccountMapper.toDto(account);
    }
   
    public AccountDto findById(Long id) {
        Account account = accountRepository.findById(id).orElse(null);
        return AccountMapper.toDto(account);
    }

   
    public void deleteById(Long id) {
        accountRepository.deleteById(id);
    }
    @Metric(1000)
    public List<AccountDto> saveAccounts(List<AccountDto> accounts) {
        List<AccountDto> savedAccounts = new ArrayList<>();
        for (AccountDto accountDto : accounts) {
            accountRepository.save(AccountMapper.toEntity(accountDto));
            savedAccounts.add(accountDto);
        }
        return savedAccounts;
    }
}
