package service;

import domain.Car;
import domain.Rental;
import repository.AbstractRepository;
import repository.RepositoryException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class RentalService {

    private AbstractRepository<Rental> rentalRepository;

    public RentalService(AbstractRepository<Rental> rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    public void addRental(int id, Car car, Date start_date, Date end_date) throws RepositoryException {
        if (start_date.after(end_date)) {
            throw new RepositoryException("Data de început trebuie să fie înaintea datei de sfârșit.");
        }

        boolean hasOverlap = rentalRepository.getAll().stream()
                .anyMatch(rental -> rental.getCar().getId() == car.getId() &&
                        datesOverlap(rental.getStart_date(), rental.getEnd_date(), start_date, end_date));

        if (hasOverlap) {
            throw new RepositoryException("Perioada de închiriere se suprapune!");
        }

        rentalRepository.add(new Rental(id, car, start_date, end_date));
    }

    private boolean datesOverlap(Date start1, Date end1, Date start2, Date end2) {
        return !end1.before(start2) && !end2.before(start1); // Verificare suprapunere reală
    }

    public List<Rental> getRentalsForCar(Car car) {
        // Filtrează închirierile pentru o anumită mașină
        return rentalRepository.getAll().stream()
                .filter(r -> r.getCar().getId() == car.getId())
                .collect(Collectors.toList());
    }

    public void deleteRental(int id, String brand, String model, Date start_date, Date end_date) throws RepositoryException {
        Car car = new Car(id, brand, model);
        Rental rental = new Rental(id, car, start_date,end_date);
        rentalRepository.delete(rental);
    }

    public void updateRental(int id_r1, int id1, String brand1, String model1, Date start_date1, Date end_date1, int id_r2, int id2, String brand2, String model2, Date start_date2, Date end_date2) throws RepositoryException{
        Car car1 = new Car(id1, brand1, model1);
        Car car2 = new Car(id2, brand2, model2);
        Rental rental1 = new Rental(id_r1, car1, start_date1, end_date1);
        Rental rental2 = new Rental(id_r2, car2, start_date2, end_date2);
        rentalRepository.update(rental1, rental2);
    }

    public Rental getRentalById(int id) {
        return rentalRepository.find(id);
    }

    public Collection<Rental> getAllRentals() {
        return rentalRepository.getAll();
    }

    public List<Map<String, Object>> raportul_1() {
        Map<String, Integer> rentalCount = new HashMap<>();

        // Iterează prin toate închirierile din rentalRepository
        for (Rental rental : rentalRepository.getAll()) {
            // Verifică dacă rental are o mașină validă asociată
            if (rental.getCar() != null) {
                Car car = rental.getCar();

                // Construiește cheia pentru car (id_brand_model)
                String carKey = car.getId() + "_" + car.getBrand() + "_" + car.getModel();
                System.out.println("Car Key: " + carKey); // Log pentru cheie

                // Inițializează contorul cu 0 dacă nu există
                int nr = rentalCount.getOrDefault(carKey, 0);

                // Crește contorul
                nr++;

                // Actualizează rentalCount cu noua valoare a contorului
                rentalCount.put(carKey, nr);
            } else {
                System.err.println("Rental fără mașină asociată: " + rental);
            }
        }

        // Transformă map-ul în listă de rezultate și sortează după numărul de închirieri
        return rentalCount.entrySet()
                .stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue())) // Sortează descrescător după numărul de închirieri
                .map(entry -> {
                    // Împarte cheia în id, brand și model
                    String[] carDetails = entry.getKey().split("_");
                    if (carDetails.length == 3) {
                        // Log pentru detaliile mașinii
                        System.out.println("Car Details: " + Arrays.toString(carDetails));

                        // Construiește un map cu informațiile despre mașină și numărul de închirieri
                        Map<String, Object> carInfo = new HashMap<>();
                        carInfo.put("id", carDetails[0]);
                        carInfo.put("brand", carDetails[1]);
                        carInfo.put("model", carDetails[2]);
                        carInfo.put("rentalCount", entry.getValue());

                        return carInfo;
                    } else {
                        // Dacă cheia nu are 3 elemente, loghează un mesaj de eroare și treci mai departe
                        System.err.println("Format invalid pentru carKey: " + entry.getKey());
                        return null; // Sau o valoare default, dacă e cazul
                    }
                })
                .filter(Objects::nonNull) // Filtrează valorile null (dacă există erori de format)
                .collect(Collectors.toList());
    }


    public Map<Car, Map<String, Integer>> raport_2() {
        Map<Car, Map<String, Integer>> inchirieriPeMasinaSiLuna = new HashMap<>();

        for (Rental rental : rentalRepository.getAll()) {
            Car car = rental.getCar();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(rental.getStart_date());
            int luna = calendar.get(Calendar.MONTH) + 1; // Lunile sunt 0-based, deci adăugăm 1
            int an = calendar.get(Calendar.YEAR);
            String lunaAn = an + "-" + (luna < 10 ? "0" + luna : luna); // Format "YYYY-MM"

            inchirieriPeMasinaSiLuna.putIfAbsent(car, new HashMap<>());
            Map<String, Integer> inchirieriPeLuna = inchirieriPeMasinaSiLuna.get(car);
            inchirieriPeLuna.put(lunaAn, inchirieriPeLuna.getOrDefault(lunaAn, 0) + 1);
        }

        return inchirieriPeMasinaSiLuna;
    }

    public List<Map.Entry<Car, Integer>> raportul_3() {
        Map<Car, Integer> zileDeInchiriere = new HashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy"); // asigură-te că folosești un formatter corespunzător

        for (Rental rental : rentalRepository.getAll()) {
            Car car = rental.getCar();

            // Conversia start_date și end_date din Date în LocalDate
            LocalDate startDate = rental.getStart_date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate endDate = rental.getEnd_date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            // Verifică diferența de zile între date (inclusiv ziua de început și ziua de sfârșit)
            long numarZile = ChronoUnit.DAYS.between(startDate, endDate);

            // Adaugă la totalul de zile pentru fiecare mașină
            zileDeInchiriere.put(car, zileDeInchiriere.getOrDefault(car, 0) + (int) numarZile);
        }

        // Sortează rezultatele după numărul de zile, descrescător
        return zileDeInchiriere.entrySet()
                .stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .collect(Collectors.toList());
    }

}