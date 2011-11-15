package org.example;
import static org.junit.Assert.assertThat;

import static org.hamcrest.core.Is.is;
import static org.example.Token.*;

import org.example.Engine;
import org.junit.Before;
import org.junit.Test;

public class EngineTest {

  private Engine engine;

  @Before
  public void setUp() {
    engine = new Engine();
  }

  @Test
  public void complex() {
    engine.setVariable("a", 1);
    engine.setVariable("b", 2);
    assertThat("( a + b ) * 2 == 6 && 1", engine.evaluate(
        OPEN, variable("a"), ADD, variable("b"), CLOSE, MUL, number(2), EQ, number(6), AND, number(1)),
        is(1.0));
  }

  @Test
  public void shouldSupportAdditiveOperators() {
    assertThat(engine.evaluate(number(2), ADD, number(1)), is(3.0));
    assertThat(engine.evaluate(number(2), SUB, number(1)), is(1.0));
  }

  @Test
  public void shouldSupportMultiplicativeOperators() {
    assertThat(engine.evaluate(number(2), MUL, number(3)), is(6.0));
    assertThat(engine.evaluate(number(6), DIV, number(2)), is(3.0));
  }

  @Test
  public void shouldSupportEqualityOperators() {
    assertThat(engine.evaluate(number(2), EQ, number(2)), is(1.0));
    assertThat(engine.evaluate(number(2), EQ, number(3)), is(0.0));

    assertThat(engine.evaluate(number(2), NEQ, number(2)), is(0.0));
    assertThat(engine.evaluate(number(2), NEQ, number(3)), is(1.0));
  }

  @Test
  public void shouldSupportRelationalOperators() {
    assertThat(engine.evaluate(number(2), LT, number(3)), is(1.0));
    assertThat(engine.evaluate(number(3), LT, number(2)), is(0.0));

    assertThat(engine.evaluate(number(3), GT, number(2)), is(1.0));
    assertThat(engine.evaluate(number(2), GT, number(3)), is(0.0));

    assertThat(engine.evaluate(number(2), LTE, number(3)), is(1.0));
    assertThat(engine.evaluate(number(3), LTE, number(3)), is(1.0));
    assertThat(engine.evaluate(number(3), LTE, number(2)), is(0.0));

    assertThat(engine.evaluate(number(4), GTE, number(3)), is(1.0));
    assertThat(engine.evaluate(number(3), GTE, number(3)), is(1.0));
    assertThat(engine.evaluate(number(3), GTE, number(4)), is(0.0));
  }

  @Test
  public void shouldSupportLogicalAnd() {
    assertThat(engine.evaluate(number(1), AND, number(1)), is(1.0));
    assertThat(engine.evaluate(number(1), AND, number(0)), is(0.0));
    assertThat(engine.evaluate(number(0), AND, number(1)), is(0.0));
    assertThat(engine.evaluate(number(0), AND, number(0)), is(0.0));
  }

  @Test
  public void shouldSupportLogicalOr() {
    assertThat(engine.evaluate(number(1), OR, number(1)), is(1.0));
    assertThat(engine.evaluate(number(1), OR, number(0)), is(1.0));
    assertThat(engine.evaluate(number(0), OR, number(1)), is(1.0));
    assertThat(engine.evaluate(number(0), OR, number(0)), is(0.0));
  }

  @Test
  public void shouldSupportUnaryOperators() {
    assertThat(engine.evaluate(NOT, number(1)), is(0.0));
    assertThat(engine.evaluate(NOT, number(0)), is(1.0));

    assertThat(engine.evaluate(ADD, number(1)), is(1.0));

    assertThat(engine.evaluate(SUB, number(1)), is(-1.0));
  }

  @Test
  public void shouldSupportBrackets() {
    assertThat(engine.evaluate(OPEN, number(1), ADD, number(2), CLOSE, MUL, number(2)), is(6.0));
  }

}
