package com.hzbankwealth.observa.api.model.logging;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Histogram {
    private long total;
    private List<Bucket> histograms;
} 