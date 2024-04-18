package runtime;

import parser.AST;

import java.util.List;
import java.util.function.Function;

@SuppressWarnings("preview")
public interface RuntimeVal {
    enum ValueType {
        INT,
        FUNC,
        NATIVE_FUNC,
        ARRAY,

        INVALID,
    }
    // if no type is present it will default to this
    ValueType type();

    record IntValue(int value) implements RuntimeVal {
        public ValueType type() {
            return ValueType.INT;
        }

        @Override
        public String toString() {
            return Integer.toString(value);
        }
    }
    record FuncValue(List<AST.Stmt> body, List<AST.Identifier> args, Environment parentEnv) implements RuntimeVal {
        public ValueType type() {
            return ValueType.FUNC;
        }

        @Override
        public String toString() {
            return "[Callable]";
        }
    }
    record NativeFuncValue(Function<RuntimeVal[], RuntimeVal> value) implements RuntimeVal {
        public ValueType type() {
            return ValueType.NATIVE_FUNC;
        }

        @Override
        public String toString() {
            return "[Native Callable]";
        }
    }
    record ArrayValue(List<RuntimeVal> values) implements RuntimeVal {
        public ValueType type() {
            return ValueType.ARRAY;
        }

        @Override
        public String toString() {
            return values.toString();
        }
    }

    static RuntimeVal zero() {
        return new IntValue(0);
    }

    static <T extends RuntimeVal> T expect(RuntimeVal val, Class<T> c) {
        if(c.isInstance(val)) {
            return c.cast(val);
        }
        throw new RuntimeException(STR."Expected \{c} but got \{val.type()}");
    }
}