package com.buyflow.erp.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.buyflow.erp.Dto.InboundDto;
import com.buyflow.erp.Service.InboundService;

import lombok.RequiredArgsConstructor;
import com.buyflow.erp.Dto.InboundListResponse;
import com.buyflow.erp.Dto.InboundSummaryDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/inbounds")
public class InboundController {

    private final InboundService inboundService;

@GetMapping
public InboundListResponse getInbounds() {

    List<InboundDto> items =
            inboundService.getInbounds();

    return InboundListResponse.builder()
            .items(items)
            .pagination(
                    InboundListResponse.Pagination
                            .builder()
                            .page(1)
                            .size(items.size())
                            .totalElements((long) items.size())
                            .totalPages(1)
                            .build()
            )
            .build();
}
@GetMapping("/summary")
public InboundSummaryDto getSummary() {

    List<InboundDto> items =
            inboundService.getInbounds();

    int expected = 0;
    int partial = 0;
    int completed = 0;

    for (InboundDto item : items) {

        if ("PARTIAL".equals(item.getStatus())) {
            partial++;
        } else if ("COMPLETED".equals(item.getStatus())) {
            completed++;
        } else {
            expected++;
        }
    }

    int total = items.size();

    int progressRate =
            total == 0
                    ? 0
                    : (completed * 100) / total;

    return InboundSummaryDto.builder()
            .todayExpected(expected)
            .yesterdayDifference(0)
            .delayed(0)
            .partial(partial)
            .progressRate(progressRate)
            .tabCounts(
                    InboundSummaryDto.TabCounts.builder()
                            .EXPECTED(expected)
                            .PARTIAL(partial)
                            .COMPLETED(completed)
                            .build()
            )
            .build();
}
}