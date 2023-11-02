package com.cnewbywa.item.error;

public class ItemNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -1533085005961041634L;

	public ItemNotFoundException(String message) {
		super(message);
	}
	
	public ItemNotFoundException(String message, Throwable t) {
		super(message, t);
	}
}
