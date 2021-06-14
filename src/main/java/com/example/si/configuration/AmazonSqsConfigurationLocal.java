package com.example.si.configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.model.PurgeQueueRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("aws-sqs-local")
public class AmazonSqsConfigurationLocal {

	@Autowired
	private AmazonConfigurationProperties config;

	@Bean
	public AwsClientBuilder.EndpointConfiguration endpointConfiguration(){

		final var endpoint = config.getLocalStack().getEndpoint().toString();
		final var region = config.getRegion();

		return new AwsClientBuilder.EndpointConfiguration(endpoint, region);
	}

	@Bean
	@Primary
	public AmazonSQSAsync amazonSQSAsync(final AwsClientBuilder.EndpointConfiguration endpointConfiguration){

		final var credentials = config.getCredentials();
		final var stack = config.getLocalStack();

		final AmazonSQSAsync sqs = AmazonSQSAsyncClientBuilder
				.standard()
				.withEndpointConfiguration(endpointConfiguration)
				.withCredentials(
						new AWSStaticCredentialsProvider(
								new BasicAWSCredentials(credentials.getAccessKey(),
										credentials.getSecretKey())))
				.build();

		stack.getSqsQueues().forEach( queue -> create(sqs, queue));

		return sqs;

	}

	private void create(final AmazonSQSAsync sqs, final String queue) {
		sqs.createQueue(queue);
		final var queueUrl = sqs.getQueueUrl(queue).getQueueUrl();
		sqs.purgeQueueAsync(new PurgeQueueRequest(queueUrl));
	}

}
