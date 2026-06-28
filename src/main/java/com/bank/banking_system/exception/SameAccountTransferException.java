package com.bank.banking_system.exception;

public class SameAccountTransferException extends RuntimeException
{
    public SameAccountTransferException(String message)
    {
        super(message);
    }
}
