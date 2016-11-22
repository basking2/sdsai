## Functions

* Info
  * version - Takes no arguments and returns the version of this pakcage.
* Functions
  * curry - This takes a list of 1 or more arguments.
            The first argument should be a string
            or a `FunctionInterface` object.
            The rest of the arguments are stored in
            a list for use at dispatch time.
  * compose - Compose 1 or more functions
              such that `["compose, "f", "g", "x"]`
              runs `f(g(x))`.
* List
  * map - This take a function and applies it
          to each of the following arguments.
  * head - Return the first element of the first argument.
           The first argument should be a list.
  * last - Evaluate every argument and return the
           result of the last evaulation.
  * tail - Consume the first element of the first
           argument and return the rest.
  * list - Materialize all arguments into a list
           and return that list.
  * listFlatten - Take a list of iterators and flatten
                  all elements into a list.
                  If a non-list item is encountered
                  it is directly added to the list.
                  This is more tolerant than 
                  the flatten function.
  * flatten - Takes a list of iterators. Returns
              an iterator that will walk through the
              elements of each of those argument iterators.
* String
  * stringJoin - Takes 1 or more strings. Returns
                 a string joined by the first string.
  * stringSplit - Split the second string using the first
                  string as a regular expression.
  * stringConcat - Concatinate all arguments as strings.
* Printing
  * help - Print help text for a function, if any.
  * print - Print each argument to standard out as
            it is requested by a caller.
            If `["print", ...]` is the top level
            of an expression, nothing will be printed
            because nothing will be requested.
  * printErr- Like `print` but uses standard error.
* Logging
  * logDebug - Like print, but logs at `DEBUG` level.
  * logInfo - Like print, but logs at `INFO` level.
  * logWarn - Like print, but logs at `WARN` level.
  * logError - Like print, but logs at `ERROR` level.
* Conditional
  * if - Takes 3 arguments. Evaluates the second
         argument only if the first is true.
         Otherwise only the 3rd argument is evaluated.
* Variables
  * let - Create a child scope that is discarded
          when the let expression finishes evaluation.
  * get - Takes a single value and returns a previously
          set value.
  * set - Takes two arguments. The second is stored
          using the first as a key by which it can be
          retrieved.
