package com.example.si;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.model.PurgeQueueRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import io.awspring.cloud.messaging.config.QueueMessageHandlerFactory;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.annotation.support.PayloadArgumentResolver;

import java.time.ZonedDateTime;
import java.util.Collections;

@Configuration
public class LocalStackConfiguration {

	@Value("${aws.local.endpoint}")
	private String endpoint;

	@Value("${cloud.aws.static.region}")
	private String region;

	@Value("${cloud.aws.credentials.access-key}")
	private String awsAccesskey;

	@Value("${cloud.aws.credentials.secret-key}")
	private String awsSecretKey;

	@Bean
	public QueueMessageHandlerFactory queueMessageHandlerFactory(MessageConverter messageConverter) {

		var factory = new QueueMessageHandlerFactory();
		factory.setArgumentResolvers(Collections.singletonList(new PayloadArgumentResolver(messageConverter)));
		return factory;
	}

	@Bean
	protected MessageConverter messageConverter(ObjectMapper objectMapper) {

		var converter = new MappingJackson2MessageConverter();
		converter.setObjectMapper(objectMapper);
		// Serialization support:
		converter.setSerializedPayloadClass(String.class);
		// Deserialization support: (suppress "contentType=application/json" header requirement)
		converter.setStrictContentTypeMatch(false);
		return converter;
	}

	@Bean
	public QueueMessagingTemplate queueMessagingTemplate(AmazonSQSAsync amazonSQSAsync){
		return new QueueMessagingTemplate(amazonSQSAsync);
	}

	@Bean
	public AwsClientBuilder.EndpointConfiguration endpointConfiguration(){
		return new AwsClientBuilder.EndpointConfiguration(endpoint, region);
	}

	@Bean
	@Primary
	public AmazonSQSAsync amazonSQSAsync(final AwsClientBuilder.EndpointConfiguration endpointConfiguration){
		AmazonSQSAsync amazonSQSAsync = AmazonSQSAsyncClientBuilder
				.standard()
				.withEndpointConfiguration(endpointConfiguration)
				.withCredentials(new AWSStaticCredentialsProvider(
						new BasicAWSCredentials(awsAccesskey, awsSecretKey)
				))
				.build();
//		createQueues(amazonSQSAsync, "incoming-queue");
//		createQueues(amazonSQSAsync, "outgoing-queue");
		return amazonSQSAsync;
	}
//
//	private void createQueues(final AmazonSQSAsync amazonSQSAsync,
//							  final String queueName){
//		amazonSQSAsync.createQueue(queueName);
//		var queueUrl = amazonSQSAsync.getQueueUrl(queueName).getQueueUrl();
//		amazonSQSAsync.purgeQueueAsync(new PurgeQueueRequest(queueUrl));
//	}

//	@Bean
//	@Primary
//	public static ObjectMapper jsonMapper() {
//		var mapper = new ObjectMapper();
//		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//		mapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true);
//		mapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
//		JavaTimeModule javaTimeModule = new JavaTimeModule();
//		javaTimeModule.addDeserializer(ZonedDateTime.class, InstantDeserializer.ZONED_DATE_TIME);
//		mapper.registerModule(javaTimeModule);
//		return mapper;
//	}
}
