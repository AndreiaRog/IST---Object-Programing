package mmt.exceptions;

/** Exception thrown when the passenger information already exists*/

public class DuplicatePassengerException extends Exception {

    public DuplicatePassengerException(){
        super("This passenger already exists");
    }
// verificar !
}
