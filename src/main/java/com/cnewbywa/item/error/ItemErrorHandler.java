package com.cnewbywa.item.error;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;

@ControllerAdvice
@Slf4j
public class ItemErrorHandler {

	@ExceptionHandler(ItemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
	public void handleItemNotFoundException(RuntimeException re) {
		log.warn(re.getMessage(), re);
	}
	
	@ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public void handleException(RuntimeException re) {
		log.error(re.getMessage(), re);
	}
}
