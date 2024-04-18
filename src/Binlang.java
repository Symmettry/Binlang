import dev.Timer;
import parser.Parser;
import runtime.Environment;
import runtime.Interpreter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

void main(String... args) throws IOException {
    Timer.enabled = args.length > 1 && Objects.equals(args[1], "--dev");
    final String content = new String(Files.readAllBytes(Paths.get(args[0])));

    final Timer timer = new Timer("Binlang Execution");

    final Parser parser = new Parser();
    Interpreter.evaluate(parser.produceAST(content), Environment.createGlobalEnv());

    timer.end();
}