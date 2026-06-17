package com.buyflow.erp.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InboundSummaryDto {

    private Integer todayExpected;
    private Integer yesterdayDifference;
    private Integer delayed;
    private Integer partial;
    private Integer progressRate;

    private TabCounts tabCounts;

    @Getter
    @Builder
    public static class TabCounts {

        @JsonProperty("EXPECTED")
        private Integer EXPECTED;

        @JsonProperty("PARTIAL")
        private Integer PARTIAL;

        @JsonProperty("COMPLETED")
        private Integer COMPLETED;
    }
}