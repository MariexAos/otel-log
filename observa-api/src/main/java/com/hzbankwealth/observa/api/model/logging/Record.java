package com.hzbankwealth.observa.api.model.logging;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Record {
    private String time;
    private String log;
    private String namespace;
    private String pod;
    private String container;
} 