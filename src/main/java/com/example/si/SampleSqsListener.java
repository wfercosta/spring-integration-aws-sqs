package com.example.si;

import io.awspring.cloud.messaging.listener.SqsMessageDeletionPolicy;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SampleSqsListener {

	private final Logger LOG = LoggerFactory.getLogger(SampleSqsListener.class);

	private SampleSqsGateway gateway;

	public SampleSqsListener(SampleSqsGateway gateway) {
		this.gateway = gateway;
	}

	@SqsListener(value="${application.aws.queue}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
	public void receive(Sample sample) {
		gateway.process(sample);
	}

}
