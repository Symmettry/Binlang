package parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("preview")
public class Parser {

    private List<Lexer.Token> tokens;

    private Lexer.Token eat() {
        if(this.tokens.isEmpty()) return null;
        final Lexer.Token temp = this.tokens.getFirst();
        this.tokens = this.tokens.stream().skip(1).toList();
        return temp;
    }
    private Lexer.Token at() {
        return this.tokens.isEmpty() ? new Lexer.Token(Lexer.TokenType.SEMICOLON, ";") : this.tokens.getFirst();
    }
    private Lexer.Token at(int offset) {
        return this.tokens.size() < offset ? new Lexer.Token(Lexer.TokenType.SEMICOLON, ";") : this.tokens.get(offset);
    }
    @SuppressWarnings("preview")
    private Lexer.Token expect(final Lexer.TokenType type, final String error) {
        if(this.at().type != type) {
            throw new IllegalArgumentException(STR."Parser error:\n\{error} Instead found: \{this.at().type}");
        }
        return this.eat();
    }

    private AST.Identifier identify(final String name) {
        return new AST.Identifier(name);
    }
    private AST.Number numerate() {
        return new AST.Number(Objects.requireNonNull(this.eat()).value);
    }

    public AST.Program produceAST(String sourceCode) {
        final Lexer lexer = new Lexer(sourceCode);
        this.tokens = lexer.tokenize();

        final List<AST.Stmt> body = new ArrayList<>();
        while(!this.tokens.isEmpty()) {
            body.add(this.parse_stmt());
        }

        return new AST.Program(body);
    }

    private AST.Stmt parse_stmt() {
        return switch(this.at().type) {
            case MIDENT -> switch(this.at().value) {
                case "#set" -> this.parse_set();
                case "#def" -> this.parse_def();
                default -> new AST.MIdentCall(identify(Objects.requireNonNull(this.eat()).value), this.parse_stmt());
            };
            case NUMBER, IDENT -> this.parse_call();
            case OPEN_ARR -> this.parse_array();
            default -> {
                this.eat();
                yield null;
            }
        };
    }

    private AST.Array parse_array() {
        this.eat(); // eat [
        final List<AST.Stmt> body = new ArrayList<>();
        while(this.at().type != Lexer.TokenType.CLOSE_ARR) {
            if(this.tokens.isEmpty()) throw new IllegalArgumentException("Array does not end; expected closing square bracket.");
            body.add(this.parse_stmt());
        }
        this.eat(); // eat ]
        return new AST.Array(body);
    }

    private AST.Stmt parse_call() {
         AST.Stmt val1 = this.at().type == Lexer.TokenType.NUMBER ? this.numerate() : identify(Objects.requireNonNull(this.eat()).value);
         if (val1.type() == AST.NodeType.IDENTIFIER && this.at().type == Lexer.TokenType.OPEN_ARR) { // a[]
            this.eat(); // eat [
            final AST.Stmt value = this.parse_stmt();
            this.eat(); // eat ]
            assert val1 instanceof AST.Identifier; // fuck you intellij :heart:
            val1 = new AST.MemberExpr((AST.Identifier) val1, value);
        }
        if(this.at().type == Lexer.TokenType.SEMICOLON) { // a;
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

        final String name = this.expect(Lexer.TokenType.IDENT, "Expected identifier following #def operation.").value;

        final List<AST.Identifier> args = new ArrayList<>();
        args.add(identify(this.expect(Lexer.TokenType.IDENT, "Expected argument following identifier in #def operation.").value)); // first arg

        if(Objects.requireNonNull(this.at()).type == Lexer.TokenType.SEPARATE) {
            this.eat();
            args.add(identify(this.expect(Lexer.TokenType.IDENT, "Expected argument following separator in #def operation.").value)); // e.g. #def nand a, b
        }

        this.expect(Lexer.TokenType.OPEN_DEF, "Expected opening curly bracket after arguments in #def operation."); // remove {

        final List<AST.Stmt> body = new ArrayList<>();
        while(this.at().type != Lexer.TokenType.CLOSE_DEF) {
            if(this.tokens.isEmpty()) throw new IllegalArgumentException("Defined operator does not end; expected closing curly bracket.");
            body.add(this.parse_stmt());
        }
        this.eat(); // remove }

        return new AST.Def(identify(name), args, body);
    }

    private AST.Assignment parse_set() {
        this.eat(); // remove #set

        final AST.Identifier ident = identify(this.expect(Lexer.TokenType.IDENT, "Expected IDENT following #set operation.").value);

        this.expect(Lexer.TokenType.ASSIGN, "Expected '=' following IDENT in #set operation.");

        return new AST.Assignment(ident, this.parse_stmt());
    }

}
