package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.DataSourceErrorLogDto;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;
import ru.t1.java.demo.service.DataSourceErrorLogService;
import ru.t1.java.demo.util.DataSourceErrorLogMapper;

@RequiredArgsConstructor
@Service
public class DataSourceErrorLogServiceImpl implements DataSourceErrorLogService {
    private final DataSourceErrorLogRepository dataSourceErrorLogRepository;

    @Override
    public DataSourceErrorLogDto save(DataSourceErrorLogDto log) {
        DataSourceErrorLog newLog = dataSourceErrorLogRepository.save(DataSourceErrorLogMapper.toEntity(log));
        return DataSourceErrorLogMapper.toDto(newLog);
    }
}
