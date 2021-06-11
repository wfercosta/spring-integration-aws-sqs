package com.example.si;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.http.dsl.Http;
import org.springframework.stereotype.Component;

@Component
public class SampleSqsFlow {

	@Autowired
	private SampleService service;

	@Bean
	public IntegrationFlow sample() {
		return f -> f
				.log(LoggingHandler.Level.INFO, message -> message)
				.handle(Http
						.<Sample>outboundGateway(message -> "http://localhost:9561/organisations/" + message.getPayload().getId())
						.httpMethod(HttpMethod.GET)
						.expectedResponseType(Organisation.class))
				.log(LoggingHandler.Level.INFO, message -> message)
				.filter(service::verify, consumer -> consumer.discardChannel("output.input"))
				.log(LoggingHandler.Level.INFO, message -> "Basic Flow --> " + message);
	}

	@Bean
	public IntegrationFlow output() {
		return f -> f.log(LoggingHandler.Level.INFO, message -> "Discard Channel --> " + message);
	}



}
