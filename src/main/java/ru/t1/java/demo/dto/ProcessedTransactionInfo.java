package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.t1.java.demo.model.enums.TransactionStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessedTransactionInfo {
    @JsonProperty("transaction_id")
    private long transactionId;

    @JsonProperty("account_id")
    private long accountId;

    @JsonProperty("status")
    private TransactionStatus status;
}
