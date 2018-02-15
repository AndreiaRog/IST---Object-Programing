package mmt.exceptions;

/** Exception thrown when the passenger information already exists*/

public class DuplicateServiceException extends Exception {

    public DuplicateServiceException(){
        super("This service id already exists");
    }
// verificar !
}
