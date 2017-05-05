package za.ac.salt.pipt.common;

import za.ac.salt.pipt.common.dataExchange.InvalidValueException;


/** An enumeration of the possible phases for a proposal. */
public enum Phase
{
    ONE, TWO, UNSPECIFIED;

    /** Returns the phase corresponding to the given integer value (i.e. ONE for 1 and TWO for 2).
     * @param phaseIntValue the integer value
     * @return the corresponding phase
     * @throws InvalidValueException if the given integer value cannot be parsed */
    public static Phase parse(long phaseIntValue)
    {
	switch ((int) phaseIntValue) {
	case 1:
	    return ONE;
	case 2:
	    return TWO;
	}

	// If we get here, the integer value obviously is invalid.
	throw new InvalidValueException("The integer value " + phaseIntValue + " doesn't correspond to any phase.");
    }


    /** Returns the integer value of the given phase (i.e. 1 for ONE and 2 for TWO). In case of an unspecified phase, -1 is returned.
     * @param phase the phase
     * @return the integer value of the given phase */
    public static int intValue(Phase phase)
    {
	switch (phase) {
	case ONE:
	    return 1;
	case TWO:
	    return 2;
	case UNSPECIFIED:
	    return -1;
	}

	// Should never be reached.
	return -1;
    }
}
