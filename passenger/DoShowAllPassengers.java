package mmt.app.passenger;

import mmt.DefaultVisitor;
import mmt.TicketOffice;
import mmt.Passenger;
import pt.tecnico.po.ui.Command;

/**
 * §3.3.1. Show all passengers.
 */
public class DoShowAllPassengers extends Command<TicketOffice> {

    DefaultVisitor _visitor = new DefaultVisitor();
  /**
   * @param receiver
   */
  public DoShowAllPassengers(TicketOffice receiver) {
    super(Label.SHOW_ALL_PASSENGERS, receiver);
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() {
      for (Passenger p: _receiver.getAllPassengers()){
        //_display.addLine(p.toString());
        _display.addLine(p.accept(_visitor));
      }
      _display.display();
  }

}
