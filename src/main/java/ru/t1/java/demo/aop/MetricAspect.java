package ru.t1.java.demo.aop;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.DataSourceErrorLogDto;
import ru.t1.java.demo.service.DataSourceErrorLogService;

import java.util.Arrays;
import java.util.UUID;

@AllArgsConstructor
@Aspect
@Component
@Slf4j
public class MetricAspect {

    private final KafkaTemplate<String, Message<DataSourceErrorLogDto>> kafkaTemplate;
    private static final String TOPIC = "t1_demo_metrics";

    @Around("@annotation(metric)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint, Metric metric) throws Throwable {
        long startTime = System.currentTimeMillis();
        //TODO: разобраться при возможном исключении
        Object result = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - startTime;
        if (executionTime > metric.value()) {
            DataSourceErrorLogDto dto = null;
            String methodName = joinPoint.getSignature().toShortString();
            String methodArgs = Arrays.toString(joinPoint.getArgs());

            String payload = String.format("Method: %s, Execution Time: %d ms, Arguments: %s",
                    methodName, executionTime, methodArgs);
            try {
                dto = DataSourceErrorLogDto.builder()
                        .stackTrace("Превышение врмени работы метода")
                        .message(payload)
                        .methodSignature(methodName)
                        .build();
                Message<DataSourceErrorLogDto> message = MessageBuilder
                        .withPayload(dto)
                        .setHeader("errorType", "METRICS")
                        .build();
                kafkaTemplate.send(TOPIC, UUID.randomUUID().toString(), message)
                        .get();

                log.info("Error message sent to Kafka topic '{}': {}", TOPIC, message);

            } catch (Exception sendException) {
                log.error("Failed to send error to Kafka topic '{}': {}", TOPIC, sendException.getMessage());
            }
        }

        return result;
    }
}
