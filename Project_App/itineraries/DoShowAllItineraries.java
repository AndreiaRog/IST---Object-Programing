package mmt.app.itineraries;

import mmt.DefaultVisitor;
import mmt.TicketOffice;

import mmt.exceptions.NoSuchPassengerIdException;
import mmt.app.exceptions.NoSuchPassengerException;

import pt.tecnico.po.ui.Command;

//FIXME import other classes if necessary

/**
 * §3.4.1. Show all itineraries (for all passengers).
 */
public class DoShowAllItineraries extends Command<TicketOffice> {

    private DefaultVisitor _visitor = new DefaultVisitor();
  /**
   * @param receiver
   */
  public DoShowAllItineraries(TicketOffice receiver) {
    super(Label.SHOW_ALL_ITINERARIES, receiver);
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() {
    try{
      for (int id=0; id < _receiver.getAllPassengers().size(); id++){
          if (_receiver.existItinerariesPassenger(id)){
              _display.addLine(_receiver.printItineraries(id, _visitor));
          }
      }
      _display.display();
    }catch (NoSuchPassengerIdException e){
        e.printStackTrace();
    }
  }

}
