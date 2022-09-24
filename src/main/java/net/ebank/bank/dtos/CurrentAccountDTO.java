package net.ebank.bank.dtos;

import lombok.Data;
import net.ebank.bank.enums.AccountStatus;


import java.util.Date;

@Data
public class CurrentAccountDTO extends BankAccountDTO{
    private String id;
    private double balance;
    private Date createdAt;
    private AccountStatus status;
    private double overDraft;
    private CustomerDTO customerDTO;
}
