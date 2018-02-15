	package mmt;

	import java.io.Serializable;
	import java.io.BufferedReader;
	import java.io.FileReader;
	import java.io.IOException;
	import java.io.FileNotFoundException;

	import java.util.Map;
	import java.util.TreeMap;
	import java.util.regex.Pattern;
	import java.util.ArrayList;
	import java.util.Collections;
	import java.util.List;
	import java.util.HashMap;

	import java.time.LocalTime;
    import java.time.LocalDate;
    import java.time.format.DateTimeParseException;

	import mmt.exceptions.BadDateSpecificationException;
	import mmt.exceptions.BadEntryException;
	import mmt.exceptions.BadTimeSpecificationException;
	//import mmt.exceptions.InvalidPassengerNameException;
	import mmt.exceptions.NoSuchDepartureException;
	import mmt.exceptions.NoSuchPassengerIdException;
	import mmt.exceptions.NoSuchServiceIdException;
	import mmt.exceptions.NoSuchStationNameException;
	import mmt.exceptions.NoSuchItineraryChoiceException;
	import mmt.exceptions.NonUniquePassengerNameException;

	/**
	* A train company has schedules (services) for its trains and passengers that
	* acquire itineraries based on those schedules.
	*/
	public class TrainCompany implements Serializable {

	private static final long serialVersionUID = 201708301010L;

	/* counter for numbering passengers when registered */
	private int _passengerCounter = 0;
	/* map that contains all passengers registered in this TrainCompany*/
	private Map<Integer, Passenger> _passengers;
	/* map that contains all services that the TrainCompany offers to the clients (Passengers)*/
	private Map<Integer, Service> _services;
	/* map that contains all stations that belong to the TrainCompany's services*/
	private Map<String, Station> _stations; //isto se calhar Lista nao?

	/* The map of services that cross each other. */
	private Map<Station, List<Integer>> _crossRoads= new HashMap<Station, List<Integer>>();


	public TrainCompany() {
	  _passengers = new TreeMap<Integer, Passenger>();
	  _services = new TreeMap<Integer, Service>();
	  _stations = new TreeMap<String, Station>();
	}

    public TrainCompany(Map<Integer, Service> services, Map<String, Station> stations){
        _services = services;
        _stations = stations;
        _passengers = new TreeMap<Integer, Passenger>();
    }


	/* Reading files given to fill the TrainCompany with passengers and/or services, and itineraries in the future */
	public void importFile(String filename) throws NonUniquePassengerNameException, NoSuchStationNameException, NoSuchPassengerIdException, NoSuchServiceIdException {
	try{
		  BufferedReader br = new BufferedReader(new FileReader(filename));
		  String s = new String();
		  while ((s = br.readLine()) != null) {
		    String[] fields = s.split("\\|");
		    registerFromFields(fields);
		  }
		  br.close();
		}catch (BadEntryException e){
		  e.printStackTrace();
		}catch (IOException e){
		e.printStackTrace();
		}
	}

	/* to make the registration of each service, passenger or itinerary in the TrainCompany based on the pattern in the imported file */
	public void registerFromFields(String[] fields) throws BadEntryException, NoSuchStationNameException, NonUniquePassengerNameException, NoSuchPassengerIdException, NoSuchServiceIdException {

		Pattern patPassenger = Pattern.compile("^(PASSENGER)");
		Pattern patService = Pattern.compile("^(SERVICE)");
		Pattern patItinerary = Pattern.compile("^(ITINERARY)");

		if (patPassenger.matcher(fields[0]).matches()){
		    registerPassenger(fields);
		}else if (patService.matcher(fields[0]).matches()){
		    registerService(fields);
		}else if (patItinerary.matcher(fields[0]).matches()){
		    registerItinerary(fields);
		}
		else{
		    throw new BadEntryException(fields[0]);
		}
	}

	public void registerPassenger(String[] fields) throws NonUniquePassengerNameException, BadEntryException{
	    if (fields[0].equals("PASSENGER")){
	        String name = fields[1];
	        registPassenger(name);
	    }else{
            throw new BadEntryException(fields[0]);
        }

	}

	public void registerService(String[] fields) throws BadEntryException{

		if (fields[0].equals("SERVICE")){
		    int id = Integer.parseInt(fields[1]);
		    double cost = Double.parseDouble(fields[2]);
		    Service service = new Service(id, cost);
		    addService(service, fields);
		}else{
            throw new BadEntryException(fields[0]);
        }
	}

	public void registerItinerary(String[] fields) throws BadEntryException, NoSuchPassengerIdException, NoSuchServiceIdException, NoSuchStationNameException {

		if (fields[0].equals("ITINERARY")){
		    int id = Integer.parseInt(fields[1]);
		    Passenger passenger = getPassengerById(id);
            LocalDate date = LocalDate.parse(fields[2]);
		    Itinerary itinerary = new Itinerary(passenger, date);
		    fillItinerary(itinerary, fields);
		    itinerary.calcTotalCost();
		    passenger.addItinerary(itinerary);
		}else{
            throw new BadEntryException(fields[0]);
        }
	}


	// Add Passengers to the TrainCompany
	public void addPassenger(Passenger passenger) throws NonUniquePassengerNameException {
		if (uniquePassengerName(passenger.getName())){
		    _passengers.put(passenger.getId(), passenger);
		    _passengerCounter++;
		}
	}

	public boolean uniquePassengerName (String name) throws NonUniquePassengerNameException{
	  for (Map.Entry<Integer, Passenger> entry : _passengers.entrySet()){
	      Passenger p = entry.getValue();
	      if (p.getName().equals(name)){
	          throw new NonUniquePassengerNameException(name);
	      }
	  }return true;
	}

	// Add Services to the TrainCompany, while adding each departure to the service and to the station(place) it belongs and also each station to the TrainCompany. At the same time, it will add to an hashMap the services that cross each other
	public void addService(Service service, String[] fields) {
		for(int i=3; i<fields.length; i+=1){
		    LocalTime departureTime = LocalTime.parse(fields[i++]);
		    String name = fields[i];
		    Departure departure= new Departure(name, departureTime, service);
		    service.addDeparture(departure);
		        if (_stations.get(name)!=null){
		            Station station = _stations.get(name);
					_crossRoads.get(station).add(service.getId());
		            station.addDeparture(departure);
		        }else{
		            Station station= new Station(name);
		            station.addDeparture(departure);
		            _stations.put(name, station);
		           	_crossRoads.put(station, new ArrayList<Integer>());
		           	_crossRoads.get(station).add(service.getId());
		        }
		}
		_services.put(service.getId(), service);
	}

	public void fillItinerary(Itinerary itinerary, String[] fields) throws NoSuchServiceIdException, NoSuchStationNameException{
		for(int i=3; i<fields.length; i++){
			String[] pieces = fields[i].split("\\/");
            addSegment(itinerary, pieces);
		}
    }

	public void addSegment(Itinerary itinerary, String[] pieces) throws NoSuchServiceIdException, NoSuchStationNameException{
        int num = Integer.parseInt(pieces[0]);
        Service s = getServiceById(num);
        String first = pieces[1];
  		String last = pieces[2];
  		if (!stationExistsInService(s, first)){
			throw new NoSuchStationNameException(first);
		}
		if (!stationExistsInService(s, last)){
			throw new NoSuchStationNameException(last);
		}
        Segment seg = fillSegment(s, first, last);
	   	itinerary.addSegment(seg);
	}

	public boolean stationExistsInService (Service service, String stationName){
		List<Departure> depList = new ArrayList<Departure>();
  		depList = service.getListDepartures();
        for(int i=0; i<depList.size(); i++){
  			Departure d = depList.get(i);
  			if(d.getName().equals(stationName)){
  				return true;
  			}
  		}
  		return false;
	}

    public Segment fillSegment(Service service, String first, String last){
        double sCost = service.getCost();
        long sTimeMin = service.getDuration();
        int sId = service.getId();
        Segment seg = new Segment(sId, sCost, sTimeMin);
        List<Departure> depList = new ArrayList<Departure>();
  		depList = service.getListDepartures();
        for(int i=0; i<depList.size(); i++){
  			Departure d1 = depList.get(i);
			if (d1.getName().equals(first)){
				seg.addDeparture(d1);
				for(int j=i; j<depList.size()-1; j++){
					Departure d2 = depList.get(j);
					int z= j+1;
					Departure d3 = depList.get(z);
                    if(d2.getName().equals(last)){
                        seg.calcSegmentCost();
                        return seg;
                    }
					else{
						seg.addDeparture(d3);
					}
	      		}
	      	}
      	}
        if(!seg.getListDepartures().isEmpty()){
            seg.calcSegmentCost();
        }
        return seg;
    }

	public void registPassenger(String name) throws NonUniquePassengerNameException{
		Passenger passenger = new Passenger(name, _passengerCounter);
		addPassenger(passenger);
	}
	/* method to obtain a list of all passengers registered in the TrainCompany */
	public List<Passenger> getAllPassengers() {
		List<Passenger> lst = new ArrayList<Passenger>();
		for(Map.Entry<Integer, Passenger> p: _passengers.entrySet()){
		  Passenger value=p.getValue();
		  lst.add(value);
		}
		Collections.sort(lst);
		return lst;
	}

	/* method to obtain a passenger based on the id requested */
	public Passenger getPassengerById(int id) throws NoSuchPassengerIdException {
		Passenger passenger = _passengers.get(id);
		if (passenger == null){
		    throw new NoSuchPassengerIdException(id);
		}
		return passenger;
	}


	/* method to change the name of a certain passenger */
	public void changePassengerName(int id, String name) throws NoSuchPassengerIdException, NonUniquePassengerNameException {
		Passenger p = getPassengerById(id);
		if (uniquePassengerName(name)){
		  p.setName(name);
		}
	}

	/* method to obtain a list of all services offered by the TrainCompany */
	public List<Service> getAllServices() {
		List<Service> lst = new ArrayList<Service>();
		for(Map.Entry<Integer, Service> s: _services.entrySet()){
		  Service value=s.getValue();
		  lst.add(value);
		}
		Collections.sort(lst);
		return lst;
	}

	/* method to obtain all services that start in a certain station, ordered by time of departure */
	public List<Service> getServicesByDepartureStation(String name) throws NoSuchStationNameException {
		List<Service> lst = new ArrayList<Service>();
		for(Map.Entry<Integer, Service> s : _services.entrySet()) {
		    Service value = s.getValue();
		    String first = value.getFirstDeparture().getName();
		    if (first.equals(name)) {
		        lst.add(value);
		    }
		}
		if (lst.isEmpty()){
		    if(_stations.get(name)==null)
		    throw new NoSuchStationNameException(name);
		}
		Collections.sort(lst, Service.DEPTIME_COMPARATOR);
		return Collections.unmodifiableList(lst);
	}

	/* method to obtain all services that end in a certain station, ordered by time of arrival (departure of last Station) */
	public List<Service> getServicesByArrivingStation(String name) throws NoSuchStationNameException {
		List<Service> lst = new ArrayList<Service>();
		for(Map.Entry<Integer, Service> s : _services.entrySet()) {
		    Service value = s.getValue();
		    String last = value.getLastDeparture().getName();
		    if (last.equals(name)) {
		        lst.add(value);
		    }
		}
		if (lst.isEmpty()){
		    if(_stations.get(name)==null)
		    throw new NoSuchStationNameException(name);
		}
		Collections.sort(lst, Service.ARRTIME_COMPARATOR);
		return Collections.unmodifiableList(lst);
	}

	/* method to obtain a service based on the number requested */
	public Service getServiceById(int id) throws NoSuchServiceIdException {
		Service service = _services.get(id);
		if (service == null) {
		   throw new NoSuchServiceIdException(id);
		}
		return service;
    }

    /*chica 23:20 7Dez */
    public Map<Integer, Service> getServicesMap(){
        return _services;
    }

    /*chica 23:22 7Dez */
    public Map<String, Station> getStationsMap(){
        return _stations;
    }

    /*chica*/
    public boolean existItinerariesPassenger(int id) throws NoSuchPassengerIdException{
        Passenger passenger = getPassengerById(id);
        List<Itinerary> list = passenger.getListItineraries();
        if (list.size() != 0){
            return true;
        }
        return false;
    }


	public String printItineraries(int id, DefaultVisitor visitor) throws NoSuchPassengerIdException{
        Passenger p = getPassengerById(id);
    	return p.printItineraries(visitor);
  	}


	public void searchItineraries(int passengerId, String first, String last, String departureDate, String departureTime) throws NoSuchPassengerIdException, NoSuchStationNameException, BadDateSpecificationException, BadTimeSpecificationException{
     	LocalDate depDate;
     	LocalTime depTime;
     	try{
     		depDate = LocalDate.parse(departureDate);
    	}catch (DateTimeParseException e){
     		throw new BadDateSpecificationException(departureDate);
     	}
     	try{
     		depTime = LocalTime.parse(departureTime);
     	}catch (DateTimeParseException e){
     		throw new BadTimeSpecificationException(departureTime);
     	}
     	if (!stationExistsInMap(first)){
     		throw new NoSuchStationNameException(first);
     	}
     	if (!stationExistsInMap(last)){
     		throw new NoSuchStationNameException(last);
     	}
     	Passenger p = getPassengerById(passengerId);
	    List<Itinerary> prelistToShow = new ArrayList<Itinerary>();
        for(Map.Entry<Integer, Service> service : _services.entrySet()) {
            Service s = service.getValue();
			List<Itinerary> listFromService = new ArrayList<Itinerary>();

			List<Integer> listUsedServices = new ArrayList<Integer>();
			listUsedServices.add(s.getId());
            buildListItin(listFromService, listUsedServices, p, s, first, last, depDate, 0);

			if (listFromService.size() > 0){
				Itinerary chosen=selectItinerary(listFromService, depTime, last, first, p, depDate);
				if (chosen.getListSegments().size() > 0){
					prelistToShow.add(chosen);
				}
			}
	   	}
		List<Itinerary> listToShow = orderItineraries(prelistToShow);
		p.setTempList(listToShow);
	}

	public boolean serviceWasUsed (int id, List<Integer> listUsedServices){
		for(int serviceId : listUsedServices) {
            if (serviceId==id){
  				return true;
  			}
  		}
  		return false;
	}


	public boolean stationExistsInMap (String stationName){
		for(Map.Entry<String, Station> entry : _stations.entrySet()) {
            Station station = entry.getValue();
            if (station.getName().equals(stationName)){
  				return true;
  			}
  		}
  		return false;
	}



	public void buildListItin(List<Itinerary> itineraries, List<Integer> listUsedServices, Passenger p, Service service, String first, String last, LocalDate  departureDate, int index) {

        Itinerary i1 = new Itinerary(p, departureDate);
        if(index != 0){
            i1 = itineraries.get(index);
        }
        Segment seg = fillSegment(service, first, last);
        if(!seg.getListDepartures().isEmpty()){
            String name = seg.getLastDeparture().getName();
	        if(name.equals(last)){
                Itinerary i2 = new Itinerary(p, departureDate);
                if(i1.getListSegments().size()>0){
                    copyItin(i1,i2);
                }
	            i2.addSegment(seg);
	            itineraries.add(i2);
	        }
            if(!name.equals(last)){
	            List<Departure> listDep = seg.getListDepartures();
	            for(int j =1; j<listDep.size(); j++){
                    if(index > 0){
                        String lastStation = itineraries.get(itineraries.size()-1).getLastSegment().getLastDeparture().getName();
                        if(itineraries.get(itineraries.size()-1).getLastSegment().getLastDeparture().getName().equals(last)){
                        break;
                        }
                    }
                    Departure dep =listDep.get(j);
	                Station station = _stations.get(dep.getName());
	                if(_crossRoads.get(station).size() > 1){
	                    seg = fillSegment(service, first, dep.getName());
	                    i1.addSegment(seg);
	                    itineraries.add(i1);
	                    index++;
	                    for(int i=0; i<_crossRoads.get(station).size(); i++){
	                        try{
		                        Service s = getServiceById(_crossRoads.get(station).get(i));
		                        if(!serviceWasUsed(s.getId(), listUsedServices)){

			                        Itinerary i3 = new Itinerary(p, departureDate);
                                    copyItin(i1,i3);
			                        itineraries.add(i3);

			                        listUsedServices.add(s.getId());

			                        buildListItin(itineraries, listUsedServices, p, s, station.getName(), last, departureDate, index);
			                    }
	                    	}catch (NoSuchServiceIdException e){
	                    		//not expected to happen
	                    	}
	                    }
                        index = itineraries.size();
	                }

	            }

	        }
	    }
	}

    public void copyItin(Itinerary i1, Itinerary i2){
        List<Segment> listI1 = i1.getListSegments();
        for(Segment s: listI1){
            i2.addSegment(s);
        }
    }


	public Itinerary selectItinerary(List<Itinerary> itineraries, LocalTime departureTime, String last, String first, Passenger p, LocalDate date){

		List<Itinerary> preList = new ArrayList<Itinerary>();
		for (Itinerary i: itineraries){
			if(i.getListSegments().size()>0){
				LocalTime depTime = i.getFirstSegment().getFirstDeparture().getTime();
				String firstDepOfItin = i.getFirstSegment().getFirstDeparture().getName();
				String lastDepOfItin = i.getLastSegment().getLastDeparture().getName();
                int num = i.getLastSegment().getId();
				if  ((depTime.isAfter(departureTime) || depTime.equals(departureTime)) &&  lastDepOfItin.equals(last)){
                    if (i.getListSegments().size() == 1){
						return i;
					}else{
                        if(chronologicOrderSegments(i) && nonRepeatedStations(i)){

                            preList.add(i);
                        }
                    }
				}
			}
		}

		if (preList.size() == 0){
			Itinerary i = new Itinerary(p, date);
			return i;
		}

		Collections.sort(preList, Itinerary.ARRTIME_ITIN_COMPARATOR); //menor hora de chegada primeiro

		return preList.get(0);

	}

	public boolean nonRepeatedStations(Itinerary i){
		List<String> stations = new ArrayList<String>();
		boolean value =true;

		for(Segment seg: i.getListSegments()){
			for(int z=0; z<seg.getListDepartures().size()-1; z++){
				stations.add(seg.getListDepartures().get(z).getName());
			}
		}

		for(int x=0; x<stations.size()-1; x++){
			for (int y=x+1; y<stations.size(); y++){
				if(stations.get(x).equals(stations.get(y))){
					value=false;
				}
			}
		}
		return value;
	}




    public boolean chronologicOrderSegments(Itinerary i){
        boolean value = true;
        for(int indice=1; indice < i.getListSegments().size(); indice++){
            LocalTime timeAfter = i.getListSegments().get(indice).getFirstDeparture().getTime();
            LocalTime timeBefore = i.getListSegments().get(indice-1).getLastDeparture().getTime();
            if(!timeAfter.isAfter(timeBefore)){
                value = false;
            }
        }
        return value;
    }



	public List<Itinerary> orderItineraries(List<Itinerary> itineraries){

		Collections.sort(itineraries);
		return itineraries;

		//apply criterios para ordenar itinerarios da lista final, crescente:
		// 1) hora partida
		// 2) hora chegada
		// 3) custo
	}


	public void commitItinerary(int passengerId, int index) throws NoSuchPassengerIdException, NoSuchItineraryChoiceException{
        Passenger p = getPassengerById(passengerId);
        if(index > p.getTempList().size() || index < 0){
            p.deleteTempList();
            throw new NoSuchItineraryChoiceException(passengerId, index);
        }
        if(index == 0){
            p.deleteTempList();
        }else{
            for(int i=0; i < p.getTempList().size(); i++){
                int j= i+1;
                if (index == j){
                    Itinerary itin = p.getTempList().get(i);
                    p.addItinerary(itin);
                }
            }
        }
	}

	public boolean existsListToShow(int passengerId) throws NoSuchPassengerIdException{
		Passenger p = getPassengerById(passengerId);
		return p.existsTempList();
	}

	public String printListToShow(int id, DefaultVisitor visitor) throws NoSuchPassengerIdException{
		Passenger p = getPassengerById(id);
		return p.getTempList(visitor);

	}

}
