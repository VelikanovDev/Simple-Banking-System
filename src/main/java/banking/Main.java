package banking;

public class Main {

    public static void main(String[] args) {
        // Program arguments: -fileName db.s3db
        Database.createNewDatabase(args[1]);

        Bank bank = new Bank();
        bank.run();
    }

}