package parser;

import dev.Timer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("preview")
public class Parser {

    private List<Lexer.Token> tokens;
    private int currentIndex = 0;

    private Lexer.Token eat() {
        if(this.currentIndex >= this.tokens.size()) return null;
        final Lexer.Token temp = this.tokens.get(currentIndex);
        this.tokens.set(currentIndex, null);
        currentIndex++;
        return temp;
    }
    private Lexer.Token at() {
        return this.currentIndex >= this.tokens.size() ? new Lexer.Token(Lexer.TokenType.SEMICOLON, ";") : this.tokens.get(currentIndex);
    }
    @SuppressWarnings("preview")
    private Lexer.Token expect(final Lexer.TokenType type, final String error) {
        if(this.at().type != type) {
            throw new IllegalArgumentException(STR."Parser error:\n\{error} Instead found: \{this.at().type}");
        }
        return this.eat();
    }

    private AST.Identifier identify(final Object name) {
        return new AST.Identifier((String) name);
    }
    private AST.Number numerate() {
        return new AST.Number((char) Objects.requireNonNull(this.eat()).value);
    }
    private AST.Stmt getNextNumValue() {
        return this.at().type == Lexer.TokenType.NUMBER ? this.numerate() : identify(Objects.requireNonNull(this.eat()).value);
    }

    public AST.Program produceAST(String sourceCode) {
        final Timer lexerTimer = new Timer("Lexer");

        final Lexer lexer = new Lexer(sourceCode);
        this.tokens = lexer.tokenize();

        lexerTimer.end();

        final Timer parserTimer = new Timer("Parser");

        final List<AST.Stmt> body = new ArrayList<>();
        while(this.currentIndex < this.tokens.size()) {
            body.add(this.parse_stmt());
        }

        parserTimer.end();

        System.out.println();

        return new AST.Program(body);
    }

    private AST.Stmt parse_stmt() {
        return switch(this.at().type) {
            case NUMBER, IDENT -> this.parse_call();
            case SEMICOLON -> {
                this.eat();
                yield null;
            }
            case OPEN_ARR -> this.parse_array();
            case MIDENT -> switch(this.at().value.toString()) {
                case "#set" -> this.parse_set();
                case "#def" -> this.parse_def();
                case "#con" -> this.parse_con();
                default -> new AST.MIdentCall(identify(Objects.requireNonNull(this.eat()).value), this.parse_stmt());
            };
            default -> throw new IllegalArgumentException(STR."Unknown value: \{this.at().value}");
        };
    }

    private List<AST.Stmt> parse_scope() {
        this.expect(Lexer.TokenType.OPEN_CBRACE, "Expected { following value in operation.");
        final List<AST.Stmt> body = new ArrayList<>();
        while(this.at().type != Lexer.TokenType.CLOSE_CBRACE) {
            if(this.currentIndex > this.tokens.size()) throw new IllegalArgumentException("Opening curly brace does not end; expected closing curly bracket.");
            body.add(this.parse_stmt());
        }
        this.eat(); // remove }
        return body;
    }

    private AST.Con parse_con() {
        this.eat(); // eat #con
        final AST.Stmt number = this.getNextNumValue();

        final List<AST.Stmt> body = this.parse_scope();
        final List<AST.Stmt> elseBody = this.at().type == Lexer.TokenType.OPEN_CBRACE ? this.parse_scope() : null;

        return new AST.Con(number, body, elseBody);
    }

    private AST.Array parse_array() {
        this.eat(); // eat [
        final List<AST.Stmt> body = new ArrayList<>();
        while(this.at().type != Lexer.TokenType.CLOSE_ARR) {
            if(this.currentIndex > this.tokens.size()) throw new IllegalArgumentException("Array does not end; expected closing square bracket.");
            body.add(this.parse_stmt());
        }
        this.eat(); // eat ]
        return new AST.Array(body);
    }

    // please someone tell me what the fuck (AST.Identifer) can be suppressed with because "cast" does not work
    @SuppressWarnings("all")
    private AST.Stmt parse_call() {
         final AST.Stmt val1 = this.getNextNumValue();
         if (val1.type() == AST.NodeType.IDENTIFIER && this.at().type == Lexer.TokenType.OPEN_ARR) { // a[]
            this.eat(); // eat [
            final AST.Stmt value = this.parse_stmt();
            this.eat(); // eat ]
            return new AST.MemberExpr((AST.Identifier) val1, value);
        }
        if(this.at().type == Lexer.TokenType.SEMICOLON || this.at().type == Lexer.TokenType.CLOSE_ARR) { // a; a]
            this.eat(); // eat semicolon
            return val1;
        }

        // a and b
        final AST.Identifier callIdent = identify(this.expect(Lexer.TokenType.IDENT, "Expected call identifier following number/ident.").value);
        if(this.at().type == Lexer.TokenType.SEMICOLON) {
            this.eat(); // eat semicolon
            return new AST.Call(val1, callIdent, null);
        }

        final AST.Stmt val2 = this.at().type == Lexer.TokenType.NUMBER ? this.numerate() : identify(Objects.requireNonNull(this.eat()).value);
        return new AST.Call(val1, callIdent, val2);
    }

    private AST.Def parse_def() {
        this.eat(); // remove #def

        final Object name = this.expect(Lexer.TokenType.IDENT, "Expected identifier following #def operation.").value;

        final List<AST.Identifier> args = new ArrayList<>();
        args.add(identify(this.expect(Lexer.TokenType.IDENT, "Expected argument following identifier in #def operation.").value)); // first arg

        if(Objects.requireNonNull(this.at()).type == Lexer.TokenType.SEPARATE) {
            this.eat();
            args.add(identify(this.expect(Lexer.TokenType.IDENT, "Expected argument following separator in #def operation.").value)); // e.g. #def nand a, b
        }

        final List<AST.Stmt> body = this.parse_scope();

        return new AST.Def(identify(name), args, body);
    }

    private AST.Assignment parse_set() {
        this.eat(); // remove #set

        final AST.Identifier ident = identify(this.expect(Lexer.TokenType.IDENT, "Expected IDENT following #set operation.").value);

        this.expect(Lexer.TokenType.ASSIGN, "Expected '=' following IDENT in #set operation.");

        return new AST.Assignment(ident, this.parse_stmt());
    }

}
