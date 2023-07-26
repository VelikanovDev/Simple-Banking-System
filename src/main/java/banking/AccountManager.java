package banking;

import java.sql.*;
import java.util.Scanner;

public class AccountManager {
    private final Scanner scanner = new Scanner(System.in);

    public void addAccount(String cardNumber, String pin) {
        String sql = "INSERT INTO card (number, pin) VALUES (?,?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cardNumber);
            ps.setString(2, pin);

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Integer getBalance(String cardNumber) {
        String sql = "SELECT balance FROM card WHERE number = " + cardNumber;
        int balance = -1;
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(sql);

            if(rs.next()) {
                balance = rs.getInt("balance");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return balance;
    }

    public void addIncome(String cardNumber) {
        String sql = "UPDATE card SET balance = balance + ? WHERE number = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            System.out.println("Enter income:");
            int sum = scanner.nextInt();

            if(sum > 0) {
                ps.setInt(1, sum);
                ps.setString(2, cardNumber);
                ps.executeUpdate();
                System.out.println("Income was added!");
            }
            else {
                System.out.println("Please, write correct sum to add");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void doTransfer(String cardNumber) {
        String sql = "UPDATE card SET balance = balance + ? WHERE number = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            System.out.println("Transfer\nEnter card number:");
            String enteredCardNumber = scanner.next();

            if(!isNumberCreatedByLuhn(enteredCardNumber)) {
                System.out.println("Probably you made a mistake in the card number. Please try again!");
            }

            else if(!checkIfCardExist(enteredCardNumber)) {
                System.out.println("Such a card does not exist.");
            }

            else {
                System.out.println("Enter how much money you want to transfer:");
                int sumToAdd = scanner.nextInt();

                if(sumToAdd > getBalance(cardNumber)) System.out.println("Not enough money!");

                else if(enteredCardNumber.equals(cardNumber)) {
                    System.out.println("You can't transfer money to the same account!");
                }
                else {
                    ps.setInt(1, sumToAdd);
                    ps.setString(2, enteredCardNumber);
                    ps.executeUpdate();
                    withdraw(cardNumber, sumToAdd);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean checkIfCardExist(String cardNumber) {
        String sql = "SELECT * FROM card WHERE number = " + cardNumber;
        ResultSet rs;
        try(Connection conn = Database.getConnection();
            Statement stmt = conn.createStatement()) {

            rs = stmt.executeQuery(sql);
            return rs.next();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    private void withdraw(String cardNumber, int sum) {
        String sql = "UPDATE card SET balance = balance - ? WHERE number = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if(getBalance(cardNumber) - sum >= 0 ) {
                ps.setInt(1, sum);
                ps.setString(2, cardNumber);
                ps.executeUpdate();
                System.out.println("Success!");
            }
            else {
                System.out.println("Please try again");
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    private boolean isNumberCreatedByLuhn(String cardNumber) {
        String[] stringNumbersOfCard = cardNumber.split("");
        int lastNum = Integer.parseInt(stringNumbersOfCard[stringNumbersOfCard.length - 1]);
        int sumByLuhn = 0;

        for (int i = 1; i < stringNumbersOfCard.length; i++) {
            int num = Integer.parseInt(stringNumbersOfCard[i - 1]);
            if (i % 2 != 0) num *= 2;
            if (num > 9) num -= 9;
            sumByLuhn += num;
        }
        return  (sumByLuhn + lastNum) % 10 == 0;
    }

    public void closeAccount(String cardNumber) {
        String sql = "DELETE FROM card WHERE number = " + cardNumber;

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
            System.out.println("The account has been closed!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public boolean checkNumberAndPin(String cardNumber, String pin) {
        String sql = "SELECT * FROM card WHERE number = " + cardNumber + " AND pin = " + pin;
        ResultSet rs;
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {

            rs = stmt.executeQuery(sql);

            return rs.next();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}
