package com.mayab.unitTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ParamsTestCalculadora {
	private Calculadora cal = null;
	
	@BeforeEach
	void setup() {
		System.out.println("Before each test");
		cal = new Calculadora();
	}
	
	
	@ParameterizedTest
	@MethodSource("multipleArgs")
	void testSuma(double num1, double num2, double expected) {
		
		//Exercise
		double result = cal.suma(num2, num1);
		
		//Assertion
		assertThat(result, is(expected));
	}
	
	static Stream<Arguments> multipleArgs(){
		return Stream.of(Arguments.of(2,3,5),
				Arguments.of(12,3,15),
				Arguments.of(2,30,32));
	}

}
