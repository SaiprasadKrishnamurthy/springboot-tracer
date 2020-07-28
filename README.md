## Lean, Tracing Framework for Regular SpringBoot Applications (Non Reactive).

TODO Documentation is in progress.
 
### Objective
Purpose is to provide observability within and across springboot applications in a distributed microservice architecture
so that the logs and traces could be used for various analytics & intelligence.

### List of Techniques
* Source code instrumentation/transformation/transpilation - Modify the Source code (.java files) to inject the tracing aspects.
* Byte code instrumentation/transformation - Modify the Byte code (.class files) to inject the tracing aspects during the class load time using the instrumentation techniques.
* Use OpenTracing API with appropriate bindings - Use Zipkin or Jaeger and instrument the application.
* Use plain Spring AOP - Use spring aspected oriented programming using AspectJ which fits well with the existing spring eco-system.

### Technique Used
I have resorted to plain Spring AOP purely for it's simplicity and it offers maximum flexibility.

### Functional Considerations.
* Very minimal intrusiveness (The source code should not be polluted with tracing related concerns).
* Very quick/simple to bootstrap (Should be very quick for any existing springboot application to include this as a library and automagically bootstrap itself with all the instrumentation required).
* Instrumentation should be tuned off/on as a configurable parameter.
* Tracing should be tuned off/on as a configurable parameter.
* Parameters logging - if required, the parameters should be extracted from a method call and logged. These parameters must be annotated in the source files.
* Asynchronous - The tracing should happen in a background thread which shouldn't obstruct the main thread of execution.
* Context propagation - The tracing identifier must be propagated within the application and across the applications.
* Inter Context propagation mechanisms - To pass the context from one application to another, we should take advantage of http headers (if it's a REST/SOAP call), jms headers (if it's a JMS call), kafka headers (if it's via kafka).
* Design must be flexible enough to accommodate more providers in the future.
* Support for adding more processors to enrich the raw trace being collected from the application.
* Support for multiple backends to store the trace (elastic/kafka logs/mongo).

 





