package org.apache.coyote;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RequestHandler {
    private static final Set<String> STATIC_RESOURCE_EXTENSIONS = Set.of("css", "js");
    private static final String STATIC_RESOURCE_ROOT_PATH = "static/";
    private static final String PATH_DELIMITER = "/";

    // mapping handler method
    public String handle(final HttpRequest httpRequest) throws IOException {
        final String path = httpRequest.getPath();
        final String[] paths = path.split(PATH_DELIMITER);

        if (paths.length == 0) {
            return handleRoot();
        }

        final String resourceName = paths[paths.length - 1];
        if (resourceName.contains(".")) {
            return handleSimpleResource(resourceName);
        }

        return handleURL(httpRequest);
    }

    private String handleRoot() {
        final String responseBody = "Hello world!";
        return HttpResponseGenerator.getOkResponse("text/html", responseBody);
    }

    private String handleSimpleResource(final String resourceName) throws IOException {
        final URL resourceURL = getClass().getClassLoader().getResource(findResourcePath(resourceName));
        final Path resourcePath = Path.of(resourceURL.getPath());
        final String responseBody = Files.readString(resourcePath);
        final String mimeType = Files.probeContentType(resourcePath);

        return HttpResponseGenerator.getOkResponse(mimeType, responseBody);
    }

    // TOOD: change naming
    private String handleURL(final HttpRequest httpRequest) throws IOException {
        final String uri = httpRequest.getUrl();
        if (uri.contains("login")) {
            return processLoginRequest(httpRequest);
        }

        if (uri.contains("register")) {
            return processRegisterRequest(httpRequest);
        }

        throw new IllegalCallerException("유효하지 않은 기능입니다.");
    }

    private String processLoginRequest(final HttpRequest httpRequest) throws IOException {
        final String query = httpRequest.getPath().split("\\?")[1];
        if (query == null) {
            return handleSimpleResource("login.html");
        }

        final String[] params = query.split("&");
        final String account = params[0].split("=")[1];
        final String password = params[1].split("=")[1];

        final Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);
        if (userOptional.isEmpty()) {
            return handleSimpleResource("login.html");
        }

        final User user = userOptional.get();
        if (user.checkPassword(password)) {
            return HttpResponseGenerator.getFoundResponse("http://localhost:8080/index.html");
        }

        return handleSimpleResource("401.html");
    }

    private String processRegisterRequest(final HttpRequest httpRequest) throws IOException {
        if (Objects.equals(httpRequest.getMethod(), "GET")) {
            return handleSimpleResource("register.html");
        }

        if (Objects.equals(httpRequest.getMethod(), "POST")) {
            return processRegisterPostRequest(httpRequest);
        }

        return handleSimpleResource("401.html");
    }

    private String processRegisterPostRequest(final HttpRequest httpRequest) {
        return "string";
    }

    private String findResourcePath(final String resourcePath) {
        final String[] fileNames = resourcePath.split("\\.");
        final String extension = fileNames[1];

        if (STATIC_RESOURCE_EXTENSIONS.contains(extension)) {
            return STATIC_RESOURCE_ROOT_PATH.concat(extension).concat(PATH_DELIMITER).concat(resourcePath);
        }

        return STATIC_RESOURCE_ROOT_PATH.concat(resourcePath);
    }
}
