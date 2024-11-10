package ru.t1.java.demo.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.service.TransactionService;

import java.util.List;

@RestController
@RequestMapping("/transaction")
@AllArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping
    public List<TransactionDto> getAllTransactions() {
        return transactionService.findAll();
    }

    @PostMapping
    public TransactionDto createTransaction(@RequestBody TransactionDto dto) {
        return transactionService.save(dto);
    }
}
