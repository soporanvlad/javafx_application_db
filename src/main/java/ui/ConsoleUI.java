package ui;

import domain.Car;
import domain.Rental;
import repository.*;
import service.CarService;
import service.RentalService;
import settings.Settings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleUI {
    private CarService carService;
    private RentalService rentalService;
    private Scanner scanner;
    private String dbLocation = "jdbc:sqlite:Car.db";

    public ConsoleUI() throws RepositoryException {
        MemoryRepository<Car> carRepository = new MemoryRepository<>();
        MemoryRepository<Rental> rentalRepository = new MemoryRepository<>();
//        addInitialCars(carRepository);
//        addInitialRental(rentalRepository, carRepository);
        this.carService = new CarService(carRepository);
        this.rentalService = new RentalService(rentalRepository);
        this.scanner = new Scanner(System.in);
    }

    private void addInitialRental(MemoryRepository<Rental> rentalRepository, MemoryRepository<Car> carReposity) throws RepositoryException {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
            Car car1 = carReposity.find(1);
            Car car2 = carReposity.find(2);
            Car car3 = carReposity.find(3);
            Car car4 = carReposity.find(4);
            Car car5 = carReposity.find(5);

            if (car1 != null) {
                rentalRepository.add(new Rental(10, car1, dateFormat.parse("03-04-2023"), dateFormat.parse("04-05-2023")));
            }
            if (car2 != null) {
                rentalRepository.add(new Rental(20, car2, dateFormat.parse("05-05-2024"), dateFormat.parse("06-06-2024")));
            }
            if (car3 != null) {
                rentalRepository.add(new Rental(30, car3, dateFormat.parse("07-07-2022"), dateFormat.parse("14-07-2023")));
            }
            if (car4 != null) {
                rentalRepository.add(new Rental(40, car4, dateFormat.parse("03-01-2024"), dateFormat.parse("22-03-2024")));
            }
            if (car5 != null) {
                rentalRepository.add(new Rental(50, car5, dateFormat.parse("01-05-2022"), dateFormat.parse("05-05-2022")));
            }
            System.out.println("Inital rentals added to repository.");
        } catch (ParseException e) {
            System.out.println("Eroare la parsarea datei" + e.getMessage());
        }
    }

    private void addInitialCars(MemoryRepository<Car> carRepository) throws RepositoryException {
        carRepository.add(new Car(1, "Toyota", "Corolla"));
        carRepository.add(new Car(2, "Honda", "Civic"));
        carRepository.add(new Car(3, "Ford", "Focus"));
        carRepository.add(new Car(4, "BMW", "320d"));
        carRepository.add(new Car(5, "Audi", "A3"));
        System.out.println("Initial cars added to repository.");
    }

    private void selectRepositoryType() throws RepositoryException {
        String repositoryType = Settings.getProperty("repositoryType");
        AbstractRepository<Car> carRepository;
        AbstractRepository<Rental> rentalRepository;

        if ("db".equalsIgnoreCase(repositoryType)) {
            String dbLocationCar = Settings.getProperty("dbLocationCar");
            String dbLocationRental = Settings.getProperty("dbLocationRental");
            carRepository = new SQLCarRepository(dbLocationCar);
            rentalRepository = new SQLRentalRepository(dbLocationRental);
        } else if ("memory".equalsIgnoreCase(repositoryType)) {
            carRepository = new MemoryRepository<>();
            rentalRepository = new MemoryRepository<>();
            addInitialCars((MemoryRepository<Car>) carRepository);
            addInitialRental((MemoryRepository<Rental>) rentalRepository, (MemoryRepository<Car>) carRepository);
        } else {
            throw new RuntimeException("Invalid repository type in settings.properties: " + repositoryType);
        }

        this.carService = new CarService(carRepository);
        this.rentalService = new RentalService(rentalRepository);
    }


    public void run() throws RepositoryException {
        boolean running = true;

        selectRepositoryType();

        while (running) {
            printMenu();
            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    addCar();
                    break;
                case "2":
                    addRental();
                    break;
                case "3":
                    viewRentalsForCar();
                    break;
                case "4":
                    viewAllCars();
                    break;
                case "5":
                    deleteCars();
                    break;
                case "6":
                    deleteRentals();
                    break;
                case "7":
                    updateCars();
                    break;
                case "8":
                    updateRentals();
                    break;
                case "9":
                    raport_1();
                    break;
                case "10":
                    raport_2();
                    break;
                case "11":
                    raport_3();
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("Opțiune invalidă! Încercați din nou.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\nMeniu:");
        System.out.println("1. Adaugă mașină");
        System.out.println("2. Adaugă închiriere");
        System.out.println("3. Afișează închirieri pentru o mașină");
        System.out.println("4. Afisează toate mașinile");
        System.out.println("5. Sterge o masina");
        System.out.println("6. Sterge o inchiriere");
        System.out.println("7. Actualizeaza informatiile despre o masina");
        System.out.println("8. Actualizeaza informatiile despre o inchiriere");
        System.out.println("9. Cele mai des inchiriate masini");
        System.out.println("10. Numarul de inchirieri efectuate in fiecare luna a anului");
        System.out.println("11. Masinile care au fost inchiriate cel mai des");
        System.out.println("0. Ieșire");
        System.out.print("Alegeți o opțiune: ");
    }

    private void deleteCars() {
        try {
            System.out.println("Intoduceti ID-ul masinii: ");
            int id = Integer.parseInt(scanner.nextLine());

            Car car = carService.getCarById(id);
            if (car == null) {
                System.out.println("Masina descrisa nu exista !");
                return;
            }

            carService.deleteCar(id, car.getBrand(), car.getModel());
            System.out.println("Masina a fost stearsa cu succes !");

        } catch (NumberFormatException e) {
            System.out.println("ID-ul trebuie sa fie un numar !" + e.getMessage());
        } catch (RepositoryException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private void updateCars() {
        try {
            System.out.println("Introduceti ID-ul masinii: ");
            int id = Integer.parseInt(scanner.nextLine());

            Car car = carService.getCarById(id);

            if (car == null) {
                System.out.println("Masina descrisa nu exista !");
                return;
            }

            System.out.println("Introduceti modelul actualizat: ");
            String model = scanner.nextLine();

            System.out.println("Intorduceti brandul actualizat: ");
            String brand = scanner.nextLine();

            carService.updateCar(id, car.getBrand(), car.getModel(), id, brand, model);
        } catch (NumberFormatException e) {
            System.out.println("ID-ul trebuie sa fie un numar !" + e.getMessage());
        } catch (RepositoryException e) {
            System.out.println("Eroare:" + e.getMessage());
        }

    }

    private void addCar() {
        try {
            System.out.print("Introduceți ID-ul mașinii: ");
            int id = Integer.parseInt(scanner.nextLine());

            System.out.print("Introduceți marca mașinii: ");
            String brand = scanner.nextLine();

            System.out.print("Introduceți modelul mașinii: ");
            String model = scanner.nextLine();

            carService.addCar(id, brand, model);
            System.out.println("Mașina a fost adăugată cu succes!");

        } catch (NumberFormatException e) {
            System.out.println("ID-ul trebuie să fie un număr.");
        } catch (RepositoryException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private void deleteRentals() {
        try {
            // Prompt the user for the rental ID, car brand, and model
            System.out.print("Introduceți ID-ul închirierii: ");
            int idRental = Integer.parseInt(scanner.nextLine());

            System.out.println("Introduceti ID-ul masinii: ");
            int idCar = Integer.parseInt(scanner.nextLine());

            Car car = carService.getCarById(idCar);
            Rental rental = rentalService.getRentalById(idRental);
            if (car == null || rental == null) {
                System.out.println("Entitatea intodusa nu exista!");
                return;
            }

            rentalService.deleteRental(idRental, car.getBrand(), car.getModel(), rental.getStart_date(), rental.getEnd_date());

            System.out.println("Închirierea a fost ștearsă cu succes!");

        } catch (NumberFormatException e) {
            System.out.println("ID-ul trebuie să fie un număr.");
        } catch (RepositoryException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private void updateRentals() {
        try {
            System.out.println("Introduceti id-ul masinii cautate: ");
            int idCar = Integer.parseInt(scanner.nextLine());

            System.out.println("Introduceti id-ul inchirierii cautate: ");
            int idRental = Integer.parseInt(scanner.nextLine());

            Rental rental = rentalService.getRentalById(idRental);
            Car car = carService.getCarById(idCar);
            if (car == null || rental == null) {
                System.out.println("Entitatea cautata nu exista !");
                return;
            }

            System.out.println("Introduceti brand-ul noii masini: ");
            String brand = scanner.nextLine();

            System.out.println("Intoruceti noul model al masinii: ");
            String model = scanner.nextLine();

            System.out.print("Introduceți noua data de început (format dd-MM-yyyy): ");
            Date startDate = parseDate(scanner.nextLine());

            System.out.print("Introduceți noua data de sfârșit (format dd-MM-yyyy): ");
            Date endDate = parseDate(scanner.nextLine());

            rentalService.updateRental(idRental, idCar, car.getBrand(), car.getModel(), rental.getStart_date(), rental.getEnd_date(), idRental, idCar, brand, model, startDate, endDate);

        } catch (NumberFormatException e) {
            System.out.println("ID-ul trebuie sa fie nu numar !" + e.getMessage());
        } catch (ParseException e) {
            System.out.println("Data nu este valida !" + e.getMessage());
        } catch (RepositoryException e) {
            System.out.println("Eroare la utilizarea functiilor: " + e.getMessage());
        }
    }

    private void addRental() {
        try {
            System.out.print("Introduceți ID-ul închirierii: ");
            int rentalId = Integer.parseInt(scanner.nextLine());

            System.out.print("Introduceți ID-ul mașinii: ");
            int carId = Integer.parseInt(scanner.nextLine());

            Car car = carService.getCarById(carId);
            if (car == null) {
                System.out.println("Mașina cu ID-ul specificat nu există.");
                return;
            }

            System.out.print("Introduceți data de început (format dd-MM-yyyy): ");
            Date startDate = parseDate(scanner.nextLine());

            System.out.print("Introduceți data de sfârșit (format dd-MM-yyyy): ");
            Date endDate = parseDate(scanner.nextLine());

            rentalService.addRental(rentalId, car, startDate, endDate);
            System.out.println("Închirierea a fost adăugată cu succes!");

        } catch (NumberFormatException e) {
            System.out.println("ID-urile trebuie să fie numere.");
        } catch (ParseException e) {
            System.out.println("Formatul datei este invalid.");
        } catch (RepositoryException e) {
            System.out.println("Eroare: " + e.getMessage());
        }
    }

    private void viewRentalsForCar() {
        try {
            System.out.print("Introduceți ID-ul mașinii: ");
            int carId = Integer.parseInt(scanner.nextLine());

            Car car = carService.getCarById(carId);
            if (car == null) {
                System.out.println("Mașina cu ID-ul specificat nu există.");
                return;
            }

            List<Rental> rentals = rentalService.getRentalsForCar(car);
            if (rentals.isEmpty()) {
                System.out.println("Nu există închirieri pentru această mașină.");
            } else {
                System.out.println("Închirieri pentru mașina " + car.getBrand() + " " + car.getModel() + ":");
                for (Rental rental : rentals) {
                    System.out.println(rental);
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("ID-ul trebuie să fie un număr.");
        }
    }

    private Date parseDate(String dateStr) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        return format.parse(dateStr);
    }

    private void viewAllCars() {
        List<Car> cars = (List<Car>) carService.getAllCars();
        if (cars.isEmpty()) {
            System.out.println("Nu există mașini înregistrate.");
        } else {
            System.out.println("Lista de mașini:");
            for (Car car : cars) {
                System.out.println(car);
            }
        }
    }

    public void raport_1() {
        List<Map<String, Object>> raport = rentalService.raportul_1();
        if (raport == null || raport.isEmpty()) {
            System.out.println("Nu exista masini in inchirieri.");
            return;
        }

        System.out.println("Raport: Numar de masini");
        raport.forEach(entry -> {
            Object carObj = entry.get("car");
            Object numarInchirieriObj = entry.get("numarInchirieri");

            if (carObj instanceof Car && numarInchirieriObj instanceof Integer) {
                Car car = (Car) carObj;
                Integer numarInchirieri = (Integer) numarInchirieriObj;

                System.out.println("Masina: " + car.getBrand() + " " + car.getModel() + ", Numar inchirieri: " + numarInchirieri);
            } else {
                System.out.println("Date invalide in raport.");
            }
        });
    }


    public void raport_2() {
        Map<Car, Map<String, Integer>> raport = rentalService.raport_2();
        if (raport.isEmpty()) {
            System.out.println("Nu există mașini închiriate.");
            return;
        }

        System.out.println("Raport: Număr de mașini închiriate, pe lună:");
        raport.forEach((car, inchirieriPeLuna) -> {
            System.out.println("Mașina: " + car.getBrand() + " " + car.getModel());
            inchirieriPeLuna.forEach((luna, numarInchirieri) ->
                    System.out.println("  Luna: " + luna + ", Număr închirieri: " + numarInchirieri)
            );
        });
    }

    public void raport_3(){
        try {
            List<Map.Entry<Car, Integer>> masiniSortate = rentalService.raportul_3();

            if (masiniSortate.isEmpty()) {
                System.out.println("Nu există mașini închiriate!");
                return;
            }

            System.out.println("Mașini sortate descrescător după numărul total de zile închiriate:");
            for (Map.Entry<Car, Integer> entry : masiniSortate) {
                Car car = entry.getKey();
                Integer numarZile = entry.getValue();
                System.out.println("Mașina: " + car.getBrand() + " " + car.getModel() + ", Zile închiriate: " + numarZile);
            }
        } catch (Exception e) {
            System.out.println("Eroare la afișarea mașinilor sortate: " + e.getMessage());
        }
    }

}
