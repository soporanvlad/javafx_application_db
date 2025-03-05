package JDBC;

import domain.Car;
import domain.Rental;
import org.sqlite.SQLiteDataSource;
import repository.AbstractRepository;
import repository.RepositoryException;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

public class JDBC_rental {

    private Random random = new Random();

    private static final String JDBC_URL = "jdbc:sqlite:Rental.db";
    private Connection conn = null;

    AbstractRepository<Car> carRepo;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Open connection
    private void openConnection() {
        try {
            SQLiteDataSource ds = new SQLiteDataSource();
            ds.setUrl(JDBC_URL);
            if (conn == null || conn.isClosed()) {
                conn = ds.getConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Close connection
    private void closeConnection() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create schema
    void createSchema() {
        try {
            try (final Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Rental (ID INT PRIMARY KEY, Car VARCHAR(400), Start_date DATETIME, End_date DATETIME);");
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] createSchema: " + e.getMessage());
        }
    }

    // Clear table before adding data
    public void clearTable() {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM Rental;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Convert a list of cars to a string
    public String toStringArray(List<Car> cars) {
        return cars.stream()
                .map(car -> String.format("%d,%s,%s", car.getId(), car.getBrand(), car.getModel()))
                .collect(Collectors.joining(";")); // Separator
    }

    private Car generateRandomCar(int id) {
        String[] brandCar = {"Toyota", "Honda","BMW","Audi", "Subaru", "Renault", "Opel", "Skoda", "Fiat", "Buick","Cadillac"};
        String[] modelCar = {"A4", "3 Series", "5 Series", "Impreza", "Civic", "Corolla", "500C", "124 Spider", "Accord", "Octavia", "Astra", "Giulia", "Corsa", "RCZ"};
        Random random = new Random();

        // Generare
        String brand_r = brandCar[random.nextInt(brandCar.length)];
        String model_r = modelCar[random.nextInt(modelCar.length)];

        return new Car(id, brand_r, model_r);
    }

    // Generate random rentals
    public void generateRandomRentals() {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO Rental VALUES (?, ?, ?, ?)");) {
            // Generare random pentru 100 de închirieri
            for (int i = 1; i <= 100; i++) {
                int numarCars = random.nextInt(5) + 1;  // 1-5 mașini per închiriere
                List<Car> carRental = new ArrayList<>();

                // Generare aleatorie de mașini pentru fiecare închiriere
                for (int j = 0; j < numarCars; j++) {
                    int carId = random.nextInt(100) + 1;  // ID aleatoriu pentru fiecare mașină
                    Car randomCar = generateRandomCar(carId);  // Generare mașină random
                    carRental.add(randomCar);
                }

                // Convertirea listei de mașini într-un șir de caractere
                String carString = toStringArray(carRental);

                // Generare date de început și sfârșit pentru închiriere
                LocalDateTime start_date = LocalDateTime.now().plusDays(random.nextInt(100));
                String formattedStartDate = start_date.format(formatter);

                LocalDateTime end_date = start_date.plusDays(10);
                String formattedEndDate = end_date.format(formatter);

                // Setarea parametrilor în PreparedStatement
                stmt.setInt(1, i);  // ID-ul închirierii
                stmt.setString(2, carString);  // Șirul de caractere cu mașinile
                stmt.setString(3, formattedStartDate);  // Data de început
                stmt.setString(4, formattedEndDate);  // Data de sfârșit

                stmt.executeUpdate();  // Executarea inserării în baza de date
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void add(Rental rental) {
        try (PreparedStatement statement = conn.prepareStatement("INSERT INTO Rental VALUES (?, ?, ?, ?)");) {
            statement.setInt(1, rental.getId());
            String rentalString = toStringArray(Collections.singletonList(rental.getCar()));
            statement.setString(2, rentalString);
            statement.setString(3, formatter.format(rental.getStart_date().toInstant()));
            statement.setString(4, formatter.format(rental.getEnd_date().toInstant()));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void remove(int id) {
        try (PreparedStatement statement = conn.prepareStatement("DELETE FROM Rental WHERE ID=?")) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Rental> getAll() {
        ArrayList<Rental> rentals = new ArrayList<>();
        try (PreparedStatement statement = conn.prepareStatement("SELECT * FROM Rental");
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("ID");
                String rentalString = rs.getString("Car");

                ArrayList<Car> cars = convertToList(rentalString);
                Car car = cars.isEmpty() ? null : cars.get(0);

                Timestamp startTimestamp = rs.getTimestamp("Start_date");
                Date startDate = new Date(startTimestamp.getTime());

                Timestamp endTimestamp = rs.getTimestamp("End_date");
                Date endDate = new Date(endTimestamp.getTime());

                rentals.add(new Rental(id, car, startDate, endDate));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return rentals;
    }

    private ArrayList<Car> convertToList(String line) {
        ArrayList<Car> lista = new ArrayList<>();
        if (line == null || line.trim().isEmpty()) {
            return lista;
        }

        String[] tokens = line.split(";"); // Separator
        for (String token : tokens) {
            String[] parts = token.split(","); // Format: id,brand,model
            if (parts.length == 3) {
                try {
                    int id = Integer.parseInt(parts[0].trim());
                    String brand = parts[1].trim();
                    String model = parts[2].trim();
                    lista.add(new Car(id, brand, model));
                } catch (NumberFormatException e) {
                    System.err.println("[Error] Failed to parse car data: " + token);
                }
            } else {
                System.err.println("[Error] Invalid car format: " + token);
            }
        }
        return lista;
    }

    public void update(Rental rental) throws RepositoryException {
        if (rental == null) {
            throw new IllegalArgumentException("Rental cannot be null!");
        }
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement updatedRental = conn.prepareStatement("UPDATE Rental SET Car = ?, Start_date = ?, End_date = ? WHERE ID = ?")) {
                String rentalString = toStringArray(Collections.singletonList(rental.getCar()));
                updatedRental.setString(1, rentalString);
                updatedRental.setString(2, formatter.format(rental.getStart_date().toInstant()));
                updatedRental.setString(3, formatter.format(rental.getEnd_date().toInstant()));
                updatedRental.setInt(4, rental.getId());

                int rowsUpdated = updatedRental.executeUpdate();

                if (rowsUpdated == 0) {
                    throw new SQLException("No rental found with ID: " + rental.getId());
                }
            }
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            throw new RepositoryException("Error updating rental: " + e.getMessage());
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        JDBC_rental dbExample = new JDBC_rental();
        dbExample.openConnection();
        dbExample.createSchema();
        dbExample.clearTable();
        dbExample.generateRandomRentals();
        dbExample.closeConnection();
    }
}
