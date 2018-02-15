package mmt.app.itineraries;

import mmt.DefaultVisitor;
import mmt.TicketOffice;


import mmt.exceptions.NoSuchPassengerIdException;
import mmt.app.exceptions.NoSuchPassengerException;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

/**
 * §3.4.2. Show all itineraries (for a specific passenger).
 */
public class DoShowPassengerItineraries extends Command<TicketOffice> {

    Input<Integer> _id;
    private DefaultVisitor _visitor=new DefaultVisitor();

  /**
   * @param receiver
   */
  public DoShowPassengerItineraries(TicketOffice receiver) {
    super(Label.SHOW_PASSENGER_ITINERARIES, receiver);
    _id = _form.addIntegerInput(Message.requestPassengerId());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException {
    _form.parse();
    try{
        if (_receiver.existItinerariesPassenger(_id.value())){
            _display.popup(_receiver.printItineraries(_id.value(), _visitor));
        }else{
            _display.popup(Message.noItineraries(_id.value()));
        }
    }catch (NoSuchPassengerIdException e){
        e.printStackTrace();
        throw new NoSuchPassengerException(_id.value());
    }
  }
}
