package ru.netology.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Reader;
import java.util.Map;

@Data
@Builder
@ToString(exclude = { "body" })
@EqualsAndHashCode(of = {"method", "path"})
public class Request {

    private String method;

    private String path;

    private Map<String, String> headers;

    private Map<String, String> queryParams;

    private Reader body;

    public String getQueryParam(String name) {
        return queryParams.get(name);
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }
}
