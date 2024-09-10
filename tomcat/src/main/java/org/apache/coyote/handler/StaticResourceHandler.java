package org.apache.coyote.handler;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.apache.coyote.NotFoundException;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponseGenerator;

public class StaticResourceHandler extends Handler {

    private static final Map<String, String> STATIC_RESOURCE_EXTENSIONS = Map.of(
            "html", "",
            "css", "/css",
            "js", "/assets"
    );
    private static final String STATIC_RESOURCE_ROOT_PATH = "static";
    private static final String DEFAULT_EXTENSION = ".html";
    private static final String PATH_DELIMITER = "/";

    private static final StaticResourceHandler INSTANCE = new StaticResourceHandler();

    private StaticResourceHandler() {
    }

    public static StaticResourceHandler getInstance() {
        return INSTANCE;
    }

    public String handle(final HttpRequest httpRequest) {
        final URL resourceURL = getClass().getClassLoader().getResource(findResourcePath(httpRequest.getPath()));
        try {
            final Path resourcePath = Path.of(resourceURL.getPath());
            final String responseBody = Files.readString(resourcePath);
            final String mimeType = Files.probeContentType(resourcePath);
            return HttpResponseGenerator.getOkResponse(mimeType, responseBody);
        } catch (NullPointerException e) {
            throw new NotFoundException(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String findResourcePath(final String path) {
        final String[] resourceNames = findResourceName(path).split("\\.");
        final String extension = resourceNames[resourceNames.length - 1];

        if (STATIC_RESOURCE_EXTENSIONS.containsKey(extension)) {
            return STATIC_RESOURCE_ROOT_PATH
                    .concat(path);
        }

        return STATIC_RESOURCE_ROOT_PATH
                .concat(path)
                .concat(DEFAULT_EXTENSION);
    }

    private String findResourceName(final String path) {
        final String[] paths = path.split(PATH_DELIMITER);
        return paths[paths.length - 1];
    }
}