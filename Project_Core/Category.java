package mmt;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;


/**
 * This is the abstract class from which the three possible categories depend: Normal, Frequent, Special.
 * The category represents the Passengers type, which determines the discounts they have when buying itineraries from the TrainCompany.
 * The category of the passenger depends on the sum of cost of the last 10 journeys.
 * Each passenger has one category associated.
*/

public abstract class Category implements Serializable{


    //Categoria nnem metodo de calcLastTenPaid nem outros,  so as especificas


    // All protected attributes are needed in subclasses.
    protected Passenger _passenger;
    protected String _name;
    protected double _discount;

    private static final long serialVersionUID=201711161816L;


    public Category (Passenger passenger){
        _passenger = passenger;
    }

    public String getName(){
        return _name;
    }

    public abstract void updateCategory();

    public double getDiscount(){
        return _discount;
    }

    public Passenger getPassenger(){
        return _passenger;
    }

}
