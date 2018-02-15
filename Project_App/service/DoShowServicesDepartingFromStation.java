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
 * 3.2.3 Show services departing from station.
 */
public class DoShowServicesDepartingFromStation extends Command<TicketOffice> {

    Input<String> _depStation;
    private DefaultVisitor _visitor=new DefaultVisitor();

  /**
   * @param receiver
   */
  public DoShowServicesDepartingFromStation(TicketOffice receiver) {
    super(Label.SHOW_SERVICES_DEPARTING_FROM_STATION, receiver);
    _depStation = _form.addStringInput(Message.requestStationName());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException {
    try{
      _form.parse();

        List<Service> services =_receiver.getServicesByDepartureStation(_depStation.value());

        for(Service s: services)
         _display.addLine(s.accept(_visitor));
         _display.display();

    } catch (NoSuchStationNameException e){
        e.printStackTrace();
        throw new NoSuchStationException(_depStation.value());
    }
  }

}
