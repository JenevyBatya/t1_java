package ru.t1.java.demo.service;

import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;

import java.util.List;

public interface AccountService {
    List<AccountDto> findAll();

    AccountDto save(AccountDto dto);

    AccountDto findById(Long id);

    void deleteById(Long id);

     List<AccountDto> saveAccounts(List<AccountDto> accounts);
     void registerAccount(AccountDto accountDto);
     void registerFromDataGenerator(AccountDto accountDto);
}
