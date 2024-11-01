package com.github.gmv.resource.operator.java.aws.infraestructure;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.retry.PredefinedRetryPolicies;
import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.AmazonECSClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AwsClientConfiguration {

    @Bean
    @Primary
    public AmazonSQS amazonSQS() {
        return AmazonSQSClientBuilder
                .standard()
                .withClientConfiguration(new ClientConfiguration()
                        .withRetryPolicy(PredefinedRetryPolicies.getDefaultRetryPolicy()))
                .withRegion(Regions.SA_EAST_1)
                .build();
    }

    @Bean
    @Primary
    public AWSSimpleSystemsManagement amazonSSM() {
        return AWSSimpleSystemsManagementClientBuilder
                .standard()
                .withClientConfiguration(new ClientConfiguration()
                        .withRetryPolicy(PredefinedRetryPolicies.getDefaultRetryPolicy()))
                .withRegion(Regions.SA_EAST_1)
                .build();
    }

    @Bean
    @Primary
    public AmazonECS amazonEcs() {
        return AmazonECSClientBuilder
                .standard()
                .withClientConfiguration(new ClientConfiguration()
                        .withRetryPolicy(PredefinedRetryPolicies.getDefaultRetryPolicy()))
                .withRegion(Regions.SA_EAST_1)
                .build();
    }

    @Bean
    @Primary
    public AmazonSNS amazonSNS() {
        return AmazonSNSClientBuilder
                .standard()
                .withClientConfiguration(new ClientConfiguration()
                        .withRetryPolicy(PredefinedRetryPolicies.getDefaultRetryPolicy()))
                .withRegion(Regions.SA_EAST_1)
                .build();
    }

}
