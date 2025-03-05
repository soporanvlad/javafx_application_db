package JDBC;

import domain.Car;
import org.sqlite.SQLiteDataSource;
import repository.RepositoryException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

public class JDBC_car {

    // url-ul bazei de date
    private static final String JDBC_URL = "jdbc:sqlite:Car.db";

    private Connection conn = null;

    // deschidem conexiunea cu baza de date
    private void openConnection() {
        try {
            SQLiteDataSource ds = new SQLiteDataSource();
            ds.setUrl(JDBC_URL);
            if (conn == null || conn.isClosed())
                conn = ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // inchidem conexiunea cu baza de date
    private void closeConnection() {
        try {
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // creeam tabelul de Masini
    void createSchema() {
        try {
            try (final Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Car(ID int PRIMARY KEY, Brand varchar(400), Model varchar(400));");
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] createSchema : " + e.getMessage());
        }
    }

    // initializam tabela cu valori aleatorii
    public void initTables() {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO Car values (?, ?, ?);")) {
            for (int i = 1; i <= 100; i++) {
                Car car = generateRandomCar(i);
                stmt.setInt(1, car.getId());
                stmt.setString(2, car.getBrand());
                stmt.setString(3, car.getModel());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // functia de add ptr baza de date
    public void add(Car car) throws RepositoryException {
        try {
            try (PreparedStatement statement = conn.prepareStatement("INSERT INTO Car VALUES (?, ?, ?)")) {
                statement.setInt(1, car.getId());
                statement.setString(2, car.getBrand());
                statement.setString(3, car.getModel());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // functia de delete ptr baza de date
    public void remove(int id) {

        try {
            try (PreparedStatement statement = conn.prepareStatement("DELETE FROM Car WHERE id=?")) {
                statement.setInt(1, id);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // select * din baza de date
    public ArrayList<Car> getAll() {
        ArrayList<Car> cars = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * from Car;")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Car p = new Car(rs.getInt(1), rs.getString(2), rs.getString(3));
                cars.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cars;
    }

    // update pe baza de date
    public void updateEntity(Car car) {

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement updatedCar = conn.prepareStatement("UPDATE Car SET Brand = ?, Model = ? WHERE id = ?")) {
                updatedCar.setString(2, car.getBrand());
                updatedCar.setString(3, car.getModel());
                updatedCar.executeUpdate();

                conn.commit();
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // generam date random
    private Car generateRandomCar(int id) {
        String[] brandCar = {"Toyota", "Honda","BMW","Audi", "Subaru", "Renault", "Opel", "Skoda", "Fiat", "Buick","Cadillac"};
        String[] modelCar = {"A4", "3 Series", "5 Series", "Impreza", "Civic", "Corolla", "500C", "124 Spider", "Accord", "Octavia", "Astra", "Giulia", "Corsa", "RCZ"};
        Random random = new Random();

        // Generare
        String brand_r = brandCar[random.nextInt(brandCar.length)];
        String model_r = modelCar[random.nextInt(modelCar.length)];

        return new Car(id, brand_r, model_r);
    }

    public static void main(String[] args) throws RepositoryException {
        JDBC_car db_example = new JDBC_car();
        db_example.openConnection();
        db_example.createSchema();
        db_example.initTables();  /// cum asta populam baza de date cu 100 de valori, ii dau com dupa o rulare
        db_example.closeConnection();

    }
}

