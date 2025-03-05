package main;

import repository.RepositoryException;
import ui.ConsoleUI;

import java.io.IOException;


public class main {
    public static void main(String[] args) throws RepositoryException, IOException, ClassNotFoundException {
        ConsoleUI ui = new ConsoleUI();
        ui.run();
    }
}
