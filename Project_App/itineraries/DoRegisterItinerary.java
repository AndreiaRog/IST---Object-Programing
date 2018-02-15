package mmt.app.itineraries;
import mmt.DefaultVisitor;

import mmt.TicketOffice;
import mmt.app.exceptions.BadDateException;
import mmt.app.exceptions.BadTimeException;
import mmt.app.exceptions.NoSuchItineraryException;
import mmt.app.exceptions.NoSuchPassengerException;
import mmt.app.exceptions.NoSuchStationException;
import mmt.exceptions.BadDateSpecificationException;
import mmt.exceptions.BadTimeSpecificationException;
import mmt.exceptions.NoSuchPassengerIdException;
import mmt.exceptions.NoSuchStationNameException;
import mmt.exceptions.NoSuchItineraryChoiceException;
import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;
import pt.tecnico.po.ui.Form;


//FIXME import other classes if necessary

/**
 * §3.4.3. Add new itinerary.
 */
public class DoRegisterItinerary extends Command<TicketOffice> {

  Input<Integer> _id;
  Input<String> _depStation;
  Input<String> _arrStation;
  Input<String> _date;
  Input<String> _time;
  Input<Integer> _itinerary;
  private DefaultVisitor _visitor=new DefaultVisitor();
  Form _form2;

  /**
   * @param receiver
   */
  public DoRegisterItinerary(TicketOffice receiver) {
    super(Label.REGISTER_ITINERARY, receiver);
    _id = _form.addIntegerInput(Message.requestPassengerId());
    _depStation = _form.addStringInput(Message.requestDepartureStationName());
    _arrStation = _form.addStringInput(Message.requestArrivalStationName());
    _date = _form.addStringInput(Message.requestDepartureDate());
    _time = _form.addStringInput(Message.requestDepartureTime());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException {
    _form.parse();
    try {
        _receiver.searchItineraries(_id.value(), _depStation.value(), _arrStation.value(), _date.value(), _time.value());
        if(_receiver.existsListToShow(_id.value())){
            _display.popup(_receiver.printListToShow(_id.value(), _visitor));
            _form2 = new Form(Label.REGISTER_ITINERARY);
            _itinerary = _form2.addIntegerInput(Message.requestItineraryChoice());
            _form2.parse();
            _receiver.commitItinerary(_id.value(), _itinerary.value());
      }
    } catch (NoSuchPassengerIdException e) {
      throw new NoSuchPassengerException(e.getId());
    } catch (NoSuchStationNameException e) {
      throw new NoSuchStationException(e.getName());
    } catch (NoSuchItineraryChoiceException e) {
      throw new NoSuchItineraryException(e.getPassengerId(), e.getItineraryId());
    } catch (BadDateSpecificationException e) {
      throw new BadDateException(e.getDate());
    } catch (BadTimeSpecificationException e) {
      throw new BadTimeException(e.getTime());
    }

      // must call (at least) _receiver.searchItineraries(...) and _receiver.commitItinerary(...)

 }
}
