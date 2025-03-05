package domain;

public class CarConverter implements EntityConverter<Car> {

    @Override
    public String toString(Car object) {
        return object.getId()+" , "+object.getBrand()+" , "+object.getModel();
    }

    @Override
    public Car fromString(String line) {
        try {
            String[] tokens = line.split(",");
            if (tokens.length < 3) {
                throw new IllegalArgumentException("Linia nu are suficiente elemente: " + line);
            }
            int id = Integer.parseInt(tokens[0].trim());
            String brand = tokens[1].trim();
            String model = tokens[2].trim();
            return new Car(id, brand, model);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID-ul nu este un număr valid în linia: " + line, e);
        } catch (Exception e) {
            throw new IllegalArgumentException("Eroare la parsarea liniei: " + line, e);
        }
    }

}
