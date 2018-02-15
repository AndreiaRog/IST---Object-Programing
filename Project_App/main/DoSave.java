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

//import mmt.exceptions.MissingFileAssociationException; TO USE


/**
 * §3.1.1. Save to file under current name (if unnamed, query for name).
 */
public class DoSave extends Command<TicketOffice> {

  Input<String> _filename;
  /**
   * @param receiver
   */
  public DoSave(TicketOffice receiver) {
    super(Label.SAVE, receiver);
    if (_receiver.getFileName().isEmpty()){
        _filename = _form.addStringInput(Message.newSaveAs());
        _receiver.setFileName(_filename.value());
    }
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() {
    //_form.parse();
    try{
        if(_receiver.getFileName() == null){
            _form.parse();
            _receiver.setFileName(_filename.value());
        }
        _receiver.save();
    /*} catch (MissingFileAssociationException e){
        e.printStackTrace(); */
    } catch (FileNotFoundException fnfe) {
        _display.popup(Message.fileNotFound());
    } catch (ClassNotFoundException e) {
        // shouldn't happen in a controlled test setup
        e.printStackTrace();
    }catch (IOException e) {
        // no behavior described: just present the problem
        e.printStackTrace();
    }
  }
}
