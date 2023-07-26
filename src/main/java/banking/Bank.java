package banking;

import java.util.Random;
import java.util.Scanner;

public class Bank {
    Account currentLoggedInAccount;
    AccountManager accountManager;
    Scanner scanner = new Scanner(System.in);

    private static final String MAIN_MENU = """
                    \n1. Create an account
                    2. Log into account
                    0. Exit""";

    private static final String ACCOUNT_MENU = """
                    \n1. Balance
                    2. Add Income
                    3. Do transfer
                    4. Close account
                    5. Log out
                    0. Exit""";

    private static final String INPUT_ERROR = "Please type a correct number";

    public Bank() {
        this.accountManager = new AccountManager();
    }

    public void run() {
        while(true) {
            System.out.println(MAIN_MENU);
            switch (getMenuInput()) {
                case 0 -> exit();
                case 1 -> createAnAccount();
                case 2 -> logIntoAccount();
                default -> System.out.println(INPUT_ERROR);
            }
        }
    }

    private int getMenuInput() {
        int input = -1;
        try {
            input = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return input;
    }

    public void createAnAccount() {
        System.out.println("Your card has been created\n"  +
                "Your card number:");

        Random generator = new Random();
        String cardNumberWithoutChecksum = String.format("400000%09d", generator.nextLong(999999999L));
        String cardNumber = cardNumberWithoutChecksum + addChecksum(new StringBuilder(cardNumberWithoutChecksum));
        System.out.println(cardNumber);

        System.out.println("Your card PIN:");
        String pin = String.format("%04d", generator.nextInt(9999));
        System.out.println(pin);

        accountManager.addAccount(cardNumber, pin);
    }

    public void logIntoAccount() {
        System.out.println("Enter your card number:");
        String tempCardNumber = scanner.nextLine();
        System.out.println("Enter your pin number:");
        String tempPin = scanner.nextLine();

        if(!accountManager.checkNumberAndPin(tempCardNumber, tempPin)) {
            System.out.println("Wrong card number or PIN!");
        }
        else {
            currentLoggedInAccount = new Account(tempCardNumber, tempPin);
            System.out.println("You have successfully logged in!");
            showAccountMenu();
        }
    }

    public void showAccountMenu() {
        boolean backFlag = false;

        while (!backFlag) {
            System.out.println(ACCOUNT_MENU);

            switch (getMenuInput()) {
                case 0 -> backFlag = true;
                case 1 -> System.out.println("Balance: "
                        + accountManager.getBalance(currentLoggedInAccount.getCardNumber()));
                case 2 -> accountManager.addIncome(currentLoggedInAccount.getCardNumber());
                case 3 -> accountManager.doTransfer(currentLoggedInAccount.getCardNumber());
                case 4 -> {
                    accountManager.closeAccount(currentLoggedInAccount.getCardNumber());
                    backFlag = true;
                }
                case 5 -> {
                    System.out.println("You have successfully logged out!\n");
                    backFlag = true;
                }
                default -> System.out.println(INPUT_ERROR);
            }
        }
    }
    public String addChecksum(StringBuilder cardNumberWithoutChecksum) {
        int sum = 0;
        for (int i = 0; i < cardNumberWithoutChecksum.length(); i++) {
            int num = Integer.parseInt(String.valueOf(cardNumberWithoutChecksum.charAt(i)));
            num = i % 2 == 0 ? num * 2 : num;
            sum += num > 9 ? num - 9 : num;
        }
        sum = 10 - sum % 10 == 10 ? 0 : 10 - sum % 10;
        return String.valueOf(sum);
    }

    public void exit() {
        System.out.print("Bye!");
        System.exit(0);
    }
}
