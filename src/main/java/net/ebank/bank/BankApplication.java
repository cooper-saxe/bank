package net.ebank.bank;

import net.ebank.bank.dtos.BankAccountDTO;
import net.ebank.bank.dtos.CurrentAccountDTO;
import net.ebank.bank.dtos.CustomerDTO;
import net.ebank.bank.dtos.SavingAccountDTO;
import net.ebank.bank.exception.CustomerNotFoundException;
import net.ebank.bank.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.stream.Stream;

@SpringBootApplication
public class BankApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankApplication.class, args);
	}

}
