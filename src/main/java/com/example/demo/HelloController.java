package com.example.demo;

import domain.Car;
import domain.Rental;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.ListView;
import repository.MemoryRepository;
import repository.RepositoryException;
import repository.SQLCarRepository;
import repository.SQLRentalRepository;
import service.CarService;
import service.RentalService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class HelloController {

    // Elemente pentru secțiunea "Masini"

    //pentru add MASINA
    @FXML
    public Button addMasina;
    public TextField id_inchiriere_de_sters_masina;
    public TextField id_inchiriere_masina_de_modif;
    public TextField brand_inchiriere_masina_de_modif;
    public TextField model_inchiriere_masina_de_modif;
    @FXML
    private ListView<String> lstMessage;
    @FXML
    private TextField id_masina;
    @FXML
    private TextField brand_masina;
    @FXML
    private TextField model_masina;

    //pentru delete MASINA
    @FXML
    public Button deleteMasina;
    @FXML
    private TextField id_masina_de_sters;
    @FXML
    private TextField brand_masina_de_sters;
    @FXML
    private TextField model_masina_de_sters;

    //pentru update MASINA
    @FXML
    public Button update_masina;
    @FXML
    private TextField id_masina_de_modif;
    @FXML
    private TextField brand_masina_de_modif;
    @FXML
    private TextField model_masina_de_modif;

    //pentru getAll MASINA
    @FXML
    public Button afiseazaMasini;

    // Elemente pentru secțiunea "Inchirieri"
    //-----------------------------------------------------------------------------------------------------------------------------------

    //pentru add INCHIRIERE
    @FXML
    public Button addInchiriere;
    @FXML
    private TextField id_inchiriere;
    @FXML
    private TextField data_inceput_inchiriere;
    @FXML
    private TextField data_sfarsit_inchiriere;
    @FXML
    private ListView<Car> cars_for_rental; // ListView for selecting cars in a rental
    private ObservableList<Car> carsObservableList = FXCollections.observableArrayList();


    //pentru delete INCHIRIRERE
    @FXML
    public Button deleteInchiriere;
    @FXML
    private TextField id_inchiriere_de_sters;
    @FXML
    private TextField data_inceput_inchiriere_de_sters;
    @FXML
    private TextField data_sfarsit_inchiriere_de_sters;


    //pentru update INCHIRIERE
    @FXML
    public Button updateInchiriere;
    @FXML
    private TextField id_inchiriere_de_modif;
    @FXML
    private TextField data_de_inceput_de_modificat;
    @FXML
    private TextField data_de_sfarsit_de_modificat;
    @FXML
    private ListView<Car> cars_for_rental_update;

    //pentru getAll INCHIRIERE
    @FXML
    public Button afiseazaInchiriere;



    private ObservableList<String> listObservableString = FXCollections.observableArrayList();

    // Creăm listele de produse și comenzi
    private MemoryRepository<Car> carRepository = new MemoryRepository<>();
    private MemoryRepository<Rental> rentalRepository = new MemoryRepository<>();
    private CarService carService = new CarService(carRepository);
    private RentalService rentalService = new RentalService(rentalRepository);


    // Adăugare masina
    @FXML
    private void adaugaMasina(ActionEvent actionEvent) {
        try {
            int id = Integer.parseInt(id_masina.getText());
            if (id <= 0) throw new IllegalArgumentException("ID-ul trebuie să fie mai mare decât 0.");

            String brand = brand_masina.getText().trim();
            if (brand.isEmpty()) throw new IllegalArgumentException("Brandul nu poate fi gol.");

            String model = model_masina.getText().trim();
            if (model.isEmpty()) throw new IllegalArgumentException("Modelul nu poate fi gol.");

            carService.addCar(id, brand, model);

            Car carNou = new Car(id, brand, model);
            carsObservableList.add(carNou);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Masina a fost adăugat cu succes.");
            alert.show();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Introduceți un număr valid pentru ID.");
            alert.show();
        } catch (DuplicateObjectException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Masina există deja.");
            alert.show();
        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.show();
        } catch (RepositoryException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Eroare la adăugarea masinii.");
            alert.show();
        }
        clearMasinaFields();
    }

    // Ștergere masina
    @FXML
    private void stergeMasina() {
        try {
            int id = Integer.parseInt(id_masina_de_sters.getText());
            if (id <= 0) throw new IllegalArgumentException("ID-ul trebuie să fie mai mare decât 0.");

            String brand = brand_masina_de_sters.getText().trim();
            if (brand.isEmpty()) throw new IllegalArgumentException("Brandul nu poate fi gol.");

            String model = model_masina_de_sters.getText().trim();
            if (model.isEmpty()) throw new IllegalArgumentException("Modelul nu poate fi gol.");

            carService.deleteCar(id, brand, model);

            Car carNou = new Car(id, brand, model);
            carsObservableList.remove(carNou);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Masina a fost stearsa cu succes.");
            alert.show();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Introduceți un număr valid pentru ID.");
            alert.show();
        } catch (DuplicateObjectException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Masina există deja.");
            alert.show();
        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.show();
        } catch (RepositoryException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Eroare la stergerea masinii.");
            alert.show();
        }
        clearStergereMasinaFields();
    }

    // Actualizare masina
    @FXML
    private void updateMasina() {
        try{
            int id = Integer.parseInt(id_masina_de_modif.getText());
            if (id <= 0) throw new IllegalArgumentException("ID-ul trebuie să fie mai mare decât 0.");

            String brand = brand_masina_de_modif.getText().trim();
            if (brand.isEmpty()) throw new IllegalArgumentException("Brandul nu poate fi goală.");

            String model = model_masina_de_modif.getText().trim();
            if (model.isEmpty()) throw new IllegalArgumentException("Modelul nu poate fi gol.");

            Car car = carService.getCarById(id);

            carService.updateCar(id, car.getBrand(), car.getModel(), id, brand, model);

            // Actualizează lista observabilă
            carsObservableList.setAll(carService.getAllCars());

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Produsul a fost actualizat cu succes.");
            alert.show();
        }catch (NumberFormatException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Enter a valid id");
            alert.show();

    } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        clearActualizareMasinaFields();
    }

    // Afișare masini
    @FXML
    public void getAllMasini(ActionEvent actionEvent) {
        listObservableString.clear();
        Collection<Car> lista = carService.getAllCars();
        for (Car p : lista) {
            listObservableString.add(p.toString());
        }

        lstMessage.setItems(listObservableString);
    }

    @FXML
    public void initialize() {
        try {
            SQLCarRepository carRepository = new SQLCarRepository("jdbc:sqlite:Car.db");
            List<Car> cars = new ArrayList<>(carRepository.getAll());
            for (Car c : cars) {
                carService.addCar(c.getId(), c.getBrand(), c.getModel());
            }

            SQLRentalRepository rentalRepository = new SQLRentalRepository("jdbc:sqlite:Rental.db");
            List<Rental> rentals = new ArrayList<>(rentalRepository.getAll());
            for (Rental r : rentals) {
                rentalService.addRental(r.getId(), r.getCar(), r.getStart_date(), r.getEnd_date());
            }

            Collection<Car> availableCars = carService.getAllCars();
            carsObservableList.setAll(availableCars);

            cars_for_rental.setItems(carsObservableList);
            cars_for_rental_update.setItems(carsObservableList);

            cars_for_rental.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            cars_for_rental_update.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        } catch (Exception e) {
            System.err.println("Eroare la inițializare: " + e.getMessage());
            e.printStackTrace();
        }

    }

    //-------------------------------------------------------------------------------------------
    // Adăugare închiriere
    @FXML
    private void adaugaInchiriere() {
        try {
            // Încarcă lista de mașini pentru închiriere
            ObservableList<Car> carsForRentalList = FXCollections.observableArrayList();
            List<Car> cars = (List<Car>) carService.getAllCars(); // presupun că ai o metodă care returnează toate mașinile
            carsForRentalList.addAll(cars);
            cars_for_rental.setItems(carsForRentalList); // adaugă lista în controlul cars_for_rental

            int id = Integer.parseInt(id_inchiriere.getText());
            if (id <= 0) throw new IllegalArgumentException("ID-ul trebuie să fie mai mare decât 0.");

            String dataText_s = data_inceput_inchiriere.getText().trim();
            if (dataText_s.isEmpty()) throw new IllegalArgumentException("Data nu poate fi goală.");

            Date start_date = new SimpleDateFormat("yyyy-MM-dd").parse(dataText_s);

            String dataText_e = data_sfarsit_inchiriere.getText().trim();
            if (dataText_e.isEmpty()) throw new IllegalArgumentException("Data nu poate fi goală.");

            Date end_date = new SimpleDateFormat("yyyy-MM-dd").parse(dataText_e);

            // Permite selectarea unui singur element
            cars_for_rental.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

            Car carRental = cars_for_rental.getSelectionModel().getSelectedItem();
            if (carRental == null) throw new IllegalArgumentException("Selectați o masina.");

            rentalService.addRental(id, carRental, start_date, end_date);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Inchirierea a fost adăugată cu succes.");
            alert.show();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Introduceți un număr valid pentru ID.");
            alert.show();
        } catch (ParseException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Introduceți o dată validă în formatul yyyy-MM-dd.");
            alert.show();
        } catch (DuplicateObjectException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Inchirierea există deja.");
            alert.show();
        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.show();
        } catch (RepositoryException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Eroare la adăugarea comenzii.");
            alert.show();
        }
        clearInchiriereFields();
    }


    // Ștergere închiriere
    @FXML
    private void stergeInchiriere() {
        try {
            int id_r = Integer.parseInt(id_inchiriere_de_sters.getText());
            if (id_r <= 0) throw new IllegalArgumentException("ID-ul trebuie să fie mai mare decât 0.");

            int id = Integer.parseInt(id_inchiriere_de_sters_masina.getText());
            if (id <= 0) throw new IllegalArgumentException("ID-ul trebuie să fie mai mare decât 0.");

            String dataText_sd = data_inceput_inchiriere_de_sters.getText().trim();
            if (dataText_sd.isEmpty()) throw new IllegalArgumentException("Data nu poate fi goală.");

            Date start_date = new SimpleDateFormat("yyyy-MM-dd").parse(dataText_sd);

            String dataText_ed = data_sfarsit_inchiriere_de_sters.getText().trim();
            if (dataText_ed.isEmpty()) throw new IllegalArgumentException("Data nu poate fi goală.");

            Date end_date = new SimpleDateFormat("yyyy-MM-dd").parse(dataText_ed);

            Car carNou = carService.getCarById(id);

            rentalService.deleteRental(id_r, carNou.getBrand(), carNou.getModel(), start_date, end_date);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Inchirierea a fost stearsa cu succes.");
            alert.show();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Introduceți un număr valid pentru ID.");
            alert.show();
        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.show();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        clearStergereInchiriereFields();
    }

    // Actualizare închiriere
    @FXML
    private void updateInchiriere() {
        try {
            int id_r = Integer.parseInt(id_inchiriere_de_modif.getText());
            if (id_r <= 0) throw new IllegalArgumentException("ID-ul trebuie să fie mai mare decât 0.");

            int id = Integer.parseInt(id_inchiriere_masina_de_modif.getText());
            if (id <= 0) throw new IllegalArgumentException("ID-ul trebuie să fie mai mare decât 0.");

            String brand = brand_inchiriere_masina_de_modif.getText().trim();
            if (brand.isEmpty()) throw new IllegalArgumentException("Brandul nu poate fi goală.");

            String model = model_inchiriere_masina_de_modif.getText().trim();
            if (model.isEmpty()) throw new IllegalArgumentException("Modelul nu poate fi gol.");

            String dataText_u = data_de_inceput_de_modificat.getText().trim();
            if (dataText_u.isEmpty()) throw new IllegalArgumentException("Data nu poate fi goală.");

            Date start_date = new SimpleDateFormat("yyyy-MM-dd").parse(dataText_u);

            String dataText_eu = data_de_sfarsit_de_modificat.getText().trim();
            if (dataText_eu.isEmpty()) throw new IllegalArgumentException("Data nu poate fi goală.");

            Date end_date = new SimpleDateFormat("yyyy-MM-dd").parse(dataText_eu);

            Car carVechi = carService.getCarById(id);
            Rental rentalVechi = rentalService.getRentalById(id_r);
            rentalService.updateRental(id_r, id, carVechi.getBrand(), carVechi.getModel(), rentalVechi.getStart_date(), rentalVechi.getEnd_date(), id_r, id, brand, model, start_date, end_date);

            // Mesaj de confirmare
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Inchirierea a fost actualizată cu succes.");
            alert.show();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Introduceți un ID valid.");
            alert.show();
        } catch (ParseException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Introduceți o dată validă în formatul yyyy-MM-dd.");
            alert.show();
        } catch (RepositoryException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Eroare la actualizarea comenzii.");
            alert.show();
        }
        clearActualizareInchiriereFields();
    }

    // Afișare închirieri
    @FXML
    private void getAllRentals() {
        listObservableString.clear();
        Collection<Rental> lista = rentalService.getAllRentals();
        for (Rental c : lista) {
            listObservableString.add(c.toString());
        }

        lstMessage.setItems(listObservableString);
    }

    // Metode de curățare a câmpurilor
    private void clearMasinaFields() {
        id_masina.clear();
        brand_masina.clear();
        model_masina.clear();
    }

    private void clearStergereMasinaFields() {
        id_masina_de_sters.clear();
        brand_masina_de_sters.clear();
        model_masina_de_sters.clear();
    }

    private void clearActualizareMasinaFields() {
        id_masina_de_modif.clear();
        brand_masina_de_modif.clear();
        model_masina_de_modif.clear();
    }

    private void clearInchiriereFields() {
        id_inchiriere.clear();
        data_inceput_inchiriere.clear();
        data_sfarsit_inchiriere.clear();
    }

    private void clearStergereInchiriereFields() {
        id_inchiriere_de_sters.clear();
        id_inchiriere_de_sters_masina.clear();
        data_sfarsit_inchiriere_de_sters.clear();
        data_inceput_inchiriere_de_sters.clear();
    }

    private void clearActualizareInchiriereFields() {
        id_inchiriere_de_modif.clear();
        id_inchiriere_masina_de_modif.clear();
        brand_inchiriere_masina_de_modif.clear();
        model_inchiriere_masina_de_modif.clear();
        data_de_inceput_de_modificat.clear();
        data_de_sfarsit_de_modificat.clear();
    }

    private void incarcaMasiniPentruInchiriere() {
        try {
            // Obține toate mașinile disponibile
            Collection<Car> masini = carService.getAllCars();

            // Adaugă fiecare mașină în lista cars_for_rental
            cars_for_rental.getItems().clear();  // Curăță lista existentă înainte de a adăuga
            cars_for_rental.getItems().addAll(masini);  // Adaugă toate mașinile

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Eroare la încărcarea mașinilor pentru închiriere.");
            alert.show();
        }
    }


    // Afișare mesaje de alertă
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
