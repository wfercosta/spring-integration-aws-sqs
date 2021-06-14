package com.example.si.configuration;


import com.amazonaws.regions.Regions;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "cloud.aws")
public class AmazonConfigurationProperties {

	private LocalStack localStack;
	private Credentials credentials;

	@Value("${cloud.aws.static.region}")
	private String region;

	@Data
	public static class LocalStack {
		private URL endpoint;
		private List<String> sqsQueues = new ArrayList<>();
	}

	@Data
	public static class Credentials {
		private String accessKey;
		private String secretKey;
	}

}
