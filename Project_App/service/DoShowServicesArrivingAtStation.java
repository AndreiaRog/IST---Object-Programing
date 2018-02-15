package mmt.app.service;

import mmt.TicketOffice;
import mmt.Service;
import mmt.DefaultVisitor;
import mmt.exceptions.NoSuchStationNameException;
import mmt.app.exceptions.NoSuchStationException;
import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;
import java.util.List;


//FIXME import other classes if necessary

/**
 * 3.2.4 Show services arriving at station.
 */
public class DoShowServicesArrivingAtStation extends Command<TicketOffice> {

    Input<String> _arrStation;
    private DefaultVisitor _visitor = new DefaultVisitor();

  /**
   * @param receiver
   */
  public DoShowServicesArrivingAtStation(TicketOffice receiver) {
    super(Label.SHOW_SERVICES_ARRIVING_AT_STATION, receiver);
    _arrStation = _form.addStringInput(Message.requestStationName());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException {
    try{
      _form.parse();

        List<Service> services =_receiver.getServicesByArrivingStation(_arrStation.value());

        for(Service s: services)
         _display.addLine(s.accept(_visitor));
         _display.display();

    } catch (NoSuchStationNameException e){
        e.printStackTrace();
        throw new NoSuchStationException(_arrStation.value());
    }
  }

}
