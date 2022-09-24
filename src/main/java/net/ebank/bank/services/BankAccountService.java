package net.ebank.bank.services;




import net.ebank.bank.dtos.*;
import net.ebank.bank.entities.BankAccount;
import net.ebank.bank.entities.CurrentAccount;
import net.ebank.bank.entities.Customer;
import net.ebank.bank.entities.SavingAccount;
import net.ebank.bank.exception.BalanceNotSufficientException;
import net.ebank.bank.exception.BankAccountNotFoundException;
import net.ebank.bank.exception.CustomerNotFoundException;
import net.ebank.bank.exception.NullEntryAmountException;

import java.util.List;

public interface BankAccountService {

    Customer saveCustomer(Customer customer);

    CurrentAccount saveCurrentAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException;

    SavingAccount saveSavingAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException;

    BankAccount getBankAccount(String accountId) throws BankAccountNotFoundException;

    List<Customer> listCustomer();

    void credit(String accountId, double amount, String description) throws BankAccountNotFoundException, NullEntryAmountException;

    void debit(String accountId,double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException;

    void transfer(String accountIdSource, String accountIdDestination, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException, NullEntryAmountException;

    List<BankAccount> listBankAccount();

    void deleteCustomer(Long customerId) throws CustomerNotFoundException;

    BankAccountDTO getBankAccountDTO(String accountId) throws BankAccountNotFoundException;

    CustomerDTO saveOrUpdateCustomer(CustomerDTO customerDTO);

    CustomerDTO getCustomerDTO(Long customerId) throws CustomerNotFoundException;

    List<CustomerDTO> listCustomerDTO();

    CurrentAccountDTO saveCurrentAccountDTO(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException;

    SavingAccountDTO saveSavingAccountDTO(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException;

    List<BankAccountDTO> listBankAccountDTO();

    List<AccountOperationDTO> historyAccount(String accountId);
}
