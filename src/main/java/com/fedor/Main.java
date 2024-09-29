package com.fedor;

public class Main {
    public static void main(String[] args) {
        CLI cli = new CLI();

        System.out.println("Enter a command, type help to see the full list");

        while (cli.isRunning()) {
            try {
                cli.parseCommand();
            } catch (NumberFormatException e) {
                System.out.println("Invalid ID");
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Missing argument for command");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        System.out.println("Bye!");
    }
}