package domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RentalConverter implements EntityConverter<Rental> {

    private final CarConverter carConverter = new CarConverter();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    public String toString(Rental object) {
        return object.getId() + "," +
                carConverter.toString(object.getCar()) + "," +
                dateFormat.format(object.getStart_date()) + "," +
                dateFormat.format(object.getEnd_date());
    }

    @Override
    public Rental fromString(String line) {
        String[] tokens = line.split(",");
        if (tokens.length < 6) {
            throw new IllegalArgumentException("Linia nu are suficiente elemente: " + line);
        }
        int id = Integer.parseInt(tokens[0]);
        Car car = carConverter.fromString(tokens[1] + "," + tokens[2] + "," + tokens[3]);
        try {
            Date start_date = dateFormat.parse(tokens[4]);
            Date end_date = dateFormat.parse(tokens[5]);
            return new Rental(id, car, start_date, end_date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Eroare la parsarea datelor: " + e.getMessage(), e);
        }
    }
}
