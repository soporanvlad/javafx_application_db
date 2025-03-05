package domain;

import java.io.Serial;
import java.io.Serializable;

public class Car extends Entity implements Serializable {
    private String brand;
    private String model;

    @Serial
    private static final long serialVersionUID = 1L;

    public Car(int id, String brand, String model)
    {
        super(id);
        this.brand = brand;
        this.model = model;
    }

    public String getBrand()
    {
        return brand;
    }

    public String getModel()
    {
        return model;
    }

    public void setBrand(String brand)
    {
        this.brand = brand;
    }

    public void setModel(String model)
    {
        this.model = model;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id='" + getId() + '\'' +
                "brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                '}';
    }

}
