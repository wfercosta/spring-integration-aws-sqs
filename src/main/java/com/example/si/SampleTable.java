package com.example.si;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@DynamoDBTable(tableName = "sample")
@NoArgsConstructor
@AllArgsConstructor
public class SampleTable {

	@DynamoDBHashKey(attributeName = "organisation")
	private String organisation;

	@DynamoDBAttribute(attributeName = "hash")
	private String hash;
}
