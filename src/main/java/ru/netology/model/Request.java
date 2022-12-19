package ru.netology.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Reader;
import java.util.Map;

@Data
@Builder
@EqualsAndHashCode(of = {"method", "path"})
public class Request {

    private String method;

    private String path;

    private Map<String, String> headers;

    private Reader body;
}
