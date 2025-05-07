package com.hzbankwealth.observa.api.model.logging;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Statistics {
    private long logs;
    private long containers;
} 