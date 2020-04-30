package Lab6Client;

import java.io.Serializable;
/**
 * Служебный класс класса Flat.
 */
public class House implements Serializable {
    private static final long serialVersionUID = 500L;
    private String name; //Поле не может быть null
    private Long year; //Значение поля должно быть больше 0
    private Integer numberOfLifts; //Значение поля должно быть больше 0

    public House(String name, Long year, Integer numberOfLifts) {
        this.name = name;
        this.year = year;
        this.numberOfLifts = numberOfLifts;
    }
}

