package parser;

import java.util.*;

@SuppressWarnings("preview")
public class Lexer {

    private final char[] chars;

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

    private int currentIndex = 0;

    private char shift() {
        this.currentIndex++;
        return chars[currentIndex - 1];
    }
    private boolean isAlpha(final char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }
    private Token token(final String value, final TokenType type) {
        return new Token(type, value);
    }
    private Token token(final char value, final TokenType type) {
        return new Token(type, String.valueOf(value));
    }
    private String lexIdent() {
        final StringBuilder sb = new StringBuilder();
        while(this.chars.length > currentIndex && this.isAlpha(this.chars[currentIndex])) {
            sb.append(shift());
        }
        return sb.toString();
    }

    // Arrays are faster for lookups than hashmaps
    private static final TokenType[] tokenMap = new TokenType[128];
    static {
        tokenMap[','] = TokenType.SEPARATE;
        tokenMap['='] = TokenType.ASSIGN;
        tokenMap['{'] = TokenType.OPEN_DEF;
        tokenMap['}'] = TokenType.CLOSE_DEF;
        tokenMap['['] = TokenType.OPEN_ARR;
        tokenMap[']'] = TokenType.CLOSE_ARR;
        tokenMap[';'] = TokenType.SEMICOLON;
    }

    public List<Token> tokenize() {
        // lists are faster for adding elements.
        final List<Token> tokens = new ArrayList<>();

        while(this.chars.length > currentIndex) {
            // an array on the other hand is faster for lookups, and we're looking up and not changing, so we'll use an array
            final char c = this.chars[currentIndex];

            switch(c) {
                // This looks nice but it's 20% slower so i won't use it. Sorry
                /*case final Character d when isNumber(d) -> { ... }
                case final Character d when isAlpha(d) -> tokens.add(token(this.lexIdent(), TokenType.IDENT));
                case final Character d when isSkippable(d) -> this.shift();*/

                case '#' -> {
                    tokens.add(token(shift() + this.lexIdent(), TokenType.MIDENT));
                }
                case '/' -> {
                    // comments
                    if(this.chars.length - currentIndex == 1 || this.chars[currentIndex + 1] != '/') throw new IllegalArgumentException(STR."Unexpected character found: \{c}");

                    while(this.chars.length > currentIndex && this.chars[currentIndex] != '\n') {
                        shift();
                    }
                }
                default -> {

                    // numbers
                    if(c >= '0' && c <= '9') {
                        shift();
                        // magic number 48 = 'o'
                        final int num = c - 48;
                        if(num == 0 || num == 1 || (tokens.size() > 1 &&
                                tokens.getLast().type == TokenType.OPEN_ARR &&
                                tokens.get(tokens.size() - 2).type == TokenType.IDENT)) {
                            tokens.add(token(c, TokenType.NUMBER));
                        } else throw new IllegalArgumentException(STR."Unexpected non-binary number: \{num}");

                    // identifiers
                    } else if (isAlpha(c)) {
                        tokens.add(token(shift() + this.lexIdent(), TokenType.IDENT));

                    // skip whitespace
                    } else if (c == ' ' || c == '\n' || c == '\r' || c == '\t')
                        shift();

                    // token mapping
                    else {
                        if (tokenMap[c] == null)
                            throw new IllegalArgumentException(STR."Unexpected character found: \{c}");
                        tokens.add(token(c, tokenMap[c]));
                        shift();
                    }

                }
            }
        }

        return tokens;
    }

    public Lexer(final String sourceCode) {
        this.chars = sourceCode.toCharArray();
    }
}