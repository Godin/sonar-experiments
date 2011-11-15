package org.example;


public class Token {

  public static final Token EOS = new Token(TokenType.EOS);

  public static final Token ADD = new Token(TokenType.ADD);
  public static final Token SUB = new Token(TokenType.SUB);

  public static final Token MUL = new Token(TokenType.MUL);
  public static final Token DIV = new Token(TokenType.DIV);

  public static final Token OPEN = new Token(TokenType.OPEN);
  public static final Token CLOSE = new Token(TokenType.CLOSE);

  public static final Token EQ = new Token(TokenType.EQ);
  public static final Token NEQ = new Token(TokenType.NEQ);

  public static final Token LT = new Token(TokenType.LT);
  public static final Token GT = new Token(TokenType.GT);
  public static final Token LTE = new Token(TokenType.LTE);
  public static final Token GTE = new Token(TokenType.GTE);

  public static final Token AND = new Token(TokenType.AND);
  public static final Token OR = new Token(TokenType.OR);

  public static final Token NOT = new Token(TokenType.NOT);

  public static Token number(double value) {
    return new Token(value);
  }

  public static Token variable(String name) {
    return new Token(name);
  }

  private TokenType type;
  private Value value;
  private String str;

  private Token(TokenType type) {
    this.type = type;
  }

  private Token(String variableName) {
    this.type = TokenType.VARIABLE;
    this.str = variableName;
  }

  private Token(double value) {
    this.type = TokenType.NUMBER;
    this.value = new Value(value);
  }

  public TokenType getType() {
    return type;
  }

  public Value getValue() {
    return value;
  }

  public String getStr() {
    return str;
  }

  @Override
  public String toString() {
    if (type == TokenType.NUMBER) {
      return value.toString();
    } else if (type == TokenType.VARIABLE) {
      return str;
    } else {
      return type.toString();
    }
  }

}
