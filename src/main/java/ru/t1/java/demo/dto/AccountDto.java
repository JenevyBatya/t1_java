package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.t1.java.demo.model.enums.AccountStatus;
import ru.t1.java.demo.model.enums.AccountType;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDto implements Serializable {
    private Long id;


    @JsonProperty("type")
    private AccountType type;

    @JsonProperty("balance")
    private Double balance;

    @JsonProperty("status")
    private AccountStatus status;

    @JsonProperty("frozen_amount")
    private Double frozenAmount;

    @JsonProperty("client_id")
    private Long clientId;
}
