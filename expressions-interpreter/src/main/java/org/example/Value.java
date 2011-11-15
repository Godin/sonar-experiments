package org.example;

public class Value {

  private double value;

  public Value(double value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return Double.toString(value);
  }

  public double getValue() {
    return value;
  }

  public Value setValue(double d) {
    this.value = d;
    return this;
  }

  public boolean getBooleanValue() {
    return this.value > 0;
  }

  public Value setBooleanValue(boolean b) {
    this.value = b ? 1.0 : 0.0;
    return this;
  }

}
