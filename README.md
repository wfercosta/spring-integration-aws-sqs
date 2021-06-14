# spring-integration-aws-sqs


```
aws configure --profile localstack
```

```
aws --endpoint="http://localhost:4566" --region=sa-east-1 sqs send-message --queue-url http://localhost:4566/000000000000/recebimento-bacen --message-body '{
  "id": "5492458a-0685-46bb-96b6-7ee30d60b243"
}'
```

```
aws --endpoint="http://localhost:4566" --region=sa-east-1 dynamodb list-tables
```

https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.DownloadingAndRunning.html