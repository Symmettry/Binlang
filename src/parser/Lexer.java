package parser;

import java.util.*;

@SuppressWarnings("preview")
public class Lexer {

    private Character[] chars;

    public enum TokenType {
        MIDENT, // #def, #prn, #set
        IDENT, // and, not, anything at all lmao

        OPEN_DEF, // {
        CLOSE_DEF, // }

        OPEN_ARR, // [
        CLOSE_ARR, // ]

        SEMICOLON, // ;

        NUMBER, // 0 or 1
        SEPARATE, // comma
        ASSIGN, // equals
    }

    public static class Token {
        final String value;
        final TokenType type;
        public Token(final TokenType type, final String value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public String toString() {
            return STR."{ type: \{type}, value: \"\{value}\" }";
        }
    }

    private Character shift() {
        final char temp = this.chars[0];
        this.chars = Arrays.stream(this.chars).skip(1).toArray(Character[]::new);
        return temp;
    }
    private boolean isAlpha(final char c) {
        return ((Character) c).toString().matches("[a-zA-Z_]+");
    }
    private boolean isNumber(final char c) {
        return ((Character) c).toString().matches("[0-9]+");
    }
    private Token token(final String value, final TokenType type) {
        return new Token(type, value);
    }
    private String lexIdent() {
        final StringBuilder sb = new StringBuilder();
        while(this.chars.length > 0 && this.isAlpha(this.chars[0])) {
            sb.append(this.chars[0]);
            shift();
        }
        return sb.toString();
    }
    private boolean isSkippable(final Character c) {
        return c == ' ' || c == '\n' || c == '\r' || c == '\t';
    }

    private static final HashMap<Character, TokenType> tokenMap = new HashMap<>();
    static {
        tokenMap.put(',', TokenType.SEPARATE);
        tokenMap.put('=', TokenType.ASSIGN);
        tokenMap.put('{', TokenType.OPEN_DEF);
        tokenMap.put('}', TokenType.CLOSE_DEF);
        tokenMap.put('[', TokenType.OPEN_ARR);
        tokenMap.put(']', TokenType.CLOSE_ARR);
        tokenMap.put(';', TokenType.SEMICOLON);
    }

    public List<Token> tokenize() {
        final List<Token> tokens = new ArrayList<>();

        while(this.chars.length > 0) {
            final Character c = this.chars[0];

            switch(c) {
                case Character d when isNumber(d) -> {
                    shift();
                    final int num = Integer.parseInt(d.toString());
                    if(num == 0 || num == 1 || (tokens.size() > 1 &&
                            tokens.getLast().type == TokenType.OPEN_ARR &&
                            tokens.get(tokens.size() - 2).type == TokenType.IDENT)) {
                        tokens.add(token(d.toString(), TokenType.NUMBER));
                    } else throw new IllegalArgumentException(STR."Unexpected non-binary number: \{num}");
                }
                case Character d when isAlpha(d) -> tokens.add(token(this.lexIdent(), TokenType.IDENT));
                case Character d when isSkippable(d) -> this.shift();
                case '#' -> {
                    shift(); // shift #
                    tokens.add(token(STR."#\{lexIdent()}", TokenType.MIDENT));
                }
                default -> {
                    if (!tokenMap.containsKey(c)) throw new IllegalArgumentException(STR."Unexpected character found: \{c.toString()}");
                    tokens.add(token(c.toString(), tokenMap.get(c)));
                    shift();
                }
            }
        }

        return tokens;
    }

    public Lexer(final String sourceCode) {
        this.chars = sourceCode.chars().mapToObj(c -> (char) c).toArray(Character[]::new);
    }
}