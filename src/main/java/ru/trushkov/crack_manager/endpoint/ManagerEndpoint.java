package ru.trushkov.crack_manager.endpoint;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import ru.nsu.ccfit.schema.crack_hash_response.CrackHashWorkerResponse;

@Endpoint
public class ManagerEndpoint {

    @PayloadRoot(namespace = "http://ccfit.nsu.ru/schema/crack-hash-response", localPart = "CrackHashWorkerResponse")
    public void getCountry(@RequestPayload CrackHashWorkerResponse response) {
        System.out.println(response.getRequestId());
        System.out.println(response.getAnswers().getWords());
        System.out.println(response.getPartNumber());
       // requests.get(response.getRequestId()).getData().addAll(response.getAnswers().getWords());
    }

}
