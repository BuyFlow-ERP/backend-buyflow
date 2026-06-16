package com.buyflow.erp.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.buyflow.erp.Entity.StockHistory;
import com.buyflow.erp.Repository.StockHistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockHistoryServiceImpl
        implements StockHistoryService {

    private final StockHistoryRepository stockHistoryRepository;

    @Override
    public List<StockHistory> getStockHistory() {

        return stockHistoryRepository
                .findAllByOrderByHistoryIdDesc();
    }

    @Override
    public List<StockHistory> getStockHistoryByType(
            String historyType) {

        return stockHistoryRepository
                .findByHistoryTypeOrderByHistoryIdDesc(
                        historyType);
    }
}
