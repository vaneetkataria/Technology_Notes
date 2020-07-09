package com.dhisco.product.discovery.testcases;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.dsl.testng.TestNGCitrusTestRunner;

@Test
public class CountAndValidateShoppingMessagesIT extends TestNGCitrusTestRunner {

	@CitrusTest(name = "countAndValidateShoppingMessages")
	public void countAndValidateShoppingMessages() {
		System.out.println("Now called custom logging service");
	}

}
