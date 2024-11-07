package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ExceptionMessageTest {

    @Test
    void test_AlreadyExistsExceptionMessage() {
        String expectedMessage = "Некорректный запрос";
        AlreadyExistsException exception = new AlreadyExistsException(expectedMessage);
        assertNotNull(exception);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void test_BookingExceptionMessage() {
        String expectedMessage = "Некорректный запрос";
        BookingException exception = new BookingException(expectedMessage);
        assertNotNull(exception);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void test_DataExceptionMessage() {
        String expectedMessage = "Ошибка даты";
        DataException exception = new DataException(expectedMessage);
        assertNotNull(exception);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void test_DuplicateParameterException() {
        String expectedMessage = "Данные дублируются";
        DuplicateParameterException exception = new DuplicateParameterException(expectedMessage);
        assertNotNull(exception);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void test_NotAvailableExceptionMessage() {
        String expectedMessage = "Ошибка";
        NotAvailableException exception = new NotAvailableException(expectedMessage);
        assertNotNull(exception);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void test_NotFoundExceptionMessage() {
        String expectedMessage = "Не найдено";
        NotFoundException exception = new NotFoundException(expectedMessage);
        assertNotNull(exception);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void test_ValidationExceptionMessage() {
        String expectedMessage = "Ошибка валидации";
        ValidationException exception = new ValidationException(expectedMessage);
        assertNotNull(exception);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void test_BadRequestExceptionMessage() {
        String expectedMessage = "Плохой запрос";
        BadRequestException exception = new BadRequestException(expectedMessage);
        assertNotNull(exception);
        assertEquals(expectedMessage, exception.getMessage());
    }
}
