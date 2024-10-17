package ru.practicum.shareit.booking.mapper;

import lombok.SneakyThrows;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.BookingStatus;

@Component
public class BookingStatusMapper {

    @SneakyThrows
    public static BookingStatus toBookingStatus(String state) {
        switch (state) {
            case "ALL":
                return BookingStatus.ALL;
            case "CURRENT":
                return BookingStatus.CURRENT;
            case "PAST":
                return BookingStatus.PAST;
            case "FUTURE":
                return BookingStatus.FUTURE;
            case "WAITING":
                return BookingStatus.WAITING;
            case "REJECTED":
                return BookingStatus.REJECTED;
            case "CANCELED":
                return BookingStatus.CANCELED;
        }
        throw new BadRequestException("Введен некорректный запрос");
    }

    ;
}
