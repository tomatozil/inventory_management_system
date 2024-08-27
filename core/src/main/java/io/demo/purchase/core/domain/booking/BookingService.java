package io.demo.purchase.core.domain.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

    private final BookingWriter bookingWriter;

    @Autowired
    public BookingService(BookingWriter bookingWriter) {
        this.bookingWriter = bookingWriter;
    }

    public long add(long userId, long slotId) {
        // userid, slotid append
        return bookingWriter.append(userId, slotId);
    }
}
