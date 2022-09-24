package net.ebank.bank.repositories;


import net.ebank.bank.entities.AccountOperation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountOperationRepo extends JpaRepository<AccountOperation, Long> {

    List<AccountOperation> findByBankAccountId(String accountId);

}
