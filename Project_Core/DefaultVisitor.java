package mmt;

import java.util.Locale;

public class DefaultVisitor implements Visitor{

    private int i=0;
    private int j=0;

    public DefaultVisitor() {
    }

    //Passenger visit
    @Override
    public String visitP(Passenger passenger){

        String totalPaid = String.format(Locale.US, "%.2f", passenger.getTotalPaid());

        return passenger.getId() + "|" + passenger.getName() + "|" + passenger.getCategoryName() + "|" + passenger.getNumItin() + "|" + totalPaid + "|" + passenger.getAcumTime();
    

    }

    //Service visit
    @Override
    public String visitS(Service service){
        String dep = service.getDepartures(this);
        String cost = String.format(Locale.US, "%.2f", service.getCost());

        return "Serviço #" + service.getId() + " @ " + cost + dep;
    }

    //Departure visit
    @Override
    public String visitD(Departure departure){
        return departure.getTime() + " " + departure.getName();
    }

    // Itinerary visit
    @Override
    public String visitI(Itinerary itinerary){
        String seg = itinerary.getSegments(this);
        String cost = String.format(Locale.US, "%.2f", itinerary.getCost());

        return "\nItinerário " + itinerary.getIndex() + " para " + itinerary.getDate() + " @ " + cost + seg;
    }

}
