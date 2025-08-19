package gov.epa.ghg.exception;

@SuppressWarnings("serial")
public class TooManyResultsException extends Exception {

  private int count = 0;

  public TooManyResultsException(int count) {
    super();
    this.count = count;
  }

  public TooManyResultsException(String msg, Throwable cause, int count) {
    super(msg, cause);
    this.count = count;
  }

  public TooManyResultsException(String msg, int count) {
    super(msg);
    this.count = count;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

}
