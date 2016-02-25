package me.exrates.model.exceptions;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class UnsupportedOperationTypeException extends RuntimeException {

    public final int operationTypeId;

    public UnsupportedOperationTypeException(int tupleId) {
        super("No such operation type " + tupleId);
        this.operationTypeId = tupleId;
    }
}