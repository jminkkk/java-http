package org.apache.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.http.header.HttpHeader;
import org.apache.http.header.HttpHeaderName;
import org.apache.http.header.HttpHeaders;

public class HttpRequestReader {

    private HttpRequestReader() {
    }

    public static HttpRequest readHttpRequest(final BufferedReader bufferedReader) throws IOException {
        final RequestLine requestLine = RequestLine.from(bufferedReader.readLine());
        final HttpHeaders headers = new HttpHeaders(readRequestHeaders(bufferedReader));

        return new HttpRequest(requestLine, headers, readRequestBody(headers, bufferedReader));
    }

    private static HttpHeader[] readRequestHeaders(final BufferedReader bufferedReader) throws IOException {
        String header = bufferedReader.readLine();

        final List<HttpHeader> headers = new ArrayList<>();
        while (header != null && !header.isEmpty()) {
            String[] keyAndValue = header.split(": ");
            headers.add(new HttpHeader(keyAndValue[0], keyAndValue[1]));
            header = bufferedReader.readLine();
        }

        return headers.toArray(HttpHeader[]::new);
    }

    private static String readRequestBody(final HttpHeaders headers, final BufferedReader bufferedReader)
            throws IOException {
        Optional<HttpHeader> httpHeader = headers.getHeaders().stream()
                .filter(header -> HttpHeaderName.CONTENT_LENGTH == header.getKey())
                .findFirst();

        if (httpHeader.isEmpty()) {
            return null;
        }

        int contentLength = Integer.parseInt(httpHeader.get().getValue());
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }
}
