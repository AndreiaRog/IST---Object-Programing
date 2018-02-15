package mmt.app.service;

import mmt.Service;
import mmt.TicketOffice;
import mmt.DefaultVisitor;
import pt.tecnico.po.ui.Command;


/**
 * 3.2.1 Show all services.
 */
public class DoShowAllServices extends Command<TicketOffice> {

    private DefaultVisitor _visitor = new DefaultVisitor();
  /**
   * @param receiver
   */
  public DoShowAllServices(TicketOffice receiver) {
    super(Label.SHOW_ALL_SERVICES, receiver);
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() {
    for (Service s : _receiver.getAllServices()){
      //_display.addLine(s.toString());
      _display.addLine(s.accept(_visitor));
    }
    _display.display();
  }

}
