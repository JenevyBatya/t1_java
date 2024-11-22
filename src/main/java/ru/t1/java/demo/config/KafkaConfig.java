package ru.t1.java.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.DataSourceErrorLogDto;
//import ru.t1.java.demo.dto.Message<DataSourceErrorLogDto>;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.kafka.MessageDeserializer;
import org.springframework.messaging.Message;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaConfig {
    @Value("${t1.kafka.consumer.group-id-account}")
    private String groupIdAccount;
    @Value("${t1.kafka.consumer.group-id-transaction}")
    private String groupIdTransaction;
    @Value("${t1.kafka.bootstrap-servers}")
    private String servers;
    @Value("${t1.kafka.session.timeout.ms:15000}")
    private String sessionTimeout;
    @Value("${t1.kafka.max.partition.fetch.bytes:300000}")
    private String maxPartitionFetchBytes;
    @Value("${t1.kafka.max.poll.records:1}")
    private String maxPollRecords;
    @Value("${t1.kafka.max.poll.interval.ms:3000}")
    private String maxPollIntervalsMs;

    private Map<String, Object> commonConsumerProps(String groupId, String defaultType) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageDeserializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, defaultType);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeout);
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, maxPartitionFetchBytes);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollIntervalsMs);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, MessageDeserializer.class.getName());
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, MessageDeserializer.class);
        return props;
    }

    private <T> ConsumerFactory<String, T> commonConsumerFactory(Map<String, Object> props) {
        DefaultKafkaConsumerFactory<String, T> factory = new DefaultKafkaConsumerFactory<>(props);
        factory.setKeyDeserializer(new StringDeserializer());
        return factory;
    }

    @Bean
    @Qualifier("consumerAccountFactory")
    public ConsumerFactory<String, AccountDto> consumerAccountFactory() {
        Map<String, Object> props = commonConsumerProps(groupIdAccount, "ru.t1.java.demo.dto.AccountDto");
        return commonConsumerFactory(props);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, AccountDto> kafkaAccountContainerFactory(@Qualifier("consumerAccountFactory") ConsumerFactory<String, AccountDto> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, AccountDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factoryBuilder(consumerFactory, factory);
        return factory;
    }

    @Bean
    @Qualifier("consumerTransactionFactory")
    public ConsumerFactory<String, TransactionDto> consumerTransactionFactory() {
        Map<String, Object> props = commonConsumerProps(groupIdTransaction, "ru.t1.java.demo.dto.TransactionDto");
        return commonConsumerFactory(props);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, TransactionDto> kafkaTransactionContainerFactory(@Qualifier("consumerTransactionFactory") ConsumerFactory<String, TransactionDto> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, TransactionDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factoryBuilder(consumerFactory, factory);
        return factory;
    }

    @Bean
    @Qualifier("consumerDataSourceErrorLogFactory")
    public ConsumerFactory<String, Message<DataSourceErrorLogDto>> consumerDataSourceErrorLogFactory() {
        Map<String, Object> props = commonConsumerProps(groupIdAccount, "ru.t1.java.demo.dto.Message<DataSourceErrorLogDto>");
        return commonConsumerFactory(props);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, Message<DataSourceErrorLogDto>> kafkaDataSourceErrorLogContainerFactory(@Qualifier("consumerDataSourceErrorLogFactory") ConsumerFactory<String, Message<DataSourceErrorLogDto>> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, Message<DataSourceErrorLogDto>> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factoryBuilder(consumerFactory, factory);
        return factory;
    }

    private <T> void factoryBuilder(ConsumerFactory<String, T> consumerFactory, ConcurrentKafkaListenerContainerFactory<String, T> factory) {
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        factory.setConcurrency(1);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.getContainerProperties().setPollTimeout(5000);
        factory.getContainerProperties().setMicrometerEnabled(true);
        factory.setCommonErrorHandler(errorHandler());
    }

    private CommonErrorHandler errorHandler() {
        DefaultErrorHandler handler = new DefaultErrorHandler(new FixedBackOff(1000, 3));
        handler.addNotRetryableExceptions(IllegalStateException.class);
        handler.setRetryListeners((record, ex, deliveryAttempt) -> {
            log.error(" RetryListeners message = {}, offset = {} deliveryAttempt = {}", ex.getMessage(), record.offset(), deliveryAttempt);
        });
        return handler;
    }

    private Map<String, Object> commonProducerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false);
        return props;
    }

    @Bean
    public ProducerFactory<String, AccountDto> producerAccountFactory() {
        Map<String, Object> props = commonProducerProps();
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public ProducerFactory<String, TransactionDto> producerTransactionFactory() {
        Map<String, Object> props = commonProducerProps();
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public ProducerFactory<String, Message<DataSourceErrorLogDto>> producerDataSourceErrorLogFactory() {
        Map<String, Object> props = commonProducerProps();
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, AccountDto> accountKafkaTemplate(@Qualifier("producerAccountFactory") ProducerFactory<String, AccountDto> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public KafkaTemplate<String, TransactionDto> transactionKafkaTemplate(@Qualifier("producerTransactionFactory") ProducerFactory<String, TransactionDto> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public KafkaTemplate<String, Message<DataSourceErrorLogDto>> dataSourceErrorLogKafkaTemplate(@Qualifier("producerDataSourceErrorLogFactory") ProducerFactory<String, Message<DataSourceErrorLogDto>> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

}