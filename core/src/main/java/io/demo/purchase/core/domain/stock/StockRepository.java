package io.demo.purchase.core.domain.stock;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepository {
    void add(long slotId, long quantity);

    Optional<Stock> findBySlotId(long slotId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    void updateStock(Stock newStock);
}

