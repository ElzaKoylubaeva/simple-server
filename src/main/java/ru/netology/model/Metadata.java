package ru.netology.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = {"method", "path"})
public class Metadata {

    private String method;

    private String path;
}
