package org.apache.coyote.todo;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public interface ResourceHandler {
    HttpResponse handle(final HttpRequest httpRequest);
}
