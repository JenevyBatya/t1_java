package ru.t1.java.demo.util;

import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.model.Account;

public class AccountMapper {

    public static Account toEntity(AccountDto dto) {
        return Account.builder()
                .clientId(dto.getClientId())
                .type(dto.getType())
                .balance(dto.getBalance())
                .status(dto.getStatus())
                .frozenAmount(dto.getFrozenAmount())
                .build();
    }

    public static AccountDto toDto(Account entity) {
        return AccountDto.builder()
                .clientId(entity.getClientId())
                .id(entity.getId())
                .type(entity.getType())
                .balance(entity.getBalance())
                .status(entity.getStatus())
                .frozenAmount(entity.getFrozenAmount())
                .build();
    }
}
