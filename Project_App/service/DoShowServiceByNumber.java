package mmt.app.service;

import mmt.TicketOffice;
import mmt.Service;
import mmt.DefaultVisitor;
import mmt.exceptions.NoSuchServiceIdException;
import mmt.app.exceptions.NoSuchServiceException;
import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

//FIXME import other classes if necessary

/**
 * 3.2.2 Show service by number.
 */
public class DoShowServiceByNumber extends Command<TicketOffice> {

  Input<Integer> _id;
  private DefaultVisitor _visitor = new DefaultVisitor();
  /**
   * @param receiver
   */
  public DoShowServiceByNumber(TicketOffice receiver) {
    super(Label.SHOW_SERVICE_BY_NUMBER, receiver);
    _id = _form.addIntegerInput(Message.requestServiceId());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException {
    _form.parse();
    try{
        Service s =_receiver.getServiceById(_id.value());
        //_display.popup(s.toString());
        _display.popup(s.accept(_visitor));
    } catch (NoSuchServiceIdException e){
        e.printStackTrace();
        throw new NoSuchServiceException(_id.value());
    }
  }

}
