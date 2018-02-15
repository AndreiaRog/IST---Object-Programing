package mmt;
import mmt.Visitable;

import java.time.LocalTime;

import java.util.Locale;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Collections;

import java.io.Serializable;


/**
 * This class represents the customers of the TrainCompany (Passengers).
 * They can aquire itineraries, built with services present in the TrainCompany.
 * Each passenger belongs to a category, depending on the last 10 journeys.
 * Each passenger has 0 or more itineraries associated.
*/
public class Passenger implements Comparable<Passenger>, Serializable, Visitable{

    private int _id;
    private String _name;
    private Category _category;
    private double _totalPaid;
    private long _acumTime = 0;
    private int _numItin = 0;
    private List<Itinerary> _itineraries;
    private List<Itinerary> _itinerariesTempList =new ArrayList<Itinerary>();

    /* list to save the cost of each itinerary, so as to calculate the cost of the last 10 in Category */
    private List<Double> _itinerariesCost;

    private static final long serialVersionUID = 201711021831L;

    /*When creating a passenger it has NORMAL category by default. The id is provided by a counter of TrainCompany. */

    public Passenger (String name, int id){
        _name = name;
        _id = id;
        _category = new NormalCategory (this, 0.0); // This only happens when creating a Passenger, so the amount is 0.
        _itineraries = new ArrayList<Itinerary>();
        _itinerariesCost = new ArrayList<Double>();
        for(int i=0; i<10; i++){
            _itinerariesCost.add(0.00);
        }
    }

    public List<Double> getItinerariesCost(){
        return _itinerariesCost;
    }

    public List<Itinerary> getItineraries(){
        return _itineraries;
    }

    public void addItinerary(Itinerary itinerary){
        _itineraries.add(itinerary);
        _itinerariesCost.add(itinerary.getCost());
        double discount = getCategory().getDiscount();
        _totalPaid+=itinerary.getCost()*(1-discount);
        long extraTime = itinerary.getDuration();
        _acumTime+=extraTime;
        _category.updateCategory();
        _numItin++;
    }

    public Category getCategory(){
        return _category;
    }

    public void setCategory(Category category){
        _category = category;
    }

    public String getCategoryName(){
        return _category.getName();
    }

    public String getName(){
        return _name;
    }

    public void setName(String name){
        _name = name;
    }

    public final int getId(){
        return _id;
    }

    public int getNumItin(){
        return _numItin;
    }

    public double getTotalPaid(){
        return _totalPaid;
    }

    public String getAcumTime(){
        long minutesNeg = -1*(_acumTime%60);
        long hours = (_acumTime+minutesNeg)/60;
        long minutes =-1*minutesNeg;
        String time = String.format("%02d:%02d", hours, minutes);    
        return time;
    }

/**
   * Compares the number of this passenger with the number of the passenger passed
   * as argument. This method may be used when sorting passenger.
   *
   * @return -1, if the passenger number is lower than the other passenger's; 0, if
   *         the passenger numbers are the same; 1, if the passenger number is
   *         greater than the other passenger's.
*/
    @Override
    public int compareTo(Passenger other) {
        int id = other.getId();
        return (_id < id ? -1 : (_id == id ? 0 : 1));
    }


/**
  * Two passengers are considered equal when they have the same id number.
*/
    @Override
    public boolean equals(Object other){
        if (other instanceof Passenger){
            Passenger p = (Passenger) other;
            return _id == p.getId();
        }
        return false;
    }


/**
 * Visitor allows the string representation of the passenger: presents the passenger id, name, category, number of itineraries made, its total cost and time spent in the format:
*/
    @Override
    public String accept(DefaultVisitor visitor){

        return visitor.visitP(this);
    }

    public String printItineraries (DefaultVisitor visitor){
        return "== Passageiro " + getId() + ": " + getName() + " ==" + getItineraries(visitor);
    }

    public String getItineraries(DefaultVisitor visitor) {
      String text = "";
      Collections.sort(_itineraries, Itinerary.DEPDATE_COMPARATOR);
        for(Itinerary i: _itineraries){
            text += "\n" + i.accept(visitor);
        }
      return text;
    }

    public List<Itinerary> getListItineraries(){
        return _itineraries;
    }

    public void setTempList(List<Itinerary> listToShow){
        _itinerariesTempList = listToShow;
    }


    public boolean existsTempList(){
        if (_itinerariesTempList.isEmpty()){
            return false;
        }
        return true;
    }

    public String getTempList(DefaultVisitor visitor){
      String text = "";
        for(Itinerary i: _itinerariesTempList){
            text += i.accept(visitor) + "\n";
        }
      return text;
    }

    public List<Itinerary> getTempList(){
        return _itinerariesTempList;
    }

    public void deleteTempList(){
        _itinerariesTempList =new ArrayList<Itinerary>();
    }
}
