package ru.t1.java.demo.util;

import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.DataSourceErrorLogDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.DataSourceErrorLog;

public class DataSourceErrorLogMapper {
    public static DataSourceErrorLog toEntity(DataSourceErrorLogDto dto) {
        return DataSourceErrorLog.builder()
                .stackTrace(dto.getMessage())
                .message(dto.getMessage())
                .methodSignature(dto.getMethodSignature())
                .build();
    }

    public static DataSourceErrorLogDto toDto(DataSourceErrorLog entity) {
        return DataSourceErrorLogDto.builder()
                .id(entity.getId())
                .stackTrace(entity.getMessage())
                .message(entity.getMessage())
                .methodSignature(entity.getMethodSignature())
                .build();
    }
}
