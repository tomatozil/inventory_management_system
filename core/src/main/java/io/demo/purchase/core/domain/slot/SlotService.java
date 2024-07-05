package io.demo.purchase.core.domain.slot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SlotService {

    private final SlotReader slotReader;

    @Autowired
    public SlotService(SlotReader slotReader) {
        this.slotReader = slotReader;
    }

    // slot 리스트 (근데 이제 날짜별)
    public List<Slot> findList(LocalDateTime date) {
        return slotReader.findList(date);
    }

    // slot 상세 정보
    public SlotDetail find(Long slotId) {
        return slotReader.find(slotId);
    }
}
