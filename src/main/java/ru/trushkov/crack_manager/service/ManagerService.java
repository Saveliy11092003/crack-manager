package ru.trushkov.crack_manager.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import ru.nsu.ccfit.schema.crack_hash_request.CrackHashManagerRequest;
import ru.trushkov.crack_manager.model.CrackPasswordDto;
import ru.trushkov.crack_manager.model.PasswordDto;
import ru.trushkov.crack_manager.model.PasswordRequest;
import ru.trushkov.crack_manager.model.enumeration.Status;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static ru.trushkov.crack_manager.model.enumeration.Status.READY;

@Service
public class ManagerService {

    @Value("${alphabet}")
    private List<String> symbolsOfAlphabet;

    private final ConcurrentHashMap<String, PasswordRequest> requests = new ConcurrentHashMap<>();
    private final RestTemplate restTemplate = new RestTemplate();
    private final String resourceUrl1 = "http://localhost:42185/internal/api/worker/hash/crack/task";
    private final String resourceUrl2 = "http://localhost:34579/internal/api/worker/hash/crack/task";
    private final String resourceUrl3 = "http://localhost:42461/internal/api/worker/hash/crack/task";

    public String crackPassword(CrackPasswordDto crackPasswordDto) {
        String requestId = UUID.randomUUID().toString();
        addNewRequest(requestId);
        doRequests(crackPasswordDto);
        return requestId;
    }

    public PasswordDto getPasswords(String requestId) {
        PasswordRequest passwordRequest = requests.get(requestId);
        PasswordDto passwordDto = PasswordDto.builder().data(passwordRequest.getData())
                .status(passwordRequest.getStatus()).build();
        return passwordDto;
    }

    private void addNewRequest(String requestId) {
        requests.put(requestId, PasswordRequest.builder().status(READY).build());
    }

    private void doRequests(CrackPasswordDto crackPasswordDto) {
        doRequest(crackPasswordDto, 0, 3, resourceUrl1);
        doRequest(crackPasswordDto, 1, 3, resourceUrl2);
        doRequest(crackPasswordDto, 2, 3, resourceUrl3);
    }

    private void doRequest(CrackPasswordDto crackPasswordDto, Integer number, Integer count, String url) {
        CrackHashManagerRequest crackHashManagerRequest = createCrackHashManagerRequest(crackPasswordDto, number, count);
        Runnable runnable = () -> restTemplate.postForObject(url, crackHashManagerRequest, CrackHashManagerRequest.class);
        Thread requestThread = new Thread(runnable);
        requestThread.start();
    }

    private CrackHashManagerRequest createCrackHashManagerRequest(CrackPasswordDto crackPasswordDto, Integer number, Integer count) {
        CrackHashManagerRequest crackHashManagerRequest = new CrackHashManagerRequest();
        crackHashManagerRequest.setHash(crackPasswordDto.getHash());
        String requestId = UUID.randomUUID().toString();
        crackHashManagerRequest.setRequestId(requestId);
        CrackHashManagerRequest.Alphabet alphabet = new CrackHashManagerRequest.Alphabet();
        alphabet.getSymbols().addAll(symbolsOfAlphabet);
        crackHashManagerRequest.setAlphabet(alphabet);
        crackHashManagerRequest.setMaxLength(crackPasswordDto.getLength());
        crackHashManagerRequest.setPartNumber(number);
        crackHashManagerRequest.setPartCount(count);
        return crackHashManagerRequest;
    }

}
