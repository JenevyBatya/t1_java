package ru.t1.java.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;
import ru.t1.java.demo.model.enums.TransactionStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction")
public class Transaction extends AbstractPersistable<Long> {

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "time", nullable = false)
    private LocalDateTime time;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

}
