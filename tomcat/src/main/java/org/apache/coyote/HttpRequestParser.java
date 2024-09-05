package org.apache.coyote;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestParser {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestParser.class);

    private HttpRequestParser() {
    }

    public static HttpRequest parseRequest(final BufferedReader bufferedReader) throws IOException {
        final String request = bufferedReader.readLine();

        final String[] requestStartLine = request.split(" ");
        final String method = requestStartLine[0];
        final String path = requestStartLine[1];
        final String version = requestStartLine[2];
        log.info("method = {}, path = {}, version = {}", method, path, version);
        return new HttpRequest(method, path, version, parseRequestHeaders(bufferedReader), parseRequestBody(bufferedReader));
    }

    private static HttpHeader[] parseRequestHeaders(final BufferedReader bufferedReader) throws IOException {
        String header = bufferedReader.readLine();
        log.info("parse header = {}", header);

        final List<HttpHeader> headers = new ArrayList<>();
        while (header != null && !header.isEmpty()) {
            String[] keyAndValue = header.split(": ");
            headers.add(new HttpHeader(keyAndValue[0], keyAndValue[1]));
            header = bufferedReader.readLine();
            if(header.isEmpty()) {
                break;
            }
        }
        log.info("end of headers");

        return headers.toArray(HttpHeader[]::new);
    }

    private static String parseRequestBody(final BufferedReader bufferedReader) throws IOException {
        log.info("parse body");
        String requestBody = bufferedReader.readLine();
        if (requestBody == null || requestBody.isEmpty()) {
            log.info("empty body");
            return null;
        }

        final StringBuilder stringBuilder = new StringBuilder();
        while (requestBody == null || !requestBody.isEmpty()) {
            System.out.println("requestBody = " + requestBody);
            requestBody = bufferedReader.readLine();
            stringBuilder.append(requestBody).append("\n");
        }
        log.info("end of body");

        return stringBuilder.toString();
    }
}
