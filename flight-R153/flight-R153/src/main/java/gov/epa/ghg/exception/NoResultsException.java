package gov.epa.ghg.exception;

@SuppressWarnings("serial")
public class NoResultsException extends Exception {

  public NoResultsException() {
    super();
  }

  public NoResultsException(String msg, Throwable cause) {
    super(msg, cause);
  }

  public NoResultsException(String msg) {
    super(msg);
  }
}
