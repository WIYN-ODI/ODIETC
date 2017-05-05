package za.ac.salt.pipt.common.dataExchange;


/** If a class implements this interface it claims that for each setter method setQuantity() method it has a corresponding safeSetQuantity() method. The latter checks whether the given value is valid. If this happens to be the case, it calls the setQuantity() method. Otherwise it throws an InvalidValueException. The interface itself doesn't contain any methods. */
public interface PiptSafelyAccessible
{
}
