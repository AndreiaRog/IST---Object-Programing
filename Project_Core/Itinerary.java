package mmt;
import mmt.Visitable;

import java.io.Serializable;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;


/**
 * This class represents a itinerary belonging to a passenger. Each itinerary
 * has one or more departures, which belong to services. Each passenger has 0 or more itineraries associated.
 */

public class Itinerary implements Serializable, Visitable, Comparable<Itinerary>{

    private static final long serialVersionUID = 201712041622L;

    private Passenger _passenger;

    private LocalDate _date;
    private double _totalCost=0;
    private List<Segment> _segments;

    public Itinerary(Passenger passenger, LocalDate date) {
        _passenger = passenger;
        _date = date;
        _segments = new ArrayList<Segment>();
    }

    public double getCost(){
    	if(_totalCost==0){
    		calcTotalCost();
    	}
        return _totalCost;
    }

    public void calcTotalCost(){
 		double cost=0;
 		for(Segment s: _segments){
 			cost+=s.getCost();
 		}
 		_totalCost=cost;
    }


    public LocalDate getDate(){
    	return _date;
    }

    public int getIndex(){
        int index=0;
        for(int i=0; i<_passenger.getItineraries().size(); i++){
            if (_passenger.getItineraries().get(i)==this){
                index= i+1;
            }
        }
        for(int i=0; i<_passenger.getTempList().size(); i++){
            if (_passenger.getTempList().get(i)==this){
                index=i+1;
            }
        }
        return index;
    }

    public List<Segment> getListSegments() {
      return _segments;
    }

    public final void addSegment(Segment segment){
        _segments.add(segment);
    }

    public String getSegments(DefaultVisitor visitor) {
      String text = "";
        for(Segment seg: _segments){
          text += "\n" + seg.accept(visitor);
        }
      return text;
    }

    public Segment getFirstSegment (){
        return _segments.get(0);
    }

    public Segment getLastSegment (){
        int lastSeg = _segments.size()-1;
        return _segments.get(lastSeg);
    }

    // é desta forma que se calcula? Ou é apenas tempo em viagem? Soma de duração de seg
    public long getDuration(){
        LocalTime first = getFirstSegment().getFirstDeparture().getTime();
        LocalTime last = getLastSegment().getLastDeparture().getTime();
        long duration = Duration.between(first,last).toMinutes();
        return duration;
    }


    public static final Comparator<Itinerary> DEPDATE_COMPARATOR = new Comparator<Itinerary>() {

        @Override
        public int compare(Itinerary itin1, Itinerary itin2) {
            LocalDate date1 = itin1.getDate();
            LocalDate date2 = itin2.getDate();
            return (date1.compareTo(date2));
        }
    };

    /*public static final Comparator<Itinerary> DEPTIME_ITIN_COMPARATOR = new Comparator<Itinerary>() {

        @Override
        public int compare(Itinerary itin1, Itinerary itin2) {
            Segment seg1 = itin1.getFirstSegment();
            Segment seg2 = itin2.getFirstSegment();
            Departure d1 = seg1.getFirstDeparture();
            Departure d2 = seg2.getFirstDeparture();
            LocalTime i1 = d1.getTime();
            LocalTime i2 = d2.getTime();
            return (i1.compareTo(i2));
        }
    }; */

    public static final Comparator<Itinerary> ARRTIME_ITIN_COMPARATOR = new Comparator<Itinerary>() {

        @Override
        public int compare(Itinerary itin1, Itinerary itin2) {
            Segment seg1 = itin1.getLastSegment();
            Segment seg2 = itin2.getLastSegment();
            Departure d1 = seg1.getLastDeparture();
            Departure d2 = seg2.getLastDeparture();
            LocalTime t1 = d1.getTime();
            LocalTime t2 = d2.getTime();
            return (t1.compareTo(t2));
        }
    };

    @Override
    public int compareTo(Itinerary itinerary) {

        return Comparator.comparing(Itinerary::getFirstSegment, Segment.DEPTIME_COMPARATOR).thenComparing(Itinerary::getLastSegment,Segment.ARRTIME_COMPARATOR).thenComparingDouble(Itinerary::getCost).compare(this, itinerary);

    }

    @Override
    public String accept(DefaultVisitor visitor){
        // quando faço este print tenho de fazer passagem de string para LocalDate para apresentar dessa forma
        //-- podemos fazer isto porque so vamos representar a data e nao vamos manipular e facilita o import!!!!
        return visitor.visitI(this);
    }
}
