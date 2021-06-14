package com.example.si.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.messaging.config.QueueMessageHandlerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.annotation.support.PayloadMethodArgumentResolver;

import java.util.Collections;

@Configuration
@Profile("aws-sqs-common")
public class AmazonSqsConfigurationCommon {

	@Bean
	@Primary
	public MessageConverter messageConverter(final ObjectMapper objectMapper) {
		var converter = new MappingJackson2MessageConverter();

		converter.setObjectMapper(objectMapper);
		converter.setSerializedPayloadClass(String.class);
		converter.setStrictContentTypeMatch(false);

		return converter;
	}

	@Bean
	public QueueMessageHandlerFactory queueMessageHandlerFactory(final MessageConverter converter) {
		final var factory = new QueueMessageHandlerFactory();
		factory.setArgumentResolvers(Collections.singletonList(new PayloadMethodArgumentResolver(converter)));
		return factory;
	}

}
