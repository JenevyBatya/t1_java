package ru.t1.java.demo.util;

import com.github.javafaker.Faker;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.enums.AccountStatus;
import ru.t1.java.demo.model.enums.AccountType;
import ru.t1.java.demo.model.enums.TransactionStatus;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.service.TransactionService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@Component
public class DataGenerator {
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final ClientService clientService;
    private final Faker faker = new Faker();
    private final Random random = new Random();

    @PostConstruct
    public void generateData() {
        List<AccountDto> accounts = new ArrayList<>();
        List<ClientDto> clients = clientsRegistration();
        for (ClientDto client : clients) {
            for (int i = 0; i <= 3; i++) {
                AccountDto accountDto = AccountDto.builder()
                        .clientId(client.getId())
                        .type(random.nextBoolean() ? AccountType.CREDIT : AccountType.DEBIT)
                        .balance(faker.number().randomDouble(2, 100, 10000))
                        .frozenAmount(faker.number().randomDouble(2, 100, 10000))
                        .status(AccountStatus.OPEN)
                        .build();
                accounts.add(accountService.save(accountDto));
            }
        }


        for (AccountDto dto : accounts) {
            for (int j = 0; j <= 5; j++) {
                TransactionDto transactionDto = TransactionDto.builder()
                        .accountId(dto.getId())
                        .amount(faker.number().randomDouble(2, 1, 1000))
//                        .time(faker.date().past(10, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                        .time(LocalDateTime.now())
                        .status(TransactionStatus.ACCECPTED)
                        .build();
                System.out.println();

                transactionService.sendTransaction(transactionDto);
            }
        }

    }

    private List<ClientDto> clientsRegistration() {
        String[] firsts = {"Joshua", "Carl", "Jojo"};
        String[] middles = {"Boba", "Choiks", "Tarantino"};
        String[] lasts = {"Pit", "Poppy", "Mao"};
        List<ClientDto> clients = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ClientDto clientDto = ClientDto.builder()
                    .firstName(firsts[i])
                    .middleName(middles[i])
                    .lastName(lasts[i])
                    .build();
            clients.add(clientService.save(clientDto));
        }
        return clients;
    }

}
