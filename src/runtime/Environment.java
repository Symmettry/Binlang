package runtime;

import java.util.HashMap;

public class Environment {

    public static Environment createGlobalEnv() {
        final Environment env = new Environment();

        env.declareVariable("#prn", new RuntimeVal.NativeFuncValue((args) -> {
            System.out.print(args[0]);
            return RuntimeVal.zero();
        }));
        env.declareVariable("#pnl", new RuntimeVal.NativeFuncValue((args) -> {
            System.out.println(args[0]);
            return RuntimeVal.zero();
        }));

        env.declareVariable("and", new RuntimeVal.NativeFuncValue((args) -> {
            final RuntimeVal.IntValue n1 = RuntimeVal.expect(args[0], RuntimeVal.IntValue.class),
                    n2 = RuntimeVal.expect(args[1], RuntimeVal.IntValue.class);
            return new RuntimeVal.IntValue(n1.value() == 1 && n2.value() == 1 ? 1 : 0);
        }));
        env.declareVariable("not", new RuntimeVal.NativeFuncValue((args) -> {
            final RuntimeVal.IntValue num = RuntimeVal.expect(args[0], RuntimeVal.IntValue.class);
            return new RuntimeVal.IntValue(num.value() == 0 ? 1 : 0);
        }));

        return env;
    }

    private final Environment parent;
    private final HashMap<String, RuntimeVal> variables = new HashMap<>();
    public Environment() {
        this.parent = null;
    }
    public Environment(Environment env) {
        this.parent = env;
    }

    public RuntimeVal declareVariable(final String name, final RuntimeVal value) {
        this.variables.put(name, value);
        return value;
    }
    public RuntimeVal getVariable(final String name) {
        return this.variables.getOrDefault(name, parent == null ? RuntimeVal.zero() : parent.getVariable(name));
    }

    @SuppressWarnings("preview")
    @Override
    public String toString() {
        return STR."{ parent: \{parent}, variables: \{variables} }";
    }

}
