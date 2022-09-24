package net.ebank.bank.services;

import lombok.AllArgsConstructor;
import net.ebank.bank.dtos.*;
import net.ebank.bank.entities.*;
import net.ebank.bank.enums.AccountStatus;
import net.ebank.bank.enums.OperationType;
import net.ebank.bank.exception.BalanceNotSufficientException;
import net.ebank.bank.exception.BankAccountNotFoundException;
import net.ebank.bank.exception.CustomerNotFoundException;
import net.ebank.bank.exception.NullEntryAmountException;
import net.ebank.bank.mappers.DtoMapper;
import net.ebank.bank.repositories.AccountOperationRepo;
import net.ebank.bank.repositories.BankAccountRepo;
import net.ebank.bank.repositories.CustomerRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Transactional
public class BankAccountServiceImpl implements BankAccountService{

    private BankAccountRepo bankAccountRepo;
    private CustomerRepo customerRepo;
    private AccountOperationRepo accountOperationRepo;
    private DtoMapper dtoMapper;

    private static final Logger log = LoggerFactory.getLogger(BankAccountServiceImpl.class);

    @Override
    public Customer saveCustomer(Customer customer) {
        log.info("saving new customer");
        Customer savedCustomer = customerRepo.save(customer);
        return savedCustomer;
    }

    @Override
    public CurrentAccount saveCurrentAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {
        log.info("saving current account");
        Customer customer = customerRepo.findById(customerId).orElse(null);

        if (customer == null){
            throw new CustomerNotFoundException("customer not found for this id ::"+customerId);
        } else {
            CurrentAccount currentAccount = new CurrentAccount();

            currentAccount.setId(UUID.randomUUID().toString());
            currentAccount.setStatus(AccountStatus.CREATED);
            currentAccount.setCreatedAt(new Date());
            currentAccount.setBalance(initialBalance);
            currentAccount.setOverDraft(overDraft);
            currentAccount.setCustomer(customer);
            CurrentAccount savedCurrentAccount = bankAccountRepo.save(currentAccount);
            return savedCurrentAccount;
        }
    }

    @Override
    public SavingAccount saveSavingAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException {
        log.info("saved saving account");
        Customer customer = customerRepo.findById(customerId).orElse(null);

        if (customer == null){
            throw new CustomerNotFoundException("customer not found for this id ::"+customerId);
        } else {
            SavingAccount savingAccount = new SavingAccount();

            savingAccount.setId(UUID.randomUUID().toString());
            savingAccount.setStatus(AccountStatus.CREATED);
            savingAccount.setCreatedAt(new Date());
            savingAccount.setBalance(initialBalance);
            savingAccount.setInterestRate(interestRate);
            savingAccount.setCustomer(customer);
            SavingAccount savedSavingAccount = bankAccountRepo.save(savingAccount);
            return savedSavingAccount;
        }
    }

    @Override
    public BankAccount getBankAccount(String accountId) throws BankAccountNotFoundException {
        log.info("get bank account by id ::"+accountId);
        BankAccount bankAccount = bankAccountRepo.findById(accountId).
                orElseThrow(()->new BankAccountNotFoundException("bank account not found for this id ::"+accountId));

        return bankAccount;
    }

    @Override
    public List<Customer> listCustomer() {
        log.info("list customers");
        return customerRepo.findAll();
    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException, NullEntryAmountException {
        log.info("credit operation");
        BankAccount bankAccount = getBankAccount(accountId);

        if (amount == 0.0 ){
            throw new NullEntryAmountException("null entry amount");
        }else {
            AccountOperation accountOperation = new AccountOperation();
            accountOperation.setBankAccount(bankAccount);
            accountOperation.setOperationDate(new Date());
            accountOperation.setType(OperationType.CREDIT);
            accountOperation.setAmount(amount);
            accountOperation.setDescription(description);
            accountOperationRepo.save(accountOperation);
            bankAccount.setBalance(bankAccount.getBalance() + amount);
            bankAccountRepo.save(bankAccount);
        }
    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
        log.info("debit operation");
        BankAccount bankAccount = getBankAccount(accountId);

        if (bankAccount.getBalance()<amount){
            throw new BalanceNotSufficientException("balance not sufficient");
        }else {
            AccountOperation accountOperation = new AccountOperation();
            accountOperation.setBankAccount(bankAccount);
            accountOperation.setOperationDate(new Date());
            accountOperation.setType(OperationType.DEBIT);
            accountOperation.setAmount(amount);
            accountOperation.setDescription(description);
            accountOperationRepo.save(accountOperation);
            bankAccount.setBalance(bankAccount.getBalance()-amount);
            bankAccountRepo.save(bankAccount);
        }
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException, NullEntryAmountException {
        log.info("transfer operation");
        debit(accountIdDestination,amount,description);
        credit(accountIdSource,amount,description);
    }

    @Override
    public List<BankAccount> listBankAccount() {
        log.info("list bank account");
       return bankAccountRepo.findAll();
    }

    @Override
    public void deleteCustomer(Long customerId) throws CustomerNotFoundException {
        customerRepo.findById(customerId).orElseThrow(()-> new CustomerNotFoundException("customer not found"));
        customerRepo.deleteById(customerId);
    }

    //mappers

    @Override
    public BankAccountDTO getBankAccountDTO(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount = getBankAccount(accountId);

        if (bankAccount instanceof CurrentAccount){
            CurrentAccount currentAccount = (CurrentAccount) bankAccount;
            return dtoMapper.fromCurrentAccount(currentAccount);
        }else {
            SavingAccount savingAccount = (SavingAccount) bankAccount;
            return dtoMapper.fromSavingAccount(savingAccount);
        }
    }

    @Override
    public CustomerDTO saveOrUpdateCustomer(CustomerDTO customerDTO){
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepo.save(customer);
        CustomerDTO customerDTO1 = dtoMapper.fromCustomer(savedCustomer);

        return customerDTO1;
    }

/*    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO){
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepo.save(customer);
        CustomerDTO customerDTO1 = dtoMapper.fromCustomer(savedCustomer);

        return customerDTO1;
    }*/

    @Override
    public CustomerDTO getCustomerDTO(Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(()-> new CustomerNotFoundException("customer not found for id ::" +customerId));
        CustomerDTO customerDTO = dtoMapper.fromCustomer(customer);
        return customerDTO;
    }


    @Override
    public List<CustomerDTO> listCustomerDTO(){
        List<Customer> customers = customerRepo.findAll();
        //convert customer to customerDTO
        List<CustomerDTO> customerDTOS = customers
                .stream().map(customer -> dtoMapper.fromCustomer(customer)).collect(Collectors.toList());
        return customerDTOS;
    }


    @Override
    public CurrentAccountDTO saveCurrentAccountDTO(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {
        log.info("saving current account");
        Customer customer = customerRepo.findById(customerId).orElse(null);

        if (customer == null){
            throw new CustomerNotFoundException("customer not found for this id ::"+customerId);
        } else {
            CurrentAccount currentAccount = new CurrentAccount();

            currentAccount.setId(UUID.randomUUID().toString());
            currentAccount.setStatus(AccountStatus.CREATED);
            currentAccount.setCreatedAt(new Date());
            currentAccount.setBalance(initialBalance);
            currentAccount.setOverDraft(overDraft);
            currentAccount.setCustomer(customer);
            CurrentAccount savedCurrentAccount = bankAccountRepo.save(currentAccount);
            return dtoMapper.fromCurrentAccount(savedCurrentAccount);
        }
    }

    @Override
    public SavingAccountDTO saveSavingAccountDTO(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException {
        log.info("saved saving account");
        Customer customer = customerRepo.findById(customerId).orElse(null);

        if (customer == null){
            throw new CustomerNotFoundException("customer not found for this id ::"+customerId);
        } else {
            SavingAccount savingAccount = new SavingAccount();

            savingAccount.setId(UUID.randomUUID().toString());
            savingAccount.setStatus(AccountStatus.CREATED);
            savingAccount.setCreatedAt(new Date());
            savingAccount.setBalance(initialBalance);
            savingAccount.setInterestRate(interestRate);
            savingAccount.setCustomer(customer);
            SavingAccount savedSavingAccount = bankAccountRepo.save(savingAccount);
            return dtoMapper.fromSavingAccount(savedSavingAccount);
        }
    }

    @Override
    public List<BankAccountDTO> listBankAccountDTO(){
        List<BankAccount> bankAccounts = bankAccountRepo.findAll();
        List<BankAccountDTO> bankAccountDTOS = bankAccounts.stream().map(bankAccount -> {
            if (bankAccount instanceof SavingAccount){
                SavingAccount savingAccount = (SavingAccount) bankAccount;
                return dtoMapper.fromSavingAccount(savingAccount);
            }else {
                CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                return dtoMapper.fromCurrentAccount(currentAccount);
            }
        }).collect(Collectors.toList());

        return bankAccountDTOS;
    }

    @Override
    public List<AccountOperationDTO> historyAccount(String accountId){
        List<AccountOperation> accountOperations = accountOperationRepo.findByBankAccountId(accountId);
        List<AccountOperationDTO> accountOperationDTOS = accountOperations
                .stream().map(accountOperation -> dtoMapper.fromAccountOperation(accountOperation)).collect(Collectors.toList());
        return accountOperationDTOS;
    }


}
