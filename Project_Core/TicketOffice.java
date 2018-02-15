package mmt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.List;
import java.util.ArrayList;

import mmt.exceptions.BadDateSpecificationException; 
import mmt.exceptions.BadTimeSpecificationException; 
import mmt.exceptions.ImportFileException;
//import mmt.exceptions.InvalidPassengerNameException;
import mmt.exceptions.MissingFileAssociationException; 
import mmt.exceptions.NoSuchPassengerIdException;
import mmt.exceptions.NoSuchServiceIdException;
import mmt.exceptions.NoSuchStationNameException;
import mmt.exceptions.NoSuchItineraryChoiceException; 
import mmt.exceptions.NonUniquePassengerNameException;

/**
 * Façade for handling persistence and other functions.
 * Is the element of communication between package App and class TrainCompany.
 */
public class TicketOffice {

  /** The object doing most of the actual work. */
  private TrainCompany _trains;
  private String _filename;

  public TicketOffice(){
    _trains = new TrainCompany();
    _filename = new String();
  }

  public void setTrains(TrainCompany train){
    _trains = train;
  }

  /* method to restart the TrainCompany, keeping the services (and its stations)*/

  public void reset() {
    _trains = new TrainCompany(_trains.getServicesMap(), _trains.getStationsMap());
    _filename = new String();
  }

  /* method to save the state of the application */
  public void save() throws FileNotFoundException, IOException, ClassNotFoundException  {
    ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(_filename)));
    oos.writeObject(_trains);
    oos.close();
  }

  /* method to restore and open the application for use */
  public void load(String filename) throws FileNotFoundException, IOException, ClassNotFoundException{
    ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)));
    TrainCompany _trains = (TrainCompany) ois.readObject();
    ois.close();
    this.setTrains(_trains);
    this.setFileName(filename);
  }


  public void importFile(String datafile)  throws ImportFileException {
    try{
        _trains.importFile(datafile);
    }catch (NonUniquePassengerNameException | NoSuchPassengerIdException | NoSuchServiceIdException | NoSuchStationNameException e){
        e.printStackTrace();
        throw new ImportFileException();
    }
  }

  public String getFileName() {
    return _filename;
  }

  public void setFileName(String filename){
    _filename = filename;
  }


  public List<Service> getAllServices(){
    return _trains.getAllServices();
  }

  public List<Passenger> getAllPassengers(){
    return _trains.getAllPassengers();
  }

  public Service getServiceById(int id) throws NoSuchServiceIdException {
    return _trains.getServiceById(id);
  }


  public List<Service> getServicesByDepartureStation(String station) throws NoSuchStationNameException{
    return _trains.getServicesByDepartureStation(station);
  }

  public List<Service> getServicesByArrivingStation(String station) throws NoSuchStationNameException{
      return _trains.getServicesByArrivingStation(station);
  }

  public Passenger getPassengerById(int id) throws NoSuchPassengerIdException {
    return _trains.getPassengerById(id);
  }

  public boolean uniquePassengerName(String name) throws NonUniquePassengerNameException{
    return _trains.uniquePassengerName(name);
  }

  public void changePassengerName(int id, String name) throws NoSuchPassengerIdException, NonUniquePassengerNameException{
     _trains.changePassengerName(id, name);
  }

  public void registPassenger(String name) throws NonUniquePassengerNameException{
    _trains.registPassenger(name);
  }

  /*chica*/
  public boolean existItinerariesPassenger(int passengerId) throws NoSuchPassengerIdException{
      return _trains.existItinerariesPassenger(passengerId);
  }

  public String printItineraries(int passengerId, DefaultVisitor visitor) throws NoSuchPassengerIdException {
    return _trains.printItineraries(passengerId, visitor);
  }

  public void searchItineraries(int passengerId, String first, String last, String departureDate, String departureTime) throws NoSuchPassengerIdException, NoSuchStationNameException, BadDateSpecificationException, BadTimeSpecificationException {  /*NoSuchItineraryChoiceException, */
    _trains.searchItineraries(passengerId, first, last, departureDate, departureTime);
  }

  public void commitItinerary(int passengerId, int itineraryNumber) throws NoSuchPassengerIdException, NoSuchItineraryChoiceException {
    _trains.commitItinerary(passengerId, itineraryNumber);
  }

  public boolean existsListToShow(int passengerId) throws NoSuchPassengerIdException{
    return _trains.existsListToShow(passengerId);
  }

  public String printListToShow(int passengerId, DefaultVisitor visitor) throws NoSuchPassengerIdException{
      return _trains.printListToShow(passengerId, visitor);
  }

}
