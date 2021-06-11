package com.example.si;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface SampleSqsGateway {

	@Gateway(requestChannel = "sample.input")
	void process(Sample sample);
}
