package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;
import ru.t1.java.demo.service.DataSourceErrorLogService;

@RequiredArgsConstructor
@Service
public class DataSourceErrorLogServiceImpl implements DataSourceErrorLogService {
    private final DataSourceErrorLogRepository dataSourceErrorLogRepository;

    @Override
    public DataSourceErrorLog save(DataSourceErrorLog log) {
        return dataSourceErrorLogRepository.save(log);
    }
}
