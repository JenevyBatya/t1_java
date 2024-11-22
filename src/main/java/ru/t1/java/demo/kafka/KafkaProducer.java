package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.util.ParserJson;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {


    private final KafkaTemplate<String, AccountDto> templateAccount;
    private final KafkaTemplate<String, TransactionDto> templateTransaction;
    private final KafkaTemplate<String, String> template;

    public void sendTo(String topic, Object payload) {
        try {
            log.info("Sending message to topic {} with payload {}", topic, payload);

            if (payload instanceof AccountDto) {
                templateAccount.send(topic, UUID.randomUUID().toString(), (AccountDto) payload).get();
            } else if (payload instanceof TransactionDto) {
                templateTransaction.send(topic, UUID.randomUUID().toString(), (TransactionDto) payload).get();
            } else {
                template.send(topic, UUID.randomUUID().toString(), ParserJson.toJson(payload));
//                throw new IllegalArgumentException("Unsupported payload type: " + payload.getClass().getName());
            }

        } catch (Exception ex) {
            log.error("Failed to send message to topic {}: {}", topic, ex.getMessage(), ex);
        } finally {
            if (payload instanceof AccountDto) {
                templateAccount.flush();
            } else if (payload instanceof TransactionDto) {
                templateTransaction.flush();
            }else {
                template.flush();
            }
        }

        log.info("Message sent to topic {} with payload {}", topic, payload);
    }


    private KafkaTemplate<String, ?> getTemplate(Object o) {
        if (o instanceof AccountDto) {
            return templateAccount;
        } else if (o instanceof TransactionDto) {
            return templateTransaction;
        } else return null;
    }


}

