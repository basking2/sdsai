package com.github.basking2.sdsai.itrex.packages;

import com.github.basking2.sdsai.itrex.Evaluator;
import com.github.basking2.sdsai.itrex.functions.*;
import com.github.basking2.sdsai.itrex.functions.function.*;
import com.github.basking2.sdsai.itrex.util.TypeConversion;

import java.util.Iterator;

import static com.github.basking2.sdsai.itrex.iterators.Iterators.toIterator;
import static com.github.basking2.sdsai.itrex.iterators.Iterators.isIter;

public class BasePackage implements Package {

    @Override
    public void importTo(final Evaluator evaluator, final String packageName) {
        doRegister(evaluator, packageName, "case", new CaseFunction());
        doRegister(evaluator, packageName, "defaultCase", new DefaultCaseFunction());
        doRegister(evaluator, packageName, "caseList", new CaseListFunction());

        // Add the function functions.
        doRegister(evaluator, packageName, "function", new FunctionFunction(evaluator));
        doRegister(evaluator, packageName, "fn", new FnFunction(evaluator));
        doRegister(evaluator, packageName, "register", new RegisterFunctionFunction());
        doRegister(evaluator, packageName, "arg", new ArgFunction());
        doRegister(evaluator, packageName, "args", new ArgsFunction());
        doRegister(evaluator, packageName, "nameArgs", new NameArgsFunction());
        doRegister(evaluator, packageName, "hasArg", new HasArgFunction());

        doRegister(evaluator, packageName, "print", new PrintArgsFunction(System.out));
        doRegister(evaluator, packageName, "printErr", new PrintArgsFunction(System.err));
        doRegister(evaluator, packageName, "trace", new TraceFunction(System.out));
        doRegister(evaluator, packageName, "traceErr", new TraceFunction(System.err));

        doRegister(evaluator, packageName, "last", new LastFunction());
        doRegister(evaluator, packageName, "list", new ListFunction());
        doRegister(evaluator, packageName, "listFlatten", new ListFlattenFunction());
        doRegister(evaluator, packageName, "if", new IfFunction());
        doRegister(evaluator, packageName, "filter", new FilterFunction());
        doRegister(evaluator, packageName, "let", new LetFunction());
        doRegister(evaluator, packageName, "get", new GetFunction());
        doRegister(evaluator, packageName, "set", new SetFunction());
        doRegister(evaluator, packageName, "update", new UpdateFunction());
        doRegister(evaluator, packageName, "isitr", (iterator, ctx) -> isIter(iterator.next()));
        doRegister(evaluator, packageName, "head", (iterator, ctx) -> toIterator(iterator.next()).next());
        doRegister(evaluator, packageName, "for", new ForFunction(evaluator));
        doRegister(evaluator, packageName, "range", new RangeFunction());
        doRegister(evaluator, packageName, "tail", (iterator, ctx) -> {
            Iterator<?> i = toIterator(iterator.next());
            i.next();
            return i;
        });

        doRegister(evaluator, packageName, "string", (itr, ctx) -> TypeConversion.toString(itr.next()));
        doRegister(evaluator, packageName, "int", (itr, ctx) -> TypeConversion.toInt(itr.next()));
        doRegister(evaluator, packageName, "float", (itr, ctx) -> TypeConversion.toFloat(itr.next()));
        doRegister(evaluator, packageName, "long", (itr, ctx) -> TypeConversion.toLong(itr.next()));
        doRegister(evaluator, packageName, "double", (itr, ctx) -> TypeConversion.toDouble(itr.next()));
        doRegister(evaluator, packageName, "boolean", (itr, ctx) -> TypeConversion.toBoolean(itr.next()));
    }

    private static void doRegister(final Evaluator evaluator, final String packageName, final String name, final FunctionInterface<?> fn) {
        if (packageName == null || packageName.isEmpty()) {
            evaluator.register(name, fn);
        } else {
            evaluator.register(packageName +"."+name, fn);
        }
    }

}
