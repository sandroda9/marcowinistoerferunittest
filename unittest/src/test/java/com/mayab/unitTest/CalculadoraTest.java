package com.mayab.unitTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CalculadoraTest {
	
	private Calculadora cal = null;
	
	@BeforeAll
	static void beforeAll() {
		System.out.println("Before all test cases");
	}
	
	@AfterAll
	static void afterAll() {
		System.out.println("After all test cases");
	}
	
	@BeforeEach
	void setup() {
		System.out.println("Before each test");
		cal = new Calculadora();
	}
	
	@AfterEach
	void cleanup() {
		System.out.println("Testing after each");
	}
	
	@Test
	void test2() { 
		System.out.println("Hola testcase2");
	}
	
	@Test
	void testSuma() {
		//Setup

		double num1 = 10;
		double num2 = 5;
		double expResult = 15;
		//Exercise
		double result = cal.suma(num2, num1);
		
		//Assertion
		assertThat(result, is(expResult));
	}
	
	@Test
	void testSubs() {
		//Setup
		Calculadora cal = new Calculadora();
		double num1 = 10;
		double num2 = 5;
		double expResult = 5;
		//Exercise
		double result = cal.resta(num1, num2);
		
		//Assertion
		assertThat(result, is(expResult));
	}
	
}
