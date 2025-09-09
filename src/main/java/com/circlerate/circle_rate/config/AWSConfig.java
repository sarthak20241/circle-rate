package com.circlerate.circle_rate.config;

import io.awspring.cloud.autoconfigure.core.AwsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class AWSConfig {
    @Bean
    public S3Presigner s3Presigner(AwsProperties awsProperties) {
        return S3Presigner.create();
    }
}
