package com.fc.sns.configuration.web;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.regex.Pattern;

@Controller
public class WebController implements ErrorController {

    public static final String ERROR_REQUEST_URI = "javax.servlet.error.request_uri";
    public static final String QUERY_STRING_REGEX = "\\?";
    public static final String FILE_EXTENSION_REGEX = "/(\\.\\w+$)/igm";
    public static final String SLASH = "/";
    public static final String JS_PATH = "js";
    public static final String STATIC_PATH = "static";
    public static final String INDEX = "index.html";

    @GetMapping({"/error"})
    public ResponseEntity<byte[]> index(HttpServletRequest request) {
        String errorUrl = ((String) request.getAttribute(ERROR_REQUEST_URI)).split(QUERY_STRING_REGEX)[0];

        try {
            return ResponseEntity.ok().body(getResourceBody(errorUrl));
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private byte[] getResourceBody(String errorUrl) throws IOException {
        String staticResourcePath = getStaticResourcePath(errorUrl).toString();
        Resource resource = new ClassPathResource(staticResourcePath);

        return FileCopyUtils.copyToByteArray(resource.getInputStream());
    }

    private StringBuffer getStaticResourcePath(String errorUrl) {
        String[] fragments = errorUrl.split(SLASH);
        String lastFragment = fragments[fragments.length - 1];
        StringBuffer path = new StringBuffer(STATIC_PATH).append(SLASH);

        if (isJsUrl(errorUrl)) {
            String[] appendPath = {STATIC_PATH, SLASH, JS_PATH, SLASH, lastFragment};
            return append(path, appendPath);
        }

        if (isStaticUrl(errorUrl)) {
            String appendPath = lastFragment;
            return append(path, appendPath);
        }

        return append(path, INDEX);
    }

    private boolean isStaticUrl(String errorUrl) {
        return Pattern.matches(FILE_EXTENSION_REGEX, errorUrl);
    }

    private boolean isJsUrl(String errorUrl) {
        return errorUrl.contains(String.format("%s%s", SLASH, JS_PATH));
    }

    private StringBuffer append(StringBuffer target, String... paths) {
        for (String path : paths) {
            target.append(path);
        }
        return target;
    }
}

