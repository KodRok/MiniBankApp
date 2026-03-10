package org.sorokin_school.console;

import org.sorokin_school.exception.BankException;
import org.sorokin_school.model.Account;
import org.sorokin_school.model.User;
import org.sorokin_school.service.AccountService;
import org.sorokin_school.service.UserService;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class ConsoleListener {

    private final UserService userService;
    private final AccountService accountService;

    public ConsoleListener(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("=== MiniBank System Started ===");
        System.out.println("Доступные команды: USER_CREATE, SHOW_ALL_USERS, ACCOUNT_CREATE,");
        System.out.println("ACCOUNT_DEPOSIT, ACCOUNT_WITHDRAW, ACCOUNT_TRANSFER, ACCOUNT_CLOSE, EXIT");

        while (running) {
            try {
                System.out.print("\nВведите команду: ");
                String input = scanner.nextLine();
                if (input == null || input.isEmpty()) {
                    continue;
                }

                String command = input.toUpperCase().trim();

                if ("EXIT".equals(command)) {
                    System.out.println("До свидания!");
                    running = false;
                    continue;
                }

                if (command.equals("USER_CREATE") || command.equals("ACCOUNT_CREATE")
                        || command.equals("SHOW_ALL_USERS")) {

                    if (command.equals("ACCOUNT_CREATE") && userService.getUsers().isEmpty()) {
                        System.out.println("Ошибка: Нельзя создать счет, " +
                                "так как в системе нет ни одного пользователя.");
                    } else {
                        handleCommand(command, scanner);
                    }
                    continue;
                }

                if (userService.getUsers().isEmpty()) {
                    System.out.println("Ошибка: В системе нет пользователей. " +
                            "Сначала выполните USER_CREATE.");
                    continue;
                }

                if (!accountService.hasAnyAccounts()) {
                    System.out.println("Ошибка: В системе нет ни одного открытого счета. " +
                            "Сначала выполните ACCOUNT_CREATE.");
                    continue;
                }

                handleCommand(command, scanner);

            } catch (BankException e) {
                System.out.println(ConsoleColors.RED + "ОШИБКА БАНКА: " + e.getMessage() + ConsoleColors.RESET);
            } catch (NumberFormatException e) {
                System.out.println(ConsoleColors.RED + "ОШИБКА: Введите число!" + ConsoleColors.RESET);
            } catch (Exception e) {
                System.out.println(ConsoleColors.YELLOW + "Критическая ошибка: " + e.getMessage() + ConsoleColors.RESET);
            }
        }
    }

    private void handleCommand(String command, Scanner scanner) {

        switch (command) {
            case "USER_CREATE":
                processUserCreate(scanner);
                break;

            case "SHOW_ALL_USERS":
                showAllUsers();
                break;

            case "ACCOUNT_CREATE":
                processAccountCreate(scanner);
                break;

            case "ACCOUNT_TRANSFER":
                processAccountTransfer(scanner);
                break;

            case "ACCOUNT_WITHDRAW":
                processAccountWithdraw(scanner);
                break;

            case "ACCOUNT_DEPOSIT":
                processAccountDeposit(scanner);
                break;

            case "ACCOUNT_CLOSE":
                processAccountClose(scanner);
                break;
            case "EXIT":
                System.out.print("До свидания!");
                break;

            default:
                System.out.println("Неизвестная команда.");
        }
    }

    private void processAccountCreate(Scanner scanner) {
        System.out.println("Введите ID пользователя:");
        int userId = Integer.parseInt(scanner.nextLine());
        accountService.createAccount(userId);
        System.out.println("Счет открыт.");
    }

    private void processAccountTransfer(Scanner scanner) {
        System.out.println("Введите ID счета отправителя:");
        int fromId = Integer.parseInt(scanner.nextLine());

        System.out.println("Введите ID счета получателя:");
        int toId = Integer.parseInt(scanner.nextLine());

        System.out.println("Введите сумму перевода:");
        int amount = Integer.parseInt(scanner.nextLine());

        accountService.transfer(fromId, toId, amount);
        System.out.println("Перевод успешно выполнен.");
    }

    private void processAccountWithdraw(Scanner scanner) {
        System.out.println("Введите ID счета:");
        int withdrawAccId = Integer.parseInt(scanner.nextLine());

        System.out.println("Введите сумму для снятия:");
        int withdrawAmount = Integer.parseInt(scanner.nextLine());

        accountService.withdraw(withdrawAccId, withdrawAmount);
        System.out.println("Снятие выполнено успешно.");
    }

    private void processAccountDeposit(Scanner scanner) {
        System.out.print("Введите ID счета: ");
        int depositAccId = Integer.parseInt(scanner.nextLine());

        System.out.println("Введите сумму для пополнения:");
        int depositAmount = Integer.parseInt(scanner.nextLine());

        accountService.deposit(depositAccId, depositAmount);
        System.out.println(ConsoleColors.GREEN + "Счет успешно пополнен на " + depositAmount + ConsoleColors.RESET);
    }

    private void processAccountClose(Scanner scanner) {
        System.out.println("Введите ID счета для закрытия:");
        try {
            int idToClose = Integer.parseInt(scanner.nextLine());
            accountService.closeAccount(idToClose);
            System.out.println("Операция завершена.");
        } catch (NumberFormatException e) {
            throw new BankException("ID счета должен быть числом!");
        }
    }

    private void processUserCreate(Scanner scanner) {
        System.out.println("Введите логин:");
        String login = scanner.nextLine();
        userService.createUser(login);
        System.out.println("Пользователь создан.");
    }

    private void showAllUsers() {
        var users = userService.getUsers();
        if (users.isEmpty()) {
            System.out.println("В системе пока нет пользователей.");
            return;
        }

        System.out.println("=== СПИСОК ВСЕХ ПОЛЬЗОВАТЕЛЕЙ ===");
        for (User user : users) {
            System.out.printf("ID: %d | Логин: %-15s | Счетов: %d%n",
                    user.getId(), user.getLogin(), user.getAccountList().size());

            if (!user.getAccountList().isEmpty()) {
                for (Account acc : user.getAccountList()) {
                    System.out.printf("   └─ Счет #%d: %d у.е.%n",
                            acc.getId(), acc.getMoneyAmount());
                }
            }
            System.out.println("---------------------------------");
        }
    }
}