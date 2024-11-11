package ru.t1.java.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;

//TODO: додумать реализацию универсальных template для producer
@Getter
@NoArgsConstructor(force = true)
public abstract class BaseDto<T> {
    private final KafkaTemplate<String, T> kafkaTemplate;
    private T payload;

    protected BaseDto(KafkaTemplate<String, T> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


}