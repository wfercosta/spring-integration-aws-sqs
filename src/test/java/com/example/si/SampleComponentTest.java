package com.example.si;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.channel.interceptor.WireTap;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.MatcherAssert.assertThat;

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

	@Autowired
	@Qualifier("failure.input")
	private DirectChannel failureChannel;

	@Test
	public void test () throws JsonProcessingException {

		//Arrange
		final Sample sample = new Sample("5492458a-0685-46bb-96b6-7ee30d60b243");
		final Organisation organisation = new Organisation("5492458a-0685-46bb-96b6-7ee30d60b243", "A Name");
		final QueueChannel wireTapChannel = MessageChannels.queue().get();

		server.stubFor(get(urlMatching(REQUEST_URL_GET_ORG_BY_ID))
						.willReturn(aResponse()
								.withStatus(HttpStatus.BAD_REQUEST.value())
								.withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
								.withBody(mapper.writeValueAsString(organisation))));


		failureChannel.addInterceptor(new WireTap(wireTapChannel));

		//Act
		sut.receive(sample);


		//Assertions
		Message<?> receive = wireTapChannel.receive();
		assertThat(receive, Matchers.instanceOf(ErrorMessage.class));
		server.verify(REQUESTED_ONCE, getRequestedFor(urlMatching(REQUEST_URL_GET_ORG_BY_ID)));
	}

}
