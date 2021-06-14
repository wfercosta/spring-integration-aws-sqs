package com.example.si;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(DynamoDBExtension.class)
@SpringBootTest
@Profile("test")
public class SampleRepositoryTest {

	@Autowired
	private AmazonDynamoDB client;

	@Autowired
	private DynamoDBMapper mapper;

	@Test
	public void test() {
		assertThat(client, notNullValue());
		assertThat(mapper, notNullValue());
	}

	@BeforeAll
	public static void beforeAll() {

	}

	@Test
	@DisplayName("Should write to persistence")
	public void Should_WriteToPersistence() {

		final String id = UUID.randomUUID().toString();
		final SampleTable sample = SampleTable.builder().organisation(id)
				.hash(UUID.randomUUID().toString())
				.build();


		mapper.save(sample);


		final List<SampleTable> results = mapper.query(SampleTable.class,
				new DynamoDBQueryExpression<SampleTable>()
						.withConsistentRead(false)
						.withExpressionAttributeValues(
								Map.of(":id", new AttributeValue().withS(id))
						)
						.withKeyConditionExpression("organisation = :id"));

		assertThat(results.size(), equalTo(1) );

		mapper.delete(sample);

	}
}
