# ItrEx

A very simple S-Expression inspired language specializing in iterators.
We call it Iter[ator]Ex[pressions] -- ItrEx.

The language is intentionally limited in its functionality as it
is intended to give no more usefulness than the functions an
implementor defines. This allows us to expose a evaluation 
endpoint on the internet and know the scope of what can be done
with it.

## ItrEx API 

* Import
  * `[import com.mypackage.MyClass]` - Imports all public static fields
    that are of type FunctionInterface and registers them in the 
    evaluator. This is not thread safe as it mutates the global
    registry of functions.
* Info
  * `[version]`
* Functional
  * `[curry function arg1 arg2...]`
  * `[compose function1 function2...]`
* List
  * `[map function iterator]`
  * `[head iterator]`
  * `[tail iterator]`
  * `[last expr1 expr2...]`
  * `[list iterator]`
  * `[listFlatten iterator1 iterator2...]`
  * `[flatten iterator1 iterator2...]`
* String
  * `[stringJoin joinString string1 string2]`
  * `[stringSplit splitRegEx string]`
  * `[stringConcat string1 string2...]`
* Printing
  * `[help]`
  * `[print arg1 arg2... ]` - Note, this returns an iterator.
    Arguments are only printed when the iterator elements are accessed.
  * `[printErr arg1 arg2... ]` - Like print, but prints to standard error.
* Logging
  * `[logDebug arg1 arg2... ]` - Like print, but logs at `DEBUG` level.
  * `[logInfo arg1 arg2... ]` - Like print, but logs at `INFO` level.
  * `[logWarn arg1 arg2... ]` - Like print, but logs at `WARN` level.
  * `[logError arg1 arg2... ]` - Like print, but logs at `ERROR` level.
* Conditional
  * `[if predicate truebranch falsebranch]`
* Variables
  * `[let]`
  * `[get name]`
  * `[set name value]`
* Concurrency
  * `[thread iterator]`
  * `[join iterator]`

# Future Features

* Better scripting support.
* A scoped `import` so two expressions can import functions 
  using the same `Evaluator` and not interfere with one another.
