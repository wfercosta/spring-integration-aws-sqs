package com.example.si;

import org.aopalliance.aop.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.handler.advice.ExpressionEvaluatingRequestHandlerAdvice;
import org.springframework.integration.http.dsl.Http;

@Configuration
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
						.expectedResponseType(Organisation.class), e -> e.advice(advice()));
	}

	@Bean
	public Advice advice() {
		ExpressionEvaluatingRequestHandlerAdvice handler = new ExpressionEvaluatingRequestHandlerAdvice();
		handler.setFailureChannelName("failure.input");
		handler.setSuccessChannelName("success.input");
		handler.setTrapException(true);
		return handler;
	}

	@Bean
	public QueueChannel queueChannel() {
		return MessageChannels.queue().get();
	}

	@Bean
	public IntegrationFlow success() {
		return f -> f
				.log(LoggingHandler.Level.INFO, message -> "Success Channel --> " + message)
				.filter(service::verify, consumer -> consumer.discardChannel("discard.input"))
				.log(LoggingHandler.Level.INFO, message -> "Basic Flow --> " + message);
	}

	@Bean
	public IntegrationFlow discard() {
		return f -> f.log(LoggingHandler.Level.INFO, message -> "Final Output --> " + message);
	}

	@Bean
	public IntegrationFlow failure() {
		return f -> f
				.log(LoggingHandler.Level.INFO, message -> "Failure Channel --> " + message)
				.wireTap( c -> c.channel(queueChannel()))
				.log(LoggingHandler.Level.INFO, message -> "closed Channel --> " + message);
	}

}
