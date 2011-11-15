package org.example;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Engine {

  private Map<String, Value> variables = new HashMap<String, Value>();

  private Token[] stream;
  private Token token;
  private int pos;

  public Engine() {
  }

  public void setVariable(String name, double value) {
    variables.put(name, new Value(value));
  }

  public double evaluate(Token... tokens) {
    this.pos = -1;
    this.stream = tokens;
    return expression().getValue();
  }

  private IllegalStateException expected(TokenType... expected) {
    return new IllegalStateException("Expected one of " + Arrays.toString(expected) + ", but got " + token);
  }

  private void nextToken() {
    pos++;
    if (pos < stream.length) {
      token = stream[pos];
    } else {
      token = Token.EOS;
    }
  }

  /**
   * unary ::= NUMBER
   * unary ::= OPEN logicalOr CLOSE
   * unary ::= VARIABLE
   * unary ::= NOT unary
   * unary ::= SUB unary
   * unary ::= ADD unary
   */
  private Value unary() {
    final Value res;
    nextToken();
    if (token.getType() == TokenType.SUB) {
      res = unary();
      return res.setValue(-res.getValue());
    } else if (token.getType() == TokenType.ADD) {
      return unary();
    } else if (token.getType() == TokenType.NOT) {
      res = unary();
      return res.setBooleanValue(!res.getBooleanValue());
    } else if (token.getType() == TokenType.OPEN) {
      res = logicalOr();
      if (token.getType() != TokenType.CLOSE) {
        throw expected(TokenType.CLOSE);
      }
    } else if (token.getType() == TokenType.NUMBER) {
      res = token.getValue();
    } else if (token.getType() == TokenType.VARIABLE) {
      // resolve variable
      res = variables.get(token.getStr());
      if (res == null) {
        throw new IllegalStateException("Unexpected variable name");
      }
    } else {
      throw expected(TokenType.SUB, TokenType.ADD, TokenType.NOT, TokenType.OPEN, TokenType.NUMBER, TokenType.VARIABLE);
    }
    nextToken();
    return res;
  }

  /**
   * multiplicative ::= unary MUL unary
   * multiplicative ::= unary DIV unary
   */
  private Value mulpiplicative() {
    Value res = unary();
    while (token.getType() == TokenType.MUL || token.getType() == TokenType.DIV) {
      if (token.getType() == TokenType.MUL) {
        res.setValue(res.getValue() * unary().getValue());
      } else if (token.getType() == TokenType.DIV) {
        res.setValue(res.getValue() / unary().getValue());
      }
    }
    return res;
  }

  /**
   * additive ::= multiplicative ADD multiplicative
   * additive ::= multiplicative SUB multiplicative
   */
  private Value additive() {
    Value res = mulpiplicative();
    while (token.getType() == TokenType.ADD || token.getType() == TokenType.SUB) {
      if (token.getType() == TokenType.ADD) {
        res.setValue(res.getValue() + mulpiplicative().getValue());
      } else if (token.getType() == TokenType.SUB) {
        res.setValue(res.getValue() - mulpiplicative().getValue());
      }
    }
    return res;
  }

  /**
   * relational ::= additive LT additive
   * relational ::= additive GT additive
   * relational ::= additive LTE additive
   * relational ::= additive GTE additive
   */
  private Value relational() {
    Value res = additive();
    while (token.getType() == TokenType.LT || token.getType() == TokenType.GT ||
        token.getType() == TokenType.LTE || token.getType() == TokenType.GTE) {
      if (token.getType() == TokenType.LT) {
        res.setBooleanValue(res.getValue() < additive().getValue());
      } else if (token.getType() == TokenType.GT) {
        res.setBooleanValue(res.getValue() > additive().getValue());
      } else if (token.getType() == TokenType.LTE) {
        res.setBooleanValue(res.getValue() <= additive().getValue());
      } else if (token.getType() == TokenType.GTE) {
        res.setBooleanValue(res.getValue() >= additive().getValue());
      }
    }
    return res;
  }

  /**
   * equality ::= relational EQ relational
   * equality ::= relational NEQ relational
   */
  private Value equality() {
    Value res = relational();
    while (token.getType() == TokenType.EQ || token.getType() == TokenType.NEQ) {
      if (token.getType() == TokenType.EQ) {
        res.setBooleanValue(res.getValue() == relational().getValue());
      } else if (token.getType() == TokenType.NEQ) {
        res.setBooleanValue(res.getValue() != relational().getValue());
      }
    }
    return res;
  }

  /**
   * logicalAnd ::= equality AND equality
   */
  private Value logicalAnd() {
    Value res = equality();
    while (token.getType() == TokenType.AND) {
      Value operand = equality();
      res.setBooleanValue(res.getBooleanValue() && operand.getBooleanValue());
    }
    return res;
  }

  /**
   * logicalOr ::= logicalAnd OR logicalAnd
   */
  private Value logicalOr() {
    Value res = logicalAnd();
    while (token.getType() == TokenType.OR) {
      Value operand = logicalAnd();
      res.setBooleanValue(res.getBooleanValue() || operand.getBooleanValue());
    }
    return res;
  }

  /**
   * expression ::= logicalOr EOS
   */
  private Value expression() {
    Value res = logicalOr();
    if (token.getType() != TokenType.EOS) {
      throw expected(TokenType.EOS);
    }
    return res;
  }

}
