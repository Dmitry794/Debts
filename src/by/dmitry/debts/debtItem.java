package by.dmitry.debts;

import java.util.Date;
import java.util.jar.Attributes;

/**
 * Created by Dmitry on 03.08.16.
 */
public class debtItem {
    private int date;
    private String name;
    private double count;
    private double coast;
    private double cash;
    private static String[] person = {"Викуля", "Ксюха"};
    private int id_person;

    public debtItem(String name, double count, double coast, int id_person) {
        this.date = new Date().getDate();
        this.name = name;
        this.count = count;
        this.coast = coast;
        cash = count * coast;
        this.id_person = id_person;
    }

    public String getName() {
        return name;
    }

    public double getCount() {
        return count;
    }

    public double getCoast() {
        return coast;
    }

    public double getCash() {
        return cash;
    }
}
