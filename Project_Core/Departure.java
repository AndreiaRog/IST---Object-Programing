package mmt;
import mmt.Visitable;

import java.time.LocalTime;
import java.util.Comparator;
import java.io.Serializable;

/**
 * This class represents a Departure Station in space and time. It's part of each service, and part of each itenerary,
 * if they use the segment of the service in which this Departure is included.
 * A service has one or many Departures. A Departure has the service it belongs to.
*/
public class Departure implements Serializable, Visitable{

    private static final long serialVersionUID = 201711161743L;

    private String _name;
    private LocalTime _departureTime;
    private Service _service;

    public Departure(String name, LocalTime time, Service service) {
        _name = name;
        _departureTime = time;
        _service = service;
    }

    public final String getName() {
        return _name;
    }

    public final LocalTime getTime() {
        return _departureTime;
    }

    public final Service getService() {
        return _service;
    }

/**
   * Compares the name of two Departures passed
   * as argument. This method may be used when building itineraries.
 */
    public static final Comparator<Departure> NAME_COMPARATOR = new Comparator<Departure>() {

        @Override
        public int compare(Departure d1, Departure d2) {
            String i1 = d1.getName();
            String i2 = d2.getName();
            return (i1 == i2 ? 1 : 0);
        }
    };

/**
  * Visitor allows the string representation of the departure: presents the departure name and its time.
*/
    @Override
    public String accept(DefaultVisitor visitor){
        return visitor.visitD(this);
    }

}
