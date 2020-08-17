package com.github.saiprasadkrishnamurthy.tracer.api;

import java.util.Optional;

public interface RawEventTransformer {
    boolean canHandle(RawEvent rawEvent);
    Optional<MethodEvent> transform(RawEvent methodEvent);
}
