package mmt.app.main;

import mmt.TicketOffice;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.Input;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ClassNotFoundException;


/**
 * §3.1.1. Open existing document.
 */
public class DoOpen extends Command<TicketOffice> {
  /**
   * @param receiver
   */
  private Input<String> _filename;

  public DoOpen(TicketOffice receiver) {
    super(Label.OPEN, receiver);
    _filename = _form.addStringInput(Message.openFile());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute(){
    _form.parse();
    try{
     _receiver.load(_filename.value());
    } catch (FileNotFoundException fnfe) {
      _display.popup(Message.fileNotFound());
    } catch (ClassNotFoundException e) {
      // shouldn't happen in a controlled test setup
      e.printStackTrace();
    } catch (IOException e) {
      // no behavior described: just present the problem
    e.printStackTrace();
    }
}

}
