package mmt;
import mmt.Visitable;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Comparator;
import java.util.Locale;

import java.time.LocalTime;
import java.time.Duration;

/**
 * This class represents the product that the TrainCompany offers to its customers
 * (Passengers). The  services are used to build the itineraries. Each itinerary
 * has one or more services. Each passenger has 0 or more itineraries associated.
 */
public class Service implements Comparable<Service>, Serializable, Visitable {

    private static final long serialVersionUID = 201711021622L;

    private int _id;
    private double _cost;


    /** The list of departure stations (space and time) associated with this service. */
    private List<Departure> _departures= new ArrayList<Departure>();

    public Service(int id, double cost) {
        _id = id;
        _cost = cost;

    }

    public int getId() {
        return _id;
    }

    public Departure getFirstDeparture(){
        return _departures.get(0);
    }

    public Departure getLastDeparture(){
        int lastDep = _departures.size()-1;
        return _departures.get(lastDep);
    }

    public String getDepartures(DefaultVisitor visitor) {
      String text = "";
        for(Departure dep: _departures){
          text += "\n" + dep.accept(visitor);
        }
      return text;
    }

    public List<Departure> getListDepartures(){
      return _departures;
    }


    public double getCost(){
        return _cost;
    }

    public void addDeparture(Departure departure){
        _departures.add(departure);
    }

    public long getDuration(){
      LocalTime begin = getFirstDeparture().getTime();
      LocalTime end = getLastDeparture().getTime();
      Duration serviceTime = Duration.between(begin, end);
      long serviceTimeMin = serviceTime.toMinutes();

      return serviceTimeMin;
    }

/**
   *Comparators: These methods are used when sorting list of Services obtained by request in the TicketOffice.
   *
   * DEPTIME: Compares the departure times of the departure stations of two Services passed as arguments.
   * ARRTIME: Compares the departure times of the arrival stations of two Services passed as arguments.
   *
   * @return 1, if the service departure time of the station that we are considering is lower than the other service's;
   *         0, otherwise
   *
*/
    public static final Comparator<Service> DEPTIME_COMPARATOR = new Comparator<Service>() {

        @Override
        public int compare(Service s1, Service s2) {
            Departure d1 = s1.getFirstDeparture();
            Departure d2 = s2.getFirstDeparture();
            LocalTime i1 = d1.getTime();
            LocalTime i2 = d2.getTime();
            return (i1.compareTo(i2));
        }
    };

    public static final Comparator<Service> ARRTIME_COMPARATOR = new Comparator<Service>() {

        @Override
        public int compare(Service s1, Service s2) {
            Departure d1 = s1.getLastDeparture();
            Departure d2 = s2.getLastDeparture();
            LocalTime i1 = d1.getTime();
            LocalTime i2 = d2.getTime();
            return (i1.compareTo(i2));
        }
    };


/**
   *Natural order:
   * Compares the number of this service with the number of the service passed
   * as argument. This method is used when sorting all the services of a TrainCompany.
   *
   *@return -1, if the service number is lower than the other service's; 0, if
   *         the service numbers are the same; 1, if the service number is
   *         greater than the other service's.
*/
    @Override
    public int compareTo(Service service) {
        int number = service.getId();
        return (_id < number ? -1 : (_id == number ? 0 : 1));
    }


/*Two services are considered equal when they have the same id*/
    @Override
        public boolean equals(Object obj) {
        return (obj instanceof Service) && ((Service) obj).getId() == _id;
    }


/**
 * Visitor allows the string representation of the service: presents the service number, its cost and its stations in the format:
 */
    @Override
    public String accept(DefaultVisitor visitor){
        return visitor.visitS(this);
    }



}
