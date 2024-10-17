package com.mayab.unitTest;

public class Calculadora {

private double ultimoResultado;
//Methods
public double suma(double operando1, double operando2) {
return ultimoResultado = operando1 + operando2;
}
public double resta(double operando1, double operando2) {
return ultimoResultado = operando1 - operando2;
}
public double division(double operando1, double operando2) {
return ultimoResultado = operando1 / operando2;
}
public double multiplicacion(double operando1, double operando2) {
return ultimoResultado = operando1 * operando2;
}
public double getUltimoResultado() {
return ultimoResultado;
}
public int divisionEntera(int dividendo, int divisor)
{
return dividendo/divisor;
}
}

