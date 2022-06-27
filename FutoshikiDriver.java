import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

/**
 * Class to run the Futoshiki game
 * 
 * @author Seth Zerwas
 *
 */
public class FutoshikiDriver {

    public static void main(String[] args) {
        int puzzleNumber;
        int puzzleSolver;
        long startTime;
        long endTime;
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Welcome to the Futoshiki solver");
        System.out.print("Enter a puzzle number: ");
        puzzleNumber = keyboard.nextInt();
        try {
            System.out.println(Puzzle.fromFile(puzzleNumber + ".txt"));
            System.out.println("How would you like to solve the puzzle\n1. Sequential\n2. Fork/Join");
            puzzleSolver = keyboard.nextInt();
            if (puzzleSolver == 1) {
                System.out.println("Solving sequentially...");
                startTime = System.currentTimeMillis();
                Puzzle solvePuzzle = Puzzle.solve(Puzzle.fromFile(puzzleNumber + ".txt"), 0);
                endTime = System.currentTimeMillis();
                // checks to see if the answer to the puzzle is null
                if (solvePuzzle == null) {
                    System.out.println("Impossible to solve");
                } else {
                    System.out.println(solvePuzzle);
                    System.out.println("Solution found in " + (endTime - startTime) / 1000.0 + " seconds");
                }
            } else if (puzzleSolver == 2) {
                System.out.println("Solving Fork/Join...");
                startTime = System.currentTimeMillis();
                PuzzleSolver initial = new PuzzleSolver(Puzzle.fromFile(puzzleNumber + ".txt"), 0);
                ForkJoinPool.commonPool().invoke(initial);
                endTime = System.currentTimeMillis();
                // checks to see if the answer to the puzzle is null
                if (initial.getAnswer() == null) {
                    System.out.println("Impossible to solve");
                } else {
                    System.out.println(initial.getAnswer());
                    System.out.println("Solution found in " + (endTime - startTime) / 1000.0 + " seconds");
                }
            } else {
                throw new IllegalArgumentException("Invalid option");
            }

        } catch (IllegalArgumentException e) {
            System.out.println(e);
        }

    }

}
