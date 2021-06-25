package crm.spring.rest.exception;

public class DaoException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2228171204519157L;

	/*
	 * Constructeurs
	 */
	public DaoException(String message) {
		super(message);
	}

	public DaoException(String message, Throwable cause) {
		super(message, cause);
	}

	public DaoException(Throwable cause) {
		super(cause);
	}

}