package repository;

import domain.Car;
import domain.Rental;
import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class SQLRentalRepository extends MemoryRepository<Rental> {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    AbstractRepository<Car> repoCar;
    private String dbLocation = "jdbc:sqlite:Rental.db";
    private Connection connection = null;

    public SQLRentalRepository(String dbLocation) {
        this.dbLocation = dbLocation;
        openConnection();
        createSchema();
        loadData();
    }

    private ArrayList<Car> convertToList(String line) {
        ArrayList<Car> lista = new ArrayList<>();
        if (line == null || line.trim().isEmpty()) {
            return lista;
        }
        String[] tokens = line.split(";");
        for (String token : tokens) {
            String[] parts = token.split(",");
            if (parts.length == 3) {
                try {
                    int id = Integer.parseInt(parts[0].trim());
                    String brand = parts[1].trim();
                    String model = parts[2].trim();
                    lista.add(new Car(id, brand, model));
                } catch (NumberFormatException e) {
                    System.err.println("Eroare la parsarea masinii: " + token);
                }
            } else {
                System.err.println("Format incorect pentru masina: " + token);
            }
        }
        return lista;
    }

    public String toStringArray(List<Car> cars) {
        return cars.stream()
                .limit(4)
                .map(c -> String.format("%d,%s,%s,%d", c.getId(), c.getBrand(), c.getModel()))
                .collect(Collectors.joining(";"));
    }

    private void loadData() {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * from Rental");
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                Car p = convertToList(rs.getString("Car")).get(0);
                Date start_date = new Date(rs.getTimestamp("Start_date").getTime());
                Date end_date = new Date(rs.getTimestamp("End_date").getTime());
                Rental r = new Rental(rs.getInt("ID"), p, start_date, end_date);
                this.data.add(r);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void openConnection() {
        try {
            SQLiteDataSource ds = new SQLiteDataSource();
            ds.setUrl(dbLocation);
            if (connection == null || connection.isClosed())
                connection = ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // o inchidem
    public void closeConnection() {
        try {
            if (connection != null)
                connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createSchema() {
        try (final Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Rental(ID int, Car varchar(400), Start_date DATETIME, End_date DATETIME);");
        } catch (SQLException e) {
            System.err.println("[ERROR] createSchema : " + e.getMessage());
        }
    }

    @Override
    public void add(Rental elem) throws RepositoryException {
        try {
            super.add(elem);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO Rental VALUES (?, ?, ?, ? )")) {
            statement.setInt(1, elem.getId());
            List<Car> carsList = new ArrayList<>(Collections.singletonList(elem.getCar()));
            statement.setObject(2, carsList);
            // Convertește data la SQL DATETIME
            Timestamp start_date = new Timestamp(elem.getStart_date().getTime());
            statement.setString(3, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(start_date));
            Timestamp end_date = new Timestamp(elem.getStart_date().getTime());
            statement.setString(4, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(end_date));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Rental elem) throws RepositoryException {
        try {
            super.delete(elem);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        try(PreparedStatement statement = connection.prepareStatement("DELETE FROM Rental WHERE ID = ? AND Car = ? AND Start_date = ? AND End_date = ?")){
            statement.setInt(1, elem.getId());
            statement.setObject(2, elem.getCar());
            Timestamp start_date = new Timestamp(elem.getStart_date().getTime());
            statement.setString(3, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(start_date));
            Timestamp end_date = new Timestamp(elem.getStart_date().getTime());
            statement.setString(4, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(end_date));
            statement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void update(Rental elem, Rental elem1) throws RepositoryException {
        if (elem1 == null) {
            throw new IllegalArgumentException("Inchirierea nu poate fi nulla!");
        }

        try {
            if (connection == null || connection.isClosed()) {
                throw new SQLException("Conexiunea la baza de date este închisă.");
            }

            connection.setAutoCommit(false); // Începe tranzacția

            // Pregătește UPDATE pentru comanda
            List<Car> carsList = new ArrayList<>(Collections.singletonList(elem.getCar()));
            Timestamp start_date = new Timestamp(elem1.getStart_date().getTime());
            Timestamp end_date = new Timestamp(elem1.getEnd_date().getTime());

            try (PreparedStatement updateComanda = connection.prepareStatement("UPDATE Rental SET Car = ?, Start_date = ?, End_date = ? WHERE ID = ?")) {

                updateComanda.setString(1, carsList.toString());
                updateComanda.setString(2, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(start_date));
                updateComanda.setString(3, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(end_date));
                updateComanda.setInt(4, elem1.getId());

                int rowsUpdated = updateComanda.executeUpdate();

                if (rowsUpdated == 0) {
                    throw new RepositoryException("Nu s-a găsit nicio Inchiriere cu ID-ul: " + elem1.getId());
                }

                connection.commit();
            } catch (SQLException e) {
                if (connection != null) {
                    try {
                        connection.rollback();
                    } catch (SQLException rollbackEx) {
                        e.addSuppressed(rollbackEx);
                    }
                }
                throw new RepositoryException("Eroare la actualizarea inchirierii: " + e.getMessage());
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            throw new RepositoryException("Eroare la nivelul conexiunii: " + ex.getMessage());
        }
    }
}
