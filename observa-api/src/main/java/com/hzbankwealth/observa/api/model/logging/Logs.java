package com.hzbankwealth.observa.api.model.logging;

import com.hzbankwealth.observa.api.model.LogEntity;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Logs {
    private long total;
    private List<LogEntity> records;
} 