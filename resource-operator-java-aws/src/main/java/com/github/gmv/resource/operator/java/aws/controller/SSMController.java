package com.github.gmv.resource.operator.java.aws.controller;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.model.*;
import com.github.gmv.resource.operator.java.aws.domain.ParameterUpdateRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ssm")
public class SSMController {

    private final AWSSimpleSystemsManagement ssmClient;

    public SSMController(final AWSSimpleSystemsManagement client) {
        this.ssmClient = client;
    }

    @GetMapping(value = "/parameters", params = "path")
    public GetParametersByPathResult getByPath(
            @RequestParam final String path
    ) {
        GetParametersByPathRequest request = new GetParametersByPathRequest()
                .withPath(path)
                .withRecursive(true)
                .withWithDecryption(true);
        return ssmClient.getParametersByPath(request);
    }

    @GetMapping(value = "/parameters", params = "name")
    public GetParameterResult getByName(
            @RequestParam final String name
    ) {
        GetParameterRequest request = new GetParameterRequest()
                .withName(name)
                .withWithDecryption(true);
        return ssmClient.getParameter(request);
    }

    @PutMapping("/parameters")
    public PutParameterResult update(
            @RequestBody ParameterUpdateRequest updateRequest
    ) {
        PutParameterRequest request = new PutParameterRequest()
                .withName(updateRequest.getName())
                .withValue(updateRequest.getValue())
                .withOverwrite(true);
        return ssmClient.putParameter(request);
    }


}
