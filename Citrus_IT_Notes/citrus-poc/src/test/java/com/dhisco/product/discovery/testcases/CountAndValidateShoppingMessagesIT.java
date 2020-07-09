package com.dhisco.product.discovery.testcases;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testng.annotations.Test;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.dsl.endpoint.CitrusEndpoints;
import com.consol.citrus.dsl.testng.TestNGCitrusTestRunner;
import com.consol.citrus.http.client.HttpClient;

@Test
public class CountAndValidateShoppingMessagesIT extends TestNGCitrusTestRunner {

	@Autowired
	private HttpClient pdHttpClient;

	@CitrusTest(name = "countAndValidateShoppingMessages")
	public void countAndValidateShoppingMessages() {

		variable("file", new ClassPathResource("src/test/resources/citrus-application.properties"));

		http(httpActionBuilder -> httpActionBuilder.client(pdHttpClient).send().post("/api/batchfile/ihg").fork(true)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE).payload(
						"matchingQualifier=REQUESTED_AND_PUBLIC&maxlos=7&maxnrcs=15&maxocc=2&maxrooms=2&sga=MT&startDate=2020-07-10&supplier=IHG&file=${file}"));
	}

}
