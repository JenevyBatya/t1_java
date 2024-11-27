package ru.t1.java.demo.dto;
//clientId, accountId, transactionId, timestamp, transaction.amount, account.balance

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionInfoDto {
    @JsonProperty("client_id")
    private Long clientId;

    @JsonProperty("account_id")
    private Long accountId;
    @JsonProperty("transaction_id")
    private Long transactionId;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("transaction_amount")
    private Double transactionAmount;

    @JsonProperty("account_balance")
    private Double accountBalance;

}
