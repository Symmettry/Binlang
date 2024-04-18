import parser.Parser;
import runtime.Environment;
import runtime.Interpreter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

void main(String... args) throws IOException {
    final String content = new String(Files.readAllBytes(Paths.get(args[0])));

    final Parser parser = new Parser();
    Interpreter.evaluate(parser.produceAST(content), Environment.createGlobalEnv());
}