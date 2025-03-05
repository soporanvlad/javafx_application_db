package service;

import domain.Car;
import repository.AbstractRepository;
import repository.RepositoryException;

import java.util.Collection;

public class CarService {

    private final AbstractRepository<Car> carRepository;

    public CarService(AbstractRepository<Car> carRepository) {
        this.carRepository = carRepository;
    }

    public void addCar(int id, String brand, String model) throws RepositoryException {
        Car car = new Car(id, brand, model);
        carRepository.add(car);
    }

    public void deleteCar(int id, String brand, String model) throws RepositoryException{
        Car car = new Car(id, brand, model);
        carRepository.delete(car);
    }

    public void updateCar(int id1, String brand1, String model1, int id2, String brand2, String model2) throws RepositoryException {
        Car car1 = new Car(id1, brand1, model1);
        Car car2 = new Car(id2, brand2, model2);
        carRepository.update(car1, car2);
    }

    public Car getCarById(int id) {
        return carRepository.find(id);
    }

    public Collection<Car> getAllCars() {
        return carRepository.getAll();
    }
}
