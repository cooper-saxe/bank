package net.ebank.bank.dtos;

import lombok.Data;
import net.ebank.bank.enums.OperationType;

import java.util.Date;

@Data
public class AccountOperationDTO {
    private Long id;
    private Date operationDate;
    private double amount;
    private String description;
    private OperationType type;
}
