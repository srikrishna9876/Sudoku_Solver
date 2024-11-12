import java.util.Random;

public class SudokuGenerator {
    private static final int SIZE = 9;
    private static final int SUBGRID_SIZE = 3;

    public int[][] generateSudoku(int clues) {
        int[][] board = new int[SIZE][SIZE];
        fillBoard(board);
        removeNumbers(board, SIZE * SIZE - clues);
        return board;
    }

    private void fillBoard(int[][] board) {
        // Fill the Sudoku board using backtracking
        solveSudoku(board);
    }

    private boolean solveSudoku(int[][] board) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == 0) {
                    for (int num = 1; num <= 9; num++) {
                        if (isValid(board, row, col, num)) {
                            board[row][col] = num;
                            if (solveSudoku(board)) {
                                return true;
                            }
                            board[row][col] = 0; // Backtrack
                        }
                    }
                    return false;
                }
            }
        }
        return true; // Solved
    }

    private boolean isValid(int[][] board, int row, int col, int num) {
        // Check row, column, and subgrid for validity
        for (int x = 0; x < SIZE; x++) {
            if (board[row][x] == num || board[x][col] == num) {
                return false;
            }
        }
        int startRow = row - row % SUBGRID_SIZE, startCol = col - col % SUBGRID_SIZE;
        for (int r = 0; r < SUBGRID_SIZE; r++) {
            for (int d = 0; d < SUBGRID_SIZE; d++) {
                if (board[r + startRow][d + startCol] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    private void removeNumbers(int[][] board, int numToRemove) {
        Random rand = new Random();
        while (numToRemove > 0) {
            int row = rand.nextInt(SIZE);
            int col = rand.nextInt(SIZE);
            if (board[row][col] != 0) {
                board[row][col] = 0; // Remove number
                numToRemove--;
            }
        }
    }
}
