package com.example.si;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(DynamoDBExtension.class)
@SpringBootTest
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
}
