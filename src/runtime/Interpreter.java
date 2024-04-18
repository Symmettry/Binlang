package runtime;

import dev.Timer;
import parser.AST;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("preview")
public class Interpreter {

    public static RuntimeVal evaluate(final AST.Stmt stmt, final Environment env) {
        if(stmt == null) return RuntimeVal.zero();
        return switch(stmt.type()) {
            case IDENTIFIER -> eval_identifier((AST.Identifier) stmt, env);
            case PROGRAM -> eval_program((AST.Program) stmt, env);
            case DEF -> eval_def((AST.Def) stmt, env);
            case NUMBER -> eval_number((AST.Number) stmt);
            case ASSIGNMENT -> eval_assignment((AST.Assignment) stmt, env);
            case CALL -> eval_call((AST.Call) stmt, env);
            case MIDENTCALL -> eval_midentcall((AST.MIdentCall) stmt, env);
            case ARRAY -> eval_array((AST.Array) stmt, env);
            case MEMBEREXPR -> eval_memberexpr((AST.MemberExpr) stmt, env);
            default -> RuntimeVal.zero();
        };
    }

    private static RuntimeVal eval_memberexpr(final AST.MemberExpr expr, final Environment env) {
        final RuntimeVal.ArrayValue arr = RuntimeVal.expect(eval_identifier(expr.ident(), env), RuntimeVal.ArrayValue.class);
        return arr.values().get(RuntimeVal.expect(evaluate(expr.value(), env), RuntimeVal.IntValue.class).value());
    }

    private static RuntimeVal.ArrayValue eval_array(final AST.Array array, final Environment env) {
        return new RuntimeVal.ArrayValue(array.body().stream().map((stmt) -> evaluate(stmt, env)).toList());
    }

    private static RuntimeVal eval_identifier(final AST.Identifier identifier, final Environment env) {
        return env.getVariable(identifier.ident());
    }

    private static RuntimeVal eval_call(final AST.Call call, final Environment env) {
        final RuntimeVal callIdent = evaluate(call.ident(), env);
        final RuntimeVal n1 = evaluate(call.n1(), env), n2 = evaluate(call.n2(), env);
        return switch(callIdent.type()) {
            case FUNC -> {
                final RuntimeVal.FuncValue func = (RuntimeVal.FuncValue) callIdent;
                final Environment newEnv = new Environment(func.parentEnv());
                newEnv.declareVariable(func.args().getFirst().ident(), n1);
                if(func.args().size() > 1) newEnv.declareVariable(func.args().get(1).ident(), n2);
                yield eval_list(func.body(), newEnv);
            }
            case NATIVE_FUNC -> ((RuntimeVal.NativeFuncValue) callIdent).value().apply(new RuntimeVal[]{n1, n2});
            default -> throw new RuntimeException(STR."Unknown call type: \{callIdent.type()}");
        };
    }
    private static RuntimeVal eval_midentcall(final AST.MIdentCall call, final Environment env) {
        final RuntimeVal callIdent = evaluate(call.ident(), env);
        final RuntimeVal value = evaluate(call.value(), env);
        if(callIdent.type() != RuntimeVal.ValueType.NATIVE_FUNC) throw new RuntimeException(STR."Unknown midentcall type: \{callIdent.type()}");
        return ((RuntimeVal.NativeFuncValue) callIdent).value().apply(new RuntimeVal[]{value});
    }

    private static RuntimeVal.FuncValue eval_def(final AST.Def def, final Environment env) {
        return (RuntimeVal.FuncValue) env.declareVariable(def.name().ident(), new RuntimeVal.FuncValue(def.body(), def.args(), env));
    }

    private static RuntimeVal eval_assignment(final AST.Assignment assign, final Environment env) {
        return env.declareVariable(assign.identifier().ident(), evaluate(assign.value(), env));
    }

    private static RuntimeVal.IntValue eval_number(final AST.Number number) {
        return new RuntimeVal.IntValue(Integer.parseInt(number.value())); // 0 and 1 are the only valid numbers
    }

    private static RuntimeVal eval_list(final List<AST.Stmt> body, final Environment env) {
        RuntimeVal lastEval = RuntimeVal.zero();
        for(final AST.Stmt stmt : body) {
            lastEval = evaluate(stmt, env);
        }
        return lastEval;
    }

    private static RuntimeVal eval_program(final AST.Program program, final Environment env) {
        final Timer timer = new Timer("Interpreter");
        final RuntimeVal res = eval_list(program.body(), env);
        System.out.println();
        timer.end();
        return res;
    }

}
