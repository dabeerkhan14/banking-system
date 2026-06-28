package com.bank.banking_system.exception;

public class DuplicateAccountException extends RuntimeException
{
    public DuplicateAccountException(String message)
    {
        super(message);
    }
}
