package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.Track;
import ru.t1.java.demo.aop.HandlingResult;
import ru.t1.java.demo.aop.LogExecution;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.util.AccountMapper;
import ru.t1.java.demo.util.ClientMapper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository repository;

    @Override
    public List<ClientDto> findAll() {
        List<Client> accounts = repository.findAll();

        return accounts.stream()
                .map(ClientMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ClientDto save(ClientDto dto) {
        Client client = repository.save(ClientMapper.toEntity(dto));
//        kafkaProducer.sendTo("t1_demo_accounts", dto);
        return ClientMapper.toDto(client);
    }

    @Override
    public ClientDto findById(Long id) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }

//    @PostConstruct
//    void init() {
//        try {
//            List<Client> clients = parseJson();
//        } catch (IOException e) {
//            log.error("Ошибка во время обработки записей", e);
//        }
////        repository.saveAll(clients);
//    }
//
//    @Override
////    @LogExecution
////    @Track
////    @HandlingResult
//    public List<Client> parseJson() throws IOException {
//        ObjectMapper mapper = new ObjectMapper();
//
//        ClientDto[] clients = mapper.readValue(new File("src/main/resources/MOCK_DATA.json"), ClientDto[].class);
//
//        return Arrays.stream(clients)
//                .map(ClientMapper::toEntity)
//                .collect(Collectors.toList());
//    }

}
