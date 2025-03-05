package repository;

import domain.Car;
import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SQLCarRepository extends MemoryRepository<Car> {

    private String dbLocation = "jdbc:sqlite:Car.db";
    private Connection connection = null;

    public SQLCarRepository(String dbLocation) {
        this.dbLocation = dbLocation;
        openConnection();
        createSchema();
        loadData();
    }

    public void openConnection() {
        try {
            SQLiteDataSource ds = new SQLiteDataSource();
            ds.setUrl(dbLocation);
            if (connection == null || connection.isClosed())
                connection = ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void createSchema() {
        try {
            try (final Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Car (" +
                        "ID INT PRIMARY KEY, " +
                        "Brand VARCHAR(200), " +
                        "Model VARCHAR(200));");
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] createSchema : " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
        public void loadData(){
        try {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * from Car"); ResultSet rs = statement.executeQuery();) {
                while (rs.next()) {
                    Car p = new Car(rs.getInt("ID"), rs.getString("Brand"), rs.getString("Model"));
                    this.data.add(p);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void delete(Car elem) throws RepositoryException {
        try {
            super.delete(elem);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM Car WHERE ID = ? AND Brand = ? AND Model = ?")) {
            statement.setInt(1, elem.getId());
            statement.setString(2, elem.getBrand());
            statement.setString(3, elem.getModel());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add(Car elem) throws RepositoryException {
        try {
            super.add(elem);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO Car VALUES (?, ?, ?)")) {
            statement.setInt(1, elem.getId());
            statement.setString(2, elem.getBrand());
            statement.setString(3, elem.getModel());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Car elem, Car elem1) throws RepositoryException {
        try {
            connection.setAutoCommit(false);

            // Corectăm interogarea SQL
            String updateQuery = "UPDATE Car SET Brand = ?, Model = ? WHERE ID = ?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                // Setăm valorile parametrilor corect
                updateStatement.setString(1, elem1.getBrand());
                updateStatement.setString(2, elem1.getModel());
                updateStatement.setInt(3, elem1.getId());

                int rowsUpdated = updateStatement.executeUpdate();

                if (rowsUpdated == 0) {
                    System.out.println("Nu s-a găsit masina cu ID-ul: " + elem1.getId());
                    throw new SQLException("Nu s-a găsit masina cu ID-ul: " + elem1.getId());
                } else {
                    System.out.println("Masina cu ID-ul " + elem1.getId() + " a fost actualizat cu succes.");
                }

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                System.err.println("Eroare la actualizarea masinii: " + e.getMessage());
                throw new SQLException("Eroare la actualizarea masinii: " + e.getMessage());
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Eroare la actualizarea masinii: " + ex.getMessage());
        }
    }


    @Override
    public Collection<Car> getAll() {
        ArrayList<Car> cars = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * from Car")) {
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
}

