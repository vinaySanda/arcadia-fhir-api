package io.arcadia.fhir.exception;

import org.hl7.fhir.instance.model.api.IBaseOperationOutcome;
import org.hl7.fhir.r4.model.OperationOutcome;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;

/**
 * <p>
 * This class is responsible for handling exceptions while resolving Arcadia end
 * points.
 * </p>
 * 
 * <p>
 * This extends {@link BaseServerResponseException} which is defined by HAPI to
 * handle FHIR exceptions.
 * </p>
 * 
 * @author Pradeep Kumara K
 *
 */
public class EndPointFailureException extends BaseServerResponseException {

	public static final int STATUS_CODE = Constants.STATUS_HTTP_500_INTERNAL_ERROR;
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor which takes only error message.
	 * 
	 * @param theMessage The error message
	 */
	public EndPointFailureException(String theMessage) {
		super(STATUS_CODE, theMessage);
	}

	/**
	 * Constructor which takes message and the cause.
	 * 
	 * @param theMessage The error message
	 * @param theCause   The cause of the error
	 */
	public EndPointFailureException(String theMessage, Throwable theCause) {
		super(STATUS_CODE, theMessage, theCause);
	}

	/**
	 * Constructor which takes only the cause.
	 * 
	 * @param theCause The cause of the error
	 */
	public EndPointFailureException(Throwable theCause) {
		super(STATUS_CODE, theCause);
	}

	/**
	 * Constructor which takes error message as well as {@link OperationOutcome}
	 * 
	 * @param theMessage          The error message
	 * @param theOperationOutcome The OperationOutcome is a FHIR resource which
	 *                            contains error details
	 */
	public EndPointFailureException(String theMessage, IBaseOperationOutcome theOperationOutcome) {
		super(STATUS_CODE, theMessage, theOperationOutcome);
	}
}
