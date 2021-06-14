package com.example.si;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@DynamoDBTable(tableName = "sample")
public class SampleTable {

	@DynamoDBHashKey(attributeName = "organisation")
	private String organisation;

	@DynamoDBAttribute(attributeName = "hash")
	private String hash;
}
