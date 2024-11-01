package com.github.gmv.resource.operator.java.aws.controller;

import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.model.DescribeTasksRequest;
import com.amazonaws.services.ecs.model.DescribeTasksResult;
import com.amazonaws.services.ecs.model.ListTasksRequest;
import com.amazonaws.services.ecs.model.ListTasksResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ecs")
//TODO estudar implementaçao TASK vs Service
public class ECSController {

    private final AmazonECS ecsClient;

    public ECSController(final AmazonECS client) {
        this.ecsClient = client;
    }

    @GetMapping("{cluster}/tasks")
    public ListTasksResult listAllTasks(@PathVariable String cluster, @RequestParam(required = false) String taskPrefix) {
        ListTasksRequest request = new ListTasksRequest()
                .withCluster(cluster)
                .withStartedBy(taskPrefix);
        return ecsClient.listTasks(request);
    }


    @GetMapping("{cluster}/details/tasks/")
    public DescribeTasksResult getTaskDetails(@PathVariable String cluster, @RequestBody List<String> tasks) {
        DescribeTasksRequest request = new DescribeTasksRequest().withCluster(cluster).withTasks(tasks);
        return ecsClient.describeTasks(request);
    }

}
