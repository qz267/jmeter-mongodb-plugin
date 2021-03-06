/*!
 * AtlantBH Custom Jmeter Components v1.0.0
 * http://www.atlantbh.com/jmeter-components/
 *
 * Copyright 2011, AtlantBH
 *
 * Licensed under the under the Apache License, Version 2.0.
 */

package com.jolestar.jmeter.jsonpathassertion;

import java.io.Serializable;
import java.text.ParseException;
import org.apache.jmeter.assertions.*;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.testelement.AbstractTestElement;
import com.jayway.jsonpath.JsonPath;

/**
 * This is main class for JSONPath Assertion which verifies assertion on previous sample result
 * using JSON path expression
 * 
 * @author Semir Sabic / AtlantBH
 */
public class JSONPathAssertion extends AbstractTestElement implements Serializable, Assertion,TestBean {

	private static final long serialVersionUID = 1L;
	
	static final String JSONPATH = "jsonPath";
	static final String EXPECTEDVALUE = "expectedValue";
	static final String JSONVALIDATION = "jsonValidation";

	public String getJsonPath() {
		return getPropertyAsString(JSONPATH);
	}
	
	public void setJsonPath(String jsonPath) {
		setProperty(JSONPATH, jsonPath);
	}
	
	
	public void setJsonValidation(boolean jsonValidation) {
		setProperty(JSONVALIDATION, jsonValidation);
	}
	
	public boolean isJsonValidation() {
		return getPropertyAsBoolean(JSONVALIDATION);
	}
	
	public String getExpectedValue() {
		return getPropertyAsString(EXPECTEDVALUE);
	}
	
	public void setExpectedValue(String expectedValue) {
		setProperty(EXPECTEDVALUE, expectedValue);
	}
	
	public boolean checkJSONPathWithoutValidation(String jsonString, String jsonPath) throws Exception {
		String jsonPathResult = "";
		
		jsonPathResult = JsonPath.read(jsonString, jsonPath).toString();
		
		if("".equalsIgnoreCase(jsonPath)){
			throw new Exception("JSON path is is empty!");
		} else if("".equalsIgnoreCase(jsonPathResult)){
			throw new Exception("Incorrect JSON path");
		} else {
			return true;
		}
	}
	
	public boolean checkJSONPathWithValidation(String jsonString, String jsonPath, String expectedValue) throws Exception {
		if("".equalsIgnoreCase(jsonPath) || "".equalsIgnoreCase(expectedValue)){
			throw new Exception("JSON path or expected value is empty!");
		}
		
		String actualValue = JsonPath.read(jsonString, jsonPath).toString();
		if (expectedValue.equalsIgnoreCase(actualValue)) {
			return true;
		} else {
			String message = "Response doesn't contain expected value. \n" +
			"\texpected value: '" + expectedValue + "'\n" + 
			"\tactual value:   '" + actualValue + "'\n";
			throw new Exception(message);
		}
	}
	
	@Override
	public AssertionResult getResult(SampleResult samplerResult) 
	{
		AssertionResult result = new AssertionResult(getName());
		byte[] responseData = samplerResult.getResponseData();
		if (responseData.length == 0) {
			return result.setResultForNull();
		}
	
		if (isJsonValidation())
		{
			try 
			{
				if (checkJSONPathWithValidation(new String(responseData,"UTF-8"), getJsonPath(), getExpectedValue())) {
					result.setFailure(false);
					result.setFailureMessage("");
				}
			} 
			catch (ParseException e)
			{
				result.setFailure(true);
				result.setFailureMessage(e.getClass().toString() + " - " + e.getMessage());
			}
			catch (Exception e) 
			{
				result.setFailure(true);
				result.setFailureMessage(e.getMessage());
			}
		}
		
		if (!isJsonValidation())
		{
			try 
			{
				if (checkJSONPathWithoutValidation(new String(responseData), getJsonPath())) {
					result.setFailure(false);
					result.setFailureMessage("");
				}		
			} 
			catch (ParseException e) 
			{
				result.setFailure(true);
				result.setFailureMessage(e.getClass().toString() + " - " + e.getMessage());
			} 
			catch (Exception e) 
			{
				result.setFailure(true);
				result.setFailureMessage(e.getMessage());
			}
		}	
		
		return result;
	}	
}