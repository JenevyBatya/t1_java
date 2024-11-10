package ru.t1.java.demo.util;

import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Transaction;

public class TransactionMapper {

    public static Transaction toEntity(TransactionDto dto) {
        return Transaction.builder()
                .amount(dto.getAmount())
                .time(dto.getTime())
                .build();
    }

    public static TransactionDto toDto(TransactionDto dto) {
        return TransactionDto.builder()
                .id(dto.getId())
                .accountId(dto.getAccountId())
                .amount(dto.getAmount())
                .time(dto.getTime())
                .build();
    }
}
