package org.apache.coyote;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpRequestParser {

    private HttpRequestParser() {
    }

    public static HttpRequest parseRequest(final BufferedReader bufferedReader) throws IOException {
        final String request = bufferedReader.readLine();

        final String[] requestStartLine = request.split(" ");
        final String method = requestStartLine[0];
        final String path = requestStartLine[1];
        final String version = requestStartLine[2];

        return new HttpRequest(method, path, version, parseRequestHeaders(bufferedReader), parseRequestBody(bufferedReader));
    }

    private static HttpHeader[] parseRequestHeaders(final BufferedReader bufferedReader) throws IOException {
        String header = bufferedReader.readLine();

        final List<HttpHeader> headers = new ArrayList<>();
        while (header != null && !header.isEmpty()) {
            String[] keyAndValue = header.split(": ");
            headers.add(new HttpHeader(keyAndValue[0], keyAndValue[1]));
            header = bufferedReader.readLine();
            if(header.isEmpty()) {
                break;
            }
        }

        return headers.toArray(HttpHeader[]::new);
    }

    private static String parseRequestBody(final BufferedReader bufferedReader) throws IOException {
        String requestBody = bufferedReader.readLine();
        if (requestBody == null || requestBody.isEmpty()) {
            return null;
        }

        final StringBuilder stringBuilder = new StringBuilder();
        while (!requestBody.isEmpty()) {
            requestBody = bufferedReader.readLine();
            stringBuilder.append(requestBody).append("\n");
        }

        return stringBuilder.toString();
    }
}
