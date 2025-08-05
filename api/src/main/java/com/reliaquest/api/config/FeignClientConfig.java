package com.reliaquest.api.config;

import feign.Logger;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuration class for Feign client settings with Resilience4j integration.
 */
@Configuration
public class FeignClientConfig {


    @Value("${spring.cloud.openfeign.client.config.default.retry.period:200}")
    private int period;

    @Value("${spring.cloud.openfeign.client.config.default.retry.maxPeriod:2000}")
    private long maxPeriod;

    @Value("${spring.cloud.openfeign.client.config.default.retry.maxAttempts:4}")
    private int maxAttempts;

    /**
     * Configures the error decoder for Feign clients.
     *
     * @return Custom error decoder instance
     */
    @Bean
    public static ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }

    @Bean
    public Retryer feignRetryer() {

        return new Retryer.Default(
                period,                    // initial interval
                maxPeriod,  // max interval
                maxAttempts                       // max attempts (1 initial + 3 retries)
        );
    }

    /**
     * Configures the logging level for Feign clients.
     *
     * @return Logger.Level.FULL for detailed request/response logging
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }


}
