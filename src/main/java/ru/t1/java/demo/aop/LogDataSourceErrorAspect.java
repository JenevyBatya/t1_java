package ru.t1.java.demo.aop;

import lombok.AllArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.service.DataSourceErrorLogService;

@AllArgsConstructor
@Aspect
@Component
public class LogDataSourceErrorAspect {
    private final DataSourceErrorLogService dataSourceErrorLogService;

    @AfterThrowing(pointcut = "@annotation(LogDataSourceError)", throwing = "e")
    public void handleException(JoinPoint joinPoint, Exception e) {
        DataSourceErrorLog dataSourceErrorLog = DataSourceErrorLog.builder()
                .stackTrace(getStackTraceAsString(e))
                .message(e.getMessage())
                .methodSignature(joinPoint.getSignature().toString())
                .build();
        dataSourceErrorLogService.save(dataSourceErrorLog);
    }

    public String getStackTraceAsString(Exception e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString());
        }
        return sb.toString();
    }
}
