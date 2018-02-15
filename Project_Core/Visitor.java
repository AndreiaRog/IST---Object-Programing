package mmt;

public interface Visitor{

    public String visitP(Passenger passenger);

    public String visitS(Service service);

    public String visitD(Departure departure);

    public String visitI(Itinerary itinerary);

}
