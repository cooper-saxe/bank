package net.ebank.bank.entities;

import lombok.*;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue("CC")
public class CurrentAccount extends BankAccount{
    private double overDraft;
}
