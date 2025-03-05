package domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Rental extends Entity implements Serializable{

    private Car car;
    private Date start_date;
    private Date end_date;

    @Serial
    private static final long serialVersionUID = 1L;

    public Rental(int id, Car car, Date start_date, Date end_date)
    {
        super(id);
        this.car = car;
        this.start_date = start_date;
        this.end_date = end_date;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    @Override
    public String toString() {
        return "Rental{" +
                "id=" + getId() +
                " car=" + car +
                ", start_date=" + start_date +
                ", end_date=" + end_date +
                '}';
    }
}
