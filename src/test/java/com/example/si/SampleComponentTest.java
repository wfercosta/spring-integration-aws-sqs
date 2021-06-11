package com.example.si;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(
		initializers = {WireMockInitializer.class}
)
@Tag(TestType.COMPONENT)
public class SampleComponentTest {

	public static final String PATTERN_UUID = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
	public static final String REQUEST_URL_GET_ORG_BY_ID = "/organisations/" + PATTERN_UUID;
	public static final int REQUESTED_ONCE = 1;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private WireMockServer server;

	@Autowired
	private SampleSqsListener sut;

	@Test
	public void test () throws JsonProcessingException {

		//Arrange
		final Sample sample = new Sample("5492458a-0685-46bb-96b6-7ee30d60b243");
		final Organisation organisation = new Organisation("5492458a-0685-46bb-96b6-7ee30d60b243", "A Name");

		server.stubFor(get(urlMatching(REQUEST_URL_GET_ORG_BY_ID))
						.willReturn(aResponse()
								.withStatus(HttpStatus.CREATED.value())
								.withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
								.withBody(mapper.writeValueAsString(organisation))));

		//Act
		sut.receive(sample);

		//Assertions
		server.verify(REQUESTED_ONCE, getRequestedFor(urlMatching(REQUEST_URL_GET_ORG_BY_ID)));
	}

}
