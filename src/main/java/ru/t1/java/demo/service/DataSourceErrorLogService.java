package ru.t1.java.demo.service;

import ru.t1.java.demo.dto.DataSourceErrorLogDto;
import ru.t1.java.demo.model.DataSourceErrorLog;

public interface DataSourceErrorLogService {
    DataSourceErrorLogDto save(DataSourceErrorLogDto log);
}
