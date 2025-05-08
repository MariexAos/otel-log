package com.hzbankwealth.observa.api.model.logging;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Bucket {
    private String time;
    private long count;
} 