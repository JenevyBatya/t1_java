package ru.t1.java.demo.util;

import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;

public class AccountMapper {

    public static Account toEntity(AccountDto dto) {
        return Account.builder()
                .type(dto.getType())
                .balance(dto.getBalance())
                .build();
    }

    public static AccountDto toDto(Account entity) {
        return AccountDto.builder()
                .id(entity.getId())
                .type(entity.getType())
                .balance(entity.getBalance())
                .build();
    }
}
