package com.github.basking2.sdsai.itrex.packages;

import com.github.basking2.sdsai.itrex.Evaluator;
import com.github.basking2.sdsai.itrex.functions.*;
import com.github.basking2.sdsai.itrex.functions.function.*;
import com.github.basking2.sdsai.itrex.util.TypeConversion;

import java.util.Iterator;

import static com.github.basking2.sdsai.itrex.iterators.Iterators.toIterator;

public class BasePackage implements Package {

    @Override
    public void importTo(final Evaluator evaluator) {
        evaluator.register("case", new CaseFunction());
        evaluator.register("defaultCase", new DefaultCaseFunction());
        evaluator.register("caseList", new CaseListFunction());

        // Add the function functions.
        evaluator.register("function", new FunctionFunction(evaluator));
        evaluator.register("register", new RegisterFunctionFunction(evaluator));
        evaluator.register("arg", new ArgFunction());
        evaluator.register("args", new ArgsFunction());
        evaluator.register("hasArg", new HasArgFunction());

        evaluator.register("print", new PrintArgsFunction(System.out));
        evaluator.register("printErr", new PrintArgsFunction(System.err));

        evaluator.register("last", new LastFunction());
        evaluator.register("list", new ListFunction());
        evaluator.register("dict", new DictFunction());
        evaluator.register("listFlatten", new ListFlattenFunction());
        evaluator.register("if", new IfFunction());
        evaluator.register("let", new LetFunction());
        evaluator.register("get", new GetFunction());
        evaluator.register("set", new SetFunction());
        evaluator.register("update", new UpdateFunction());
        evaluator.register("head", (iterator, ctx) -> toIterator(iterator.next()).next());
        evaluator.register("for", new ForFunction(evaluator));
        evaluator.register("range", new RangeFunction());
        evaluator.register("tail", (iterator, ctx) -> {
            Iterator<?> i = toIterator(iterator.next());
            i.next();
            return i;
        });

        evaluator.register("string", (itr, ctx) -> TypeConversion.toString(itr.next()));
        evaluator.register("int", (itr, ctx) -> TypeConversion.toInt(itr.next()));
        evaluator.register("float", (itr, ctx) -> TypeConversion.toFloat(itr.next()));
        evaluator.register("long", (itr, ctx) -> TypeConversion.toLong(itr.next()));
        evaluator.register("double", (itr, ctx) -> TypeConversion.toDouble(itr.next()));
        evaluator.register("boolean", (itr, ctx) -> TypeConversion.toBoolean(itr.next()));
    }
}
