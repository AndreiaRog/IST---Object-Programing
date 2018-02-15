package mmt;

import java.io.Serializable;
import java.util.List;
import java.util.Collections;


/**
 * The category represents the Passengers type that paid more than 250 but less than or equal to 2500 in the last 10 iteneraries.
*/
public class FrequentCategory extends Category implements Serializable{

    private double _lastTenPaid;

    public FrequentCategory (Passenger passenger, Double paid) {
        super(passenger);
        _lastTenPaid = paid;
        _name = "FREQUENTE";
        _discount=0.15;
    }

/* Calculates and updates the actual cost of the 10 last iteneraries bought by the passenger */
    public void calcLastTenPaid(){
        List<Double> lst = _passenger.getItinerariesCost();
        //Collections.reverse(lst);
        Double cost = 0.00;
        for(int i=lst.size()-1; i >lst.size()-11; i--){
            cost += lst.get(i);
        }
        _lastTenPaid = cost;
    }

    /*Verifies the cost of the 10 last itineraries, and updates the state accordingly*/
    public void updateCategory(){
        calcLastTenPaid();
        if (_lastTenPaid > 250.00){
            if(_lastTenPaid > 2500.00){
                _passenger.setCategory(new SpecialCategory(_passenger, _lastTenPaid));
            }else{
                _passenger.setCategory(new FrequentCategory(_passenger, _lastTenPaid));
            }
        }else{
            _passenger.setCategory(new NormalCategory(_passenger, _lastTenPaid));
        }
    }


    /* This method is called whenever the passenger adds a new itinerary. */
    public void update(){
        this.calcLastTenPaid();
        this.updateCategory();
    }
}
