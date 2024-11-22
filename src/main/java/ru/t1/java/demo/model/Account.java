package ru.t1.java.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;
import ru.t1.java.demo.model.enums.AccountStatus;
import ru.t1.java.demo.model.enums.AccountType;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account")
public class Account extends AbstractPersistable<Long> {

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType type;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "balance", nullable = false)
    private Double balance;

    @Column(name = "frozen_amount")
    private Double frozenAmount;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
}
