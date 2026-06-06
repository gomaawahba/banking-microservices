package com.banking.user.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    private String id;
    private String accountNumber;
    private String iban;
    private AccountType accountType;
    private String status;
    private String currency;

    public enum AccountType {
        CHECKING, SAVINGS, BUSINESS
    }
}