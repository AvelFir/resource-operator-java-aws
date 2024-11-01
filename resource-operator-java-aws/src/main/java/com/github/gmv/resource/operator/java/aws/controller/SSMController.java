package com.github.gmv.resource.operator.java.aws.controller;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.model.GetParametersByPathRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParametersByPathResult;
import com.amazonaws.services.simplesystemsmanagement.model.PutParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.PutParameterResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ssm")
public class SSMController {

    private final AWSSimpleSystemsManagement ssmClient;

    public SSMController(final AWSSimpleSystemsManagement client) {
        this.ssmClient = client;
    }

    @GetMapping("/parameters")
    public GetParametersByPathResult getByPath(
            @RequestParam final String path
    ) {
        GetParametersByPathRequest request = new GetParametersByPathRequest()
                .withPath(path)
                .withRecursive(true)
                .withWithDecryption(true);
        return ssmClient.getParametersByPath(request);
    }

    @PutMapping("/parameters")
    public PutParameterResult update(
            @RequestParam final String name,
            @RequestParam final String value
    ) {
        PutParameterRequest request = new PutParameterRequest()
                .withName(name)
                .withValue(value)
                .withOverwrite(true);
        return ssmClient.putParameter(request);
    }


}
