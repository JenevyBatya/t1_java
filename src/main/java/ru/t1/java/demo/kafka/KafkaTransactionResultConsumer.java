package ru.t1.java.demo.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.ProcessedTransactionInfo;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.ParserJson;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaTransactionResultConsumer {
    private final TransactionService transactionService;

    @KafkaListener(id = "${t1.kafka.consumer.group-id-transaction-result}",
            topics = "t1_demo_transaction_result",
            containerFactory = "kafkaTransactionResultFactory")
    public void listenToAccounts(@Payload List<String> messageList,
                                 Acknowledgment ack,
                                 @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                 @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        for (String info : messageList) {
            try {
                transactionService.processResult(ParserJson.fromJson(info, ProcessedTransactionInfo.class));
            } catch (JsonProcessingException e) {
                log.error(e.getMessage());
            } finally {
                log.error("After finaly");
                ack.acknowledge();
                log.error("After first ack");
            }

        }
    }
}
