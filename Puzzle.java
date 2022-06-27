import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Class to represent the current state of the Futoshiki puzzle. The puzzle may
 * either be completed or partially solved.
 * 
 * @author Seth Zerwas
 *
 */
public class Puzzle {

    /** Constant used to represent an empty square on the puzzle board */
    public static final int EMPTY = 0;

    /** Height and width of puzzle. The puzzle has size*size squares. */
    private int size;

    /** The numerical values currently filled in */
    private int[][] values;

    /** The vertical inequalities in the puzzle */
    private Constraint[][] verticalConstraints;

    /** The horizontal inequalities in the puzzle */
    private Constraint[][] horizontalConstraints;

    /**
     * Constructor to build an empty puzzle that is size by size. By default every
     * square is set to EMPTY and every constraint is set to NONE.
     * 
     * @param size The puzzle dimension (height and width).
     */
    public Puzzle(int size) {
        this.size = size;
        values = new int[size][size];
        // initialize to 0
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                values[row][column] = EMPTY;
            }
        }

        horizontalConstraints = new Constraint[size][size - 1];
        // initialize to None
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size - 1; column++) {
                horizontalConstraints[row][column] = Constraint.NONE;
            }
        }

        verticalConstraints = new Constraint[size - 1][size];
        // initialize to None
        for (int row = 0; row < size - 1; row++) {
            for (int column = 0; column < size; column++) {
                verticalConstraints[row][column] = Constraint.NONE;
            }
        }
    }

    /**
     * Constructor to make a copy of an existing puzzle. The copy contains new
     * arrays for values and constraints so there is no danger of two Puzzle objects
     * having references to the same arrays.
     * 
     * @param old The Puzzle we wish to make a copy of.
     */
    public Puzzle(Puzzle old) {
        // copy constructor
        this.size = old.size;
        this.values = new int[size][size];
        this.horizontalConstraints = new Constraint[size][size - 1];
        this.verticalConstraints = new Constraint[size - 1][size];

        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                values[row][column] = old.values[row][column];
            }
        }

        horizontalConstraints = new Constraint[size][size - 1];
        // initialize to None
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size - 1; column++) {
                horizontalConstraints[row][column] = old.horizontalConstraints[row][column];
            }
        }

        verticalConstraints = new Constraint[size - 1][size];
        // initialize to None
        for (int row = 0; row < size - 1; row++) {
            for (int column = 0; column < size; column++) {
                verticalConstraints[row][column] = old.verticalConstraints[row][column];
            }
        }

    }

    /**
     * Static factory method to build a Puzzle out of a file given the filename
     * (which should include an extension like .txt). The file needs to be in the
     * expected format or this method throws an exception.
     * 
     * @param filename The filename of a text file containing a correctly-formatted
     *                 Futoshiki puzzle.
     * @return The Puzzle object constructed from the data in the file.
     */
    public static Puzzle fromFile(String filename) {
        List<String> lines;

        try {
            lines = Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalArgumentException("File unable to be read");
        }

        // check dimensions
        int rows = lines.size() / 2 + 1;
        int cols = lines.get(0).length() / 2 + 1;

        if (rows != cols) {
            // file not in correct format
            throw new IllegalArgumentException("Number of rows does not match number of columns");
        }
        // make sure every row same length
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).length() != lines.get(0).length()) {
                throw new IllegalArgumentException("Not every row is the same length");
            }
        }

        Puzzle current = new Puzzle(rows);

        // add the numbers
        for (int i = 0; i < rows; i++) {
            String currentLine = lines.get(2 * i);
            for (int j = 0; j < cols; j++) {
                char currentChar = currentLine.charAt(2 * j);
                if (Character.isDigit(currentChar)) {
                    int digit = Character.digit(currentChar, 10);
                    if (digit > 0) {
                        current.insertValue(i, j, digit);
                    }
                } else {
                    throw new IllegalArgumentException("Only digits allowed");
                }
            }
        }
        // add horizonal constraints
        for (int i = 0; i < rows; i++) {
            String currentLine = lines.get(2 * i);
            for (int j = 0; j < cols - 1; j++) {
                char currentChar = currentLine.charAt(2 * j + 1);
                if (currentChar == '<') {
                    current.addHorizontalConstraint(i, j, Constraint.LESS);
                } else if (currentChar == '>') {
                    current.addHorizontalConstraint(i, j, Constraint.GREATER);
                }
                // else here doesn't lead to error, just ignored, though should be a space
            }
        }

        // add vertical constraints
        for (int i = 0; i < rows - 1; i++) {
            String currentLine = lines.get(2 * i + 1);
            for (int j = 0; j < cols; j++) {
                char currentChar = currentLine.charAt(2 * j);
                if (currentChar == '<') {
                    current.addVerticalConstraint(i, j, Constraint.LESS);
                } else if (currentChar == '>') {
                    current.addVerticalConstraint(i, j, Constraint.GREATER);
                }
                // else here doesn't lead to error, though should be a dash
            }
        }

        return current;
    }

    /**
     * Getter for the size of the puzzle.
     * 
     * @return The dimension of the puzzle.
     */
    public int getSize() {
        return size;
    }

    /**
     * Method to find the current value at a particular row and column.
     * 
     * @param row    The row number, using 0 indexing
     * @param column The column number, using 0 indexing
     * @return The value currently in the puzzle at the given row and column
     */
    public int getValue(int row, int column) {
        return values[row][column];
    }

    /**
     * Method to insert a value into the puzzle at a particular row and column.
     * 
     * @param row    The row number, using 0 indexing
     * @param column The column number, using 0 indexing
     * @param value  The value to be inserted at the given row and column. Should be
     *               between 1 and the size of the puzzle.
     */
    public void insertValue(int row, int column, int value) {
        values[row][column] = value;
        // add error checking and exceptions
    }

    /**
     * Method to add a vertical inequality to the puzzle at a specified location.
     * The location of the constraint is given by the row and column of the square
     * above the inequality.
     * 
     * @param row             The row of the square above the constraint location
     * @param column          The column of the square above the constraint location
     * @param constraintToAdd The enum constant for the constraint to add
     */
    public void addVerticalConstraint(int row, int column, Constraint constraintToAdd) {
        // row column should be coordinates of top square involved
        verticalConstraints[row][column] = constraintToAdd;
    }

    /**
     * Method to add a horizontal inequality to the puzzle at a specified location.
     * The location of the constraint is given by the row and column of the square
     * to the left of the inequality.
     * 
     * @param row             The row of the square left of the constraint location
     * @param column          The column of the square left of the constraint
     *                        location
     * @param constraintToAdd The enum constant for the constraint to add
     */
    public void addHorizontalConstraint(int row, int column, Constraint constraintToAdd) {
        // row column should be coordinates of left square involved
        horizontalConstraints[row][column] = constraintToAdd;
    }

    /**
     * Method to determine if the puzzle is completely filled in and valid.
     * 
     * @return true if the puzzle is solved, false otherwise.
     */
    public boolean isSolved() {
        // no empty spots and valid
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                if (values[row][column] == EMPTY) {
                    return false;
                }
            }
        }
        // no empty, just check if valid
        return isValid();
    }

    /**
     * Method to determine if the puzzle is valid or not. A puzzle is not valid if a
     * row or column has more than 1 of an allowed value or if a constraint is not
     * satisfied.
     * 
     * @return true if the puzzle is valid, false otherwise.
     */
    public boolean isValid() {
        // go through and check all consistent
        boolean validity = true;
        for (int row = 0; row < size; row++) {
            validity = validity && checkRow(row);
        }
        for (int column = 0; column < size; column++) {
            validity = validity && checkColumn(column);
        }

        return validity;
    }

    /**
     * Helper method to determine if a given column is valid, meaning it doesn't
     * have 2 or more of any value and the constraints are all satisfied.
     * 
     * @param columnNumber The column number (0 indexing) we wish to check validity
     *                     of.
     * @return true if the column is currently valid, false otherwise.
     */
    private boolean checkColumn(int columnNumber) {
        // make sure column does not have duplicate numbers
        int[] counts = new int[size + 1];

        for (int row = 0; row < size; row++) {
            int currentValue = values[row][columnNumber];
            if (!(currentValue >= 1 && currentValue <= size) && currentValue != EMPTY) {
                // value outside allowed range is present
                return false;
            }
            counts[currentValue] = counts[currentValue] + 1;
        }
        for (int i = 1; i <= size; i++) {
            if (counts[i] > 1) {
                return false;
            }
        }
        // numbers are all valid, need to check constraints
        for (int row = 0; row < size - 1; row++) {
            Constraint currentConstraint = verticalConstraints[row][columnNumber];
            if (values[row][columnNumber] != EMPTY && values[row + 1][columnNumber] != EMPTY
                    && currentConstraint.satisfied(values[row][columnNumber], values[row + 1][columnNumber]) == false) {
                return false;
            }
        }
        // no problems found
        return true;
    }

    /**
     * Helper method to determine if a given row is valid, meaning it doesn't have 2
     * or more of any value and the constraints are all satisfied.
     * 
     * @param rowNumber The row number (0 indexing) we wish to check validity of.
     * @return true if the row is currently valid, false otherwise.
     */
    private boolean checkRow(int rowNumber) {
        // make sure row does not have duplicate numbers
        int[] counts = new int[size + 1];

        for (int column = 0; column < size; column++) {
            int currentValue = values[rowNumber][column];
            if (!(currentValue >= 1 && currentValue <= size) && currentValue != EMPTY) {
                // value outside allowed range is present
                return false;
            }
            counts[currentValue] = counts[currentValue] + 1;
        }
        for (int i = 1; i <= size; i++) {
            if (counts[i] > 1) {
                return false;
            }
        }

        // numbers are all valid, need to check constraints
        for (int column = 0; column < size - 1; column++) {
            Constraint currentConstraint = horizontalConstraints[rowNumber][column];

            if (values[rowNumber][column] != EMPTY && values[rowNumber][column + 1] != EMPTY
                    && currentConstraint.satisfied(values[rowNumber][column], values[rowNumber][column + 1]) == false) {

                return false;
            }
        }

        // no problems found
        return true;
    }

    /**
     * Method used to get a string representation of the puzzle for displaying. Note
     * that this is different from how a puzzle is expected to be written in a file.
     * 
     * @return A string representation of the puzzle.
     */
    @Override
    public String toString() {
        String boardString = "";

        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                if (values[row][column] == EMPTY) {
                    boardString = boardString + "_";
                } else {
                    boardString = boardString + values[row][column];
                }
                if (column < size - 1) {
                    boardString = boardString + horizontalConstraints[row][column].getHorizontalDisplay();
                }
            }
            boardString = boardString + "\n";
            if (row < size - 1) {
                for (int column = 0; column < size; column++) {
                    boardString = boardString + verticalConstraints[row][column].getVerticalDisplay() + " ";

                }
                boardString = boardString + "\n";
            }

        }

        return boardString;
    }

    /**
     * Method to find a solution to given puzzle using recursive backtracking.
     * 
     * @param game        The partially-solved puzzle to solve.
     * @param currentSpot The first spot without a value. The spot should be a
     *                    number from 0 to (size x size)-1.
     * @return A Puzzle object with every value filled in if the puzzle has a
     *         solution, and null if no solution is found.
     */
    public static Puzzle solve(Puzzle game, int currentSpot) {
        // System.out.println(game);

        int size = game.getSize();
        if (currentSpot >= size * size) {
            // return null;
            if (game.isSolved()) {
                return game;
            }
            return null;
        }
        int currentRow = currentSpot / size;
        int currentColumn = currentSpot % size;
        if (game.getValue(currentRow, currentColumn) == Puzzle.EMPTY) {
            // empty spot, can try out possibilities
            for (int i = 1; i <= size; i++) {
                Puzzle next = new Puzzle(game);
                next.insertValue(currentRow, currentColumn, i);

                if (next.isValid() == true) {

                    // so far so good, move on to next spot
                    Puzzle returned = solve(next, currentSpot + 1);
                    if (returned != null) {
                        return returned;
                    }
                }
            }
            return null;
        } else {

            return solve(game, currentSpot + 1);
        }

    }

}
