package ru.art.platform.api.model;

import lombok.*;

@Value
@Builder
public class Project {
    private final Long id;
    private final String title;
    private final String gitUrl;
    private final String jiraUrl;
}
