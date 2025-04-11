package br.com.asgard.core;

import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConversionException;

import java.beans.Visibility;
import java.util.logging.Logger;

public interface EventMessagingConsumer<T> {
    void handler(T messageEvent, MessageHeaders messageHeaders, Visibility visibility);

    default void conversionExceptionHandler(final String logCode, final MessageConversionException e){
        System.out.println("log");
    }

    default void exceptionHandler(final String logCode, final Exception e){
        System.out.println("log");
    }

    void exceptionHandler();

    void conversionExceptionHandler(MessageConversionException exception);
}
