package mmt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Comparator;
import java.io.Serializable;

import java.time.LocalTime;

/**
 * This class represents the Station in space only. It's part of each service, and part of each itenerary,
 * if they use the segment of the service in which this Station is included.
 * Each station has one or many Departures. Useful to find departures from different services with the same station, to buil itineraries
*/
public class Station implements Comparable<Station>, Serializable{

    private static final long serialVersionUID = 201711161744L;

    private String _name;
    private List<Departure> _departures = new ArrayList<Departure>();

    public Station(String name) {
        _name = name;
    }

    public final String getName() {
        return _name;
    }

    public final void addDeparture(Departure departure){
        _departures.add(departure);
    }

    public List<Departure> getDepartures(){
        return _departures;
    }

/**
   * Compares the name of this Station with the name of the Station passed
   * as argument. This method may be used when building iteneraries.
 */
    @Override
    public int compareTo(Station station) {
        String name = station.getName();
        return (_name==name? 1 : 0);
    }

}
