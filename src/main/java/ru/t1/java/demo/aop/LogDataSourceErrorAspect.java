package ru.t1.java.demo.aop;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.DataSourceErrorLogDto;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.service.DataSourceErrorLogService;

import java.util.UUID;

@AllArgsConstructor
@Aspect
@Component
@Slf4j
public class LogDataSourceErrorAspect {
    private final DataSourceErrorLogService dataSourceErrorLogService;
    private final KafkaTemplate<String, Message<DataSourceErrorLogDto>> kafkaTemplate;
    private static final String TOPIC = "t1_demo_metrics";

    @AfterThrowing(pointcut = "within(@LogDataSourceError *)", throwing = "e")
    public void handleException(JoinPoint joinPoint, Exception e) {
        DataSourceErrorLogDto dto = null;
        try {
            dto = DataSourceErrorLogDto.builder()
                    .stackTrace(getStackTraceAsString(e))
                    .message(e.getMessage())
                    .methodSignature(joinPoint.getSignature().toString())
                    .build();
            Message<DataSourceErrorLogDto> message = MessageBuilder
                    .withPayload(dto)
                    .setHeader("errorType", "DATA_SOURCE")
                    .build();
            kafkaTemplate.send(TOPIC, UUID.randomUUID().toString(), message)
                    .get();

            log.info("Error message sent to Kafka topic '{}': {}", TOPIC, message);

        } catch (Exception sendException) {
            logErrorToDatabase( e, dto);
            log.error("Failed to send error to Kafka topic '{}': {}", TOPIC, sendException.getMessage());
        }
    }

    public String getStackTraceAsString(Exception e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString());
        }
        return sb.toString();
    }


    private void logErrorToDatabase(Exception e, DataSourceErrorLogDto dto) {
        try {


            dataSourceErrorLogService.save(dto);

            log.info("Error saved to database: {}", e.getMessage());
        } catch (Exception dbException) {
            log.error("Failed to save error to database: {}", dbException.getMessage());
        }
    }
}
