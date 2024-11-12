public class Sudoku {
    private int[][] board; // 9x9 grid to represent the Sudoku board

    // Constructor to initialize the board with a given initial state
    public Sudoku(int[][] initialBoard) {
        board = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                board[i][j] = initialBoard[i][j];
            }
        }
    }

    // Method to get the current board (for GUI)
    public int[][] getBoard() {
        return board;
    }

    // Method to set the board based on a 2D array
    public void setBoard(int[][] newBoard) {
        this.board = newBoard;
    }

    // Solve method to initiate the backtracking process
    public boolean solve() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] == 0) { // Empty cell found
                    for (int num = 1; num <= 9; num++) { // Try digits 1-9
                        if (isValid(row, col, num)) {
                            board[row][col] = num; // Place the number

                            if (solve()) { // Recursively attempt to solve
                                return true; // Solution found
                            }

                            // Reset the cell (backtrack)
                            board[row][col] = 0;
                        }
                    }
                    return false; // No valid number found for this cell
                }
            }
        }
        return true; // Puzzle solved
    }

    // Method to check if a number can be placed in the given row and column
    private boolean isValid(int row, int col, int num) {
        // Check row
        for (int c = 0; c < 9; c++) {
            if (board[row][c] == num) {
                return false; // Found a duplicate in the row
            }
        }

        // Check column
        for (int r = 0; r < 9; r++) {
            if (board[r][col] == num) {
                return false; // Found a duplicate in the column
            }
        }

        // Check 3x3 grid
        int gridRowStart = row - row % 3;
        int gridColStart = col - col % 3;
        for (int r = gridRowStart; r < gridRowStart + 3; r++) {
            for (int c = gridColStart; c < gridColStart + 3; c++) {
                if (board[r][c] == num) {
                    return false; // Found a duplicate in the grid
                }
            }
        }

        return true; // No conflicts found
    }
}
