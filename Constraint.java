
/**
 * Enum used to represent the inequalities in a Futoshiki puzzle.
 * 
 * @author Seth Zerwas
 *
 */
public enum Constraint {
    /**
     * Constant to represent a less-than inequality. The vertical display is an
     * upside-down V.
     */
    LESS("<", "\u0245"),
    /** Constant to represent a greater-than inequality. */
    GREATER(">", "V"),
    /** Constant used when no inequality is present at a location. */
    NONE(" ", " ");

    /** How the constraint should be displayed if it is used horizontally. */
    private String horizontalDisplay;
    /** How the constant should be displayed if it is used vertically. */
    private String verticalDisplay;

    /**
     * Private constructor to initialize the two display strings.
     * 
     * @param horizontalDisplay How the constraint should be displayed if it is used
     *                          horizontally
     * @param verticalDisplay   How the constant should be displayed if it is used
     *                          vertically
     */
    private Constraint(String horizontalDisplay, String verticalDisplay) {
        this.horizontalDisplay = horizontalDisplay;
        this.verticalDisplay = verticalDisplay;
    }

    /**
     * Method to evaluate the inequality given two operands.
     * 
     * @param value1 The left operand
     * @param value2 The right operand
     * @return true if the inequality is satisfied or if the constant is NONE, false
     *         otherwise.
     */
    public boolean satisfied(int value1, int value2) {
        if (this == LESS) {
            return value1 < value2;
        } else if (this == GREATER) {
            return value1 > value2;
        } else {
            return true;
        }
    }

    /**
     * Getter for the vertical display character.
     * 
     * @return The vertical display String.
     */
    public String getVerticalDisplay() {
        return verticalDisplay;
    }

    /**
     * Getter for the horizontal display character.
     * 
     * @return The horizontal display String.
     */
    public String getHorizontalDisplay() {
        return horizontalDisplay;
    }

    /**
     * Method to return a string representation of the constraint. The
     * getVerticalDisplay and getHorizontalDisplay should be used instead, but this
     * method defaults to the horizontal display since the vertical may not be ASCII
     * characters.
     */
    public String toString() {
        return horizontalDisplay;
    }
}
