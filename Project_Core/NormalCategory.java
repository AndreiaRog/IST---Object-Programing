package mmt;

import java.io.Serializable;
import java.util.List;
import java.util.Collections;




/**
 * The category represents the Passengers type that paid less than or equal to 250 in the last 10 iteneraries.
*/
public class NormalCategory  extends Category implements Serializable{

    private double _lastTenPaid;

    public NormalCategory (Passenger passenger, Double paid) {
        super(passenger);
        _lastTenPaid = paid;
        _name = "NORMAL";
        _discount =0.00;
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

}
