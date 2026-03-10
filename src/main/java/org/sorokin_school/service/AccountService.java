package org.sorokin_school.service;

import org.sorokin_school.AccountProperties;
import org.sorokin_school.console.ConsoleColors;
import org.sorokin_school.exception.BankException;
import org.sorokin_school.model.Account;
import org.sorokin_school.model.User;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AccountService {
    private final Map<Integer, Account> accounts = new HashMap<>();
    private final UserService userService;
    private final AccountProperties accountProperties;
    private int accountIdCounter = 1;

    public AccountService(AccountProperties accountProperties, UserService userService) {
        this.accountProperties = accountProperties;
        this.userService = userService;
    }

    public Account createAccount(int userId) {
        User user = userService.getUser(userId)
                .orElseThrow(() -> new BankException("Пользователь не найден"));

        Account account = new Account(accountIdCounter++, userId, accountProperties.getDefaultAmount());
        accounts.put(account.getId(), account);
        user.getAccountList().add(account);
        return account;
    }

    public void deposit(int accountId, int amount) {
        if (amount <= 0) throw new BankException("Сумма должна быть положительной");
        Account account = findAccountById(accountId);
        account.setMoneyAmount(account.getMoneyAmount() + amount);
    }

    public void withdraw(int accountId, int amount) {
        if (amount <= 0) throw new BankException("Сумма должна быть положительной");
        Account account = findAccountById(accountId);
        if (account.getMoneyAmount() < amount) throw new BankException("Недостаточно средств");
        account.setMoneyAmount(account.getMoneyAmount() - amount);
    }

    public void transfer(int fromId, int toId, int amount) {
        if (amount <= 0) {
            throw new BankException("Сумма перевода должна быть положительной!");
        }
        if (fromId == toId) {
            throw new BankException("Нельзя перевести деньги на тот же самый счет!");
        }

        Account from = findAccountById(fromId);
        Account to = findAccountById(toId);

        if (from.getMoneyAmount() < amount) {
            throw new BankException("Недостаточно средств! Требуется: " + amount +
                    ", в наличии: " + from.getMoneyAmount());
        }

        int amountToReceive = amount;

        if (from.getUserId() != to.getUserId()) {
            double commission = amount * accountProperties.getTransferCommission();
            amountToReceive = (int) (amount - commission);

            System.out.println(ConsoleColors.BLUE + "Межбанковский перевод. Комиссия: "
                    + (amount - amountToReceive) + ConsoleColors.RESET);
        } else {
            System.out.println("Перевод между своими счетами. Без комиссии.");
        }

        from.setMoneyAmount(from.getMoneyAmount() - amount);
        to.setMoneyAmount(to.getMoneyAmount() + amountToReceive);
    }

    public void closeAccount(int accountId) {
        Account accountToClose = findAccountById(accountId);

        User owner = userService.getUser(accountToClose.getUserId())
                .orElseThrow(() -> new BankException("Владелец счета не найден"));

        List<Account> userAccounts = owner.getAccountList();
        if (userAccounts.size() <= 1) {
            throw new BankException("Ошибка: нельзя закрыть единственный счет пользователя id=" + owner.getId());
        }

        Account beneficiaryAccount = userAccounts.stream()
                .filter(acc -> acc.getId() != accountId)
                .findFirst()
                .orElseThrow();

        int balanceToTransfer = accountToClose.getMoneyAmount();
        beneficiaryAccount.setMoneyAmount(beneficiaryAccount.getMoneyAmount() + balanceToTransfer);

        userAccounts.remove(accountToClose);
        accounts.remove(accountId);

        System.out.printf("Счет #%d закрыт. Остаток %d у.е. переведен на счет #%d.%n",
                accountId, balanceToTransfer, beneficiaryAccount.getId());
    }

    private Account findAccountById(int id) {
        Account account = accounts.get(id);
        if (account == null) {
            throw new BankException("Счет с ID " + id + " не найден!");
        }
        return account;
    }

    public boolean hasAnyAccounts() {
        return !accounts.isEmpty();
    }
}