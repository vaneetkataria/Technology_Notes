package com.dhisco.product.discovery.testcases;

import java.nio.charset.Charset;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.Test;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.dsl.testng.TestNGCitrusTestRunner;
import com.consol.citrus.kafka.endpoint.KafkaEndpoint;
import com.consol.citrus.kafka.message.KafkaMessage;
import com.consol.citrus.message.MessageType;

@Test
public class CountAndValidateShoppingMessagesIT extends TestNGCitrusTestRunner {

	@Autowired
	private KafkaEndpoint propertyStateKafkaEndPoint;

	@CitrusTest(name = "countAndValidateShoppingMessages")
	public void countAndValidateShoppingMessages() {

		RestTemplate restTemplate = new RestTemplate();
		FormHttpMessageConverter formConverter = new FormHttpMessageConverter();
		formConverter.setCharset(Charset.forName("UTF8"));
		restTemplate.getMessageConverters().add(formConverter);
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setAccept(Collections.singletonList(MediaType.parseMediaType("application/json")));
		httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("file", new FileSystemResource("src/test/resources/testcases/csv/Data_Dump_File_Prod_Rate_IT.csv"));
		parts.add("maxlos", 7);
		parts.add("maxnrcs", 15);
		parts.add("maxocc", 2);
		parts.add("maxrooms", 2);
		parts.add("sga", "MT");
		parts.add("startDate", "2020-07-10");
		parts.add("supplier", "IHG");

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(parts, httpHeaders);

		new Thread(() -> restTemplate.postForEntity("http://localhost:8087/api/batchfile/ihg", requestEntity,
				String.class)).start();

		// Test {\"state\":0,\"shopBatchSize\":null} is pushed to
		// product.discovery.property.state topic.
		receive(receiveMessageBuilder -> receiveMessageBuilder.endpoint(propertyStateKafkaEndPoint)
				.messageType(MessageType.JSON).message(new KafkaMessage("{\"state\":0,\"shopBatchSize\":null}")));

		// Similariy Next Messages can also be tested .

	}

}
