import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;

/**
 * Class to solve the puzzle using Fork/Join
 * 
 * @author Seth Zerwas
 *
 */
public class PuzzleSolver extends RecursiveAction {

    /** Puzzle object that holds the current puzzle that is trying to be solved */
    private Puzzle currentPuzzle;

    /** Integer that holds the current spot in the puzzle */
    private int currentSpot;

    /** Puzzle object that holds the answer to the puzzle */
    private Puzzle answer;

    /**
     * Constructor to build a new puzzleSolver object
     * 
     * @param current The current puzzle that needs to be solved
     * @param spot    The current spot on the puzzle
     */
    public PuzzleSolver(Puzzle current, int spot) {
        currentPuzzle = current;
        currentSpot = spot;
        // set the answer for each puzzleSolver object to null
        this.answer = null;
    }

    /**
     * Method to get the puzzle answer
     * 
     * @return Returns the answer to the puzzle
     */
    public Puzzle getAnswer() {
        return this.answer;
    }

    /**
     * Method to quickly solve the puzzle using fork/join
     */
    @Override
    protected void compute() {
        // creates a new ArrayList to store the different ways to solve the puzzle at
        // the current spot
        ArrayList<PuzzleSolver> puzzleWorkers = new ArrayList<>();
        int puzzleSize = currentPuzzle.getSize();
        if (puzzleSize * puzzleSize - currentSpot < 20) {
            this.answer = Puzzle.solve(currentPuzzle, currentSpot);
        } else {
            int currentRow = currentSpot / puzzleSize;
            int currentColumn = currentSpot % puzzleSize;
            if (currentPuzzle.getValue(currentRow, currentColumn) == Puzzle.EMPTY) {
                for (int i = 1; i <= puzzleSize; i++) {
                    Puzzle next = new Puzzle(currentPuzzle);
                    next.insertValue(currentRow, currentColumn, i);
                    if (next.isValid()) {
                        // if puzzle is valid, then puzzle is turned into a new PuzzleSolver object
                        PuzzleSolver tryPuzzle = new PuzzleSolver(next, currentSpot + 1);
                        // new PuzzleSolver object is added to the ArrayList.
                        puzzleWorkers.add(tryPuzzle);
                    }
                }
                // for loop to begin parallelism with the PuzzleSolver objects within
                // puzzleWorker ArrayList using fork
                for (PuzzleSolver puzzleWorker : puzzleWorkers) {
                    puzzleWorker.fork();
                }
                // for loop joining all of the PuzzleSolver objects after being forked
                for (PuzzleSolver puzzleWorker : puzzleWorkers) {
                    puzzleWorker.join();
                }
                // for loop to see which of the PuzzleSolver objects contains a solved puzzle
                for (PuzzleSolver puzzleWorker : puzzleWorkers) {
                    if (puzzleWorker.answer != null) {
                        this.answer = puzzleWorker.answer;
                    }
                }
            } else {
                // if the spot was not empty then a new PuzzleSolver object is created trying
                // the next spot in the puzzle.
                PuzzleSolver tryPuzzle = new PuzzleSolver(currentPuzzle, currentSpot + 1);
                tryPuzzle.compute();
                this.answer = tryPuzzle.answer;

            }
        }
    }
}
