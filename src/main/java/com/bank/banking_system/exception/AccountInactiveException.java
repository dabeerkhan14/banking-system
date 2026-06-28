package com.bank.banking_system.exception;

public class AccountInactiveException extends RuntimeException
{
    public AccountInactiveException(String message)
    {
        super(message);
    }
}
