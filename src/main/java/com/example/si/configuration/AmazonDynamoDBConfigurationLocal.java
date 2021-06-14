package com.example.si.configuration;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.example.si.SampleTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AmazonDynamoDBConfigurationLocal {

	@Autowired
	private AmazonConfigurationProperties config;

	@Autowired
	private ApplicationContext context;

	@Bean
	public AmazonDynamoDB amazonDynamoDB() {
		final var endpoint = config.getLocalStack().getEndpoint().toString();
		final var region = config.getRegion();

		return AmazonDynamoDBClientBuilder
				.standard()
				.withEndpointConfiguration(
						new AwsClientBuilder.EndpointConfiguration(endpoint,region))
				.build();
	}

	@Bean
	public DynamoDBMapper dynamoDBMapper(final AmazonDynamoDB client, ApplicationContext context) {
		final var mapper = new DynamoDBMapper(client);
		final var current = client.listTables();
		final var tables = List.of(SampleTable.class);

		tables.forEach( table -> {
			final var annotation = table.getAnnotation(DynamoDBTable.class);

			if (!current.getTableNames().contains(annotation.tableName())) {
				final var request = mapper.generateCreateTableRequest(table);
				request.setProvisionedThroughput(new ProvisionedThroughput(5L, 5L));
				client.createTable(request);
			}
		});

		return mapper;
	}

}
