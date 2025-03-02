package ru.trushkov.crack_manager.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import ru.nsu.ccfit.schema.crack_hash_request.CrackHashManagerRequest;
import ru.nsu.ccfit.schema.crack_hash_response.CrackHashWorkerResponse;
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
    private final String resourceUrl1 = "http://localhost:8081/internal/api/worker/hash/crack/task";
    private final String resourceUrl2 = "http://localhost:34579/internal/api/worker/hash/crack/task";
    private final String resourceUrl3 = "http://localhost:42461/internal/api/worker/hash/crack/task";

    public String crackPassword(CrackPasswordDto crackPasswordDto) {
        String requestId = UUID.randomUUID().toString();
        addNewRequest(requestId);
        doRequests(crackPasswordDto, requestId);
        return requestId;
    }

    public PasswordDto getPasswords(String requestId) {
        PasswordRequest passwordRequest = requests.get(requestId);
        PasswordDto passwordDto = PasswordDto.builder().data(passwordRequest.getData())
                .status(passwordRequest.getStatus()).build();
        return passwordDto;
    }

    private void addNewRequest(String requestId) {
        requests.put(requestId, PasswordRequest.builder().status(READY).data(new CopyOnWriteArrayList<>())
                .successWork(0).build());
    }

    private void doRequests(CrackPasswordDto crackPasswordDto, String requestId) {
        doRequest(crackPasswordDto, 0, 3, resourceUrl1, requestId);
        //doRequest(crackPasswordDto, 1, 3, resourceUrl2, requestId);
        //doRequest(crackPasswordDto, 2, 3, resourceUrl3, requestId);
    }

    private void doRequest(CrackPasswordDto crackPasswordDto, Integer number, Integer count, String url, String requestId) {
        RestTemplate restTemplate = new RestTemplate();
        CrackHashManagerRequest crackHashManagerRequest = createCrackHashManagerRequest(crackPasswordDto, number, count, requestId);
        System.out.println(crackHashManagerRequest.getAlphabet().getSymbols());
        System.out.println(crackHashManagerRequest.getRequestId());
        System.out.println(crackHashManagerRequest.getHash());
        System.out.println(crackHashManagerRequest.getAlphabet().getSymbols());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<CrackHashManagerRequest> request = new HttpEntity<>(crackHashManagerRequest, headers);
        Runnable runnable = () -> {
            restTemplate.postForObject(url, request, CrackHashManagerRequest.class);
        };
        Thread requestThread = new Thread(runnable);
        requestThread.start();
    }

    private CrackHashManagerRequest createCrackHashManagerRequest(CrackPasswordDto crackPasswordDto, Integer number, Integer count, String requestId) {
        CrackHashManagerRequest crackHashManagerRequest = new CrackHashManagerRequest();
        crackHashManagerRequest.setHash(crackPasswordDto.getHash());
        crackHashManagerRequest.setRequestId(requestId);
        CrackHashManagerRequest.Alphabet alphabet = new CrackHashManagerRequest.Alphabet();
        alphabet.getSymbols().addAll(symbolsOfAlphabet);
        crackHashManagerRequest.setAlphabet(alphabet);
        crackHashManagerRequest.setMaxLength(crackPasswordDto.getLength());
        crackHashManagerRequest.setPartNumber(number);
        crackHashManagerRequest.setPartCount(count);
        return crackHashManagerRequest;
    }

    public void changeRequest(CrackHashWorkerResponse response) {
        String requestId = response.getRequestId();
        requests.get(requestId).getData().addAll(response.getAnswers().getWords());
        requests.get(requestId).setSuccessWork(requests.get(requestId).getSuccessWork() + 1);
        if (requests.get(requestId).getSuccessWork() == 3) {
            requests.get(requestId).setStatus(READY);
        }
    }
}
