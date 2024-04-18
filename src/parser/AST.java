package parser;

import java.util.List;

@SuppressWarnings("preview")
public class AST {
    public enum NodeType {
        PROGRAM,
        ASSIGNMENT,
        NUMBER,
        CALL,
        DEF,
        IDENTIFIER,
        MIDENTCALL,
        ARRAY,
        MEMBEREXPR,

        INVALID,
    }

    public interface Stmt {
        NodeType type();
    }

    public record Program(List<Stmt> body) implements Stmt {
        public NodeType type() {
            return NodeType.PROGRAM;
        }

        @Override
        public String toString() {
            return STR."{ type: \{type()}, body: \{body} }";
        }
    }

    public record Assignment(Identifier identifier, Stmt value) implements Stmt {
        public NodeType type() {
            return NodeType.ASSIGNMENT;
        }

        @Override
        public String toString() {
            return STR."{ type: \{type()}, value: \{value} }";
        }
    }

    public record Number(String value) implements Stmt {
        public NodeType type() {
            return NodeType.NUMBER;
        }

        @Override
        public String toString() {
            return STR."{ type: \{type()}, value: \{value} }";
        }
    }

    public record Call(Stmt n1, Identifier ident, Stmt n2) implements Stmt {
        public NodeType type() {
            return NodeType.CALL;
        }

        @Override
        public String toString() {
            return STR."{ type: \{type()}, n1: \{n1}, ident: \{ident}, n2: \{n2} }";
        }
    }

    public record Def(Identifier name, List<Identifier> args, List<Stmt> body) implements Stmt {
        public NodeType type() {
            return NodeType.DEF;
        }

        @Override
        public String toString() {
            return STR."{ type: \{type()}, name: \{name}, args: \{args}, body: \{body} }";
        }
    }

    public record Identifier(String ident) implements Stmt {
        public NodeType type() {
            return NodeType.IDENTIFIER;
        }

        @Override
        public String toString() {
            return STR."{ type: \{type()}, ident: \"\{ident}\" }";
        }
    }

    public record MIdentCall(Identifier ident, Stmt value) implements Stmt {
        public NodeType type() {
            return NodeType.MIDENTCALL;
        }

        @Override
        public String toString() {
            return STR."{ type: \{type()}, ident: \{ident}, value: \{value} }";
        }
    }

    public record Array(List<Stmt> body) implements Stmt {
        public NodeType type() {
            return NodeType.ARRAY;
        }

        @Override
        public String toString() {
            return STR."{ type: \{type()}, body: \{body} }";
        }
    }

    public record MemberExpr(Identifier ident, Stmt value) implements Stmt {
        public NodeType type() {
            return NodeType.MEMBEREXPR;
        }

        @Override
        public String toString() {
            return STR."{ type: \{type()}, value: \{value} }";
        }
    }

}