package gov.epa.ghg.exception;

@SuppressWarnings("serial")
public class DataAccessException extends RuntimeException {

  public DataAccessException() {
    super();
  }
  
  public DataAccessException(String message) {
    super(message);
  }
  
  public DataAccessException(String message, Throwable cause) {
    super(message, cause);
  }
}
