package database;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Parser parser = new Parser();
        while (true) {
            System.out.print(">> ");
            String command = scanner.nextLine();
            parser.parse(command);
        }
    }
}