import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class ConnectFourGUI extends JFrame {
    private static final int ROWS = 6;
    private static final int COLUMNS = 7;
    private final JButton[][] buttons;
    private final char[][] board;
    private char currentPlayer;
    private final ComputerPlayer AI_player;


    public ConnectFourGUI() {
        setTitle("Connect Four");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        buttons = new JButton[ROWS][COLUMNS];
        board = new char[ROWS][COLUMNS];
        currentPlayer = '1';
        AI_player = new ComputerPlayer();
        initComponents();
    }
    private void initComponents() {
        JPanel boardPanel = new JPanel(new GridLayout(ROWS, COLUMNS));
        boardPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        boardPanel.setBackground(new Color(4, 79, 180));
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                buttons[i][j] = new JButton(new CircleIcon(Color.WHITE));
                buttons[i][j].setOpaque(false);
                buttons[i][j].setContentAreaFilled(false);
                buttons[i][j].setBorderPainted(false);
                buttons[i][j].addActionListener(new ButtonClickListener(j));
                boardPanel.add(buttons[i][j]);
                board[i][j] = ' ';
            }
        }
        setLayout(new BorderLayout());
        add(boardPanel, BorderLayout.CENTER);
    }
    private class ButtonClickListener implements ActionListener {
        private final int column;
        public ButtonClickListener(int column) {
            this.column = column;
        }
        public void actionPerformed(ActionEvent e) {
            if (currentPlayer == '1') {
                dropToken(column);
                updateGUI();
                if (checkForWin()) {
                    highlightWinningRow();
                    JOptionPane.showMessageDialog(null, "You Won! ");
                    resetGame();
                } else if (isBoardFull()) {
                    JOptionPane.showMessageDialog(null, "Oh no Its a Draw! ");
                    resetGame();
                } else {
                    switchPlayer();
                }
            }
            // Computer's turn
            if (currentPlayer == '2') {
                int computerColumn = AI_player.makeMove(board);
                dropToken(computerColumn);
                updateGUI();
                if (checkForWin()) {
                    highlightWinningRow();
                    JOptionPane.showMessageDialog(null, "Computer Won! :( ");
                    resetGame();
                } else if (isBoardFull()) {
                    JOptionPane.showMessageDialog(null, "Oh no Its a Draw!");
                    resetGame();
                } else {
                    switchPlayer();
                }
            }
        }
    }
    private static class ComputerPlayer {
        public int makeMove(char[][] board) {
            Random random = new Random();
            int column;
            do {
                column = random.nextInt(COLUMNS);
            } while (!isValidMove(board, column));
            return column;
        }
        private boolean isValidMove(char[][] board, int column) {
            // Check if the selected column is not full
            return board[0][column] == ' ';
        }
    }
    private void dropToken(int column) {
        for (int i = ROWS - 1; i >= 0; i--) {
            if (board[i][column] == ' ') {
                board[i][column] = currentPlayer;
                break;
            }
        }
    }
    private void switchPlayer() {

        currentPlayer = (currentPlayer == '1') ? '2' : '1';
    }
    private boolean checkForWin() {

        return checkHorizontal() || checkVertical() || checkDiagonal();
    }
    private boolean checkHorizontal() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j <= COLUMNS - 4; j++) {
                if (board[i][j] == currentPlayer &&
                        board[i][j + 1] == currentPlayer &&
                        board[i][j + 2] == currentPlayer &&
                        board[i][j + 3] == currentPlayer) {
                    return true;
                }
            }
        }
        return false;
    }
    private boolean checkVertical() {
        for (int i = 0; i <= ROWS - 4; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if (board[i][j] == currentPlayer &&
                        board[i + 1][j] == currentPlayer &&
                        board[i + 2][j] == currentPlayer &&
                        board[i + 3][j] == currentPlayer) {
                    return true;
                }
            }
        }
        return false;
    }
    private boolean checkDiagonal() {
        for (int i = 0; i <= ROWS - 4; i++) {
            for (int j = 0; j <= COLUMNS - 4; j++) {
                if (board[i][j] == currentPlayer &&
                        board[i + 1][j + 1] == currentPlayer &&
                        board[i + 2][j + 2] == currentPlayer &&
                        board[i + 3][j + 3] == currentPlayer) {
                    return true;
                }

                if (board[i][j + 3] == currentPlayer &&
                        board[i + 1][j + 2] == currentPlayer &&
                        board[i + 2][j + 1] == currentPlayer &&
                        board[i + 3][j] == currentPlayer) {
                    return true;
                }
            }
        }
        return false;
    }
    private boolean isBoardFull() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if (board[i][j] == ' ') {
                    return false;
                }
            }
        }
        return true;
    }
    private void updateGUI() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if (board[i][j] == '1') {
                    buttons[i][j].setIcon(new CircleIcon(new Color(255, 255, 0)));
                } else if (board[i][j] == '2') {
                    buttons[i][j].setIcon(new CircleIcon(new Color(255, 165, 0)));
                }
            }
        }
    }
    private void highlightWinningRow() {
        int[] winningRow = findWinningRow();
        if (winningRow != null) {
            int startRow = winningRow[0];
            int startColumn = winningRow[1];
            int endRow = winningRow[2];
            int endColumn = winningRow[3];

            if (startRow == endRow) {
                // Highlighting a horizontal row
                for (int j = startColumn; j <= endColumn; j++) {
                    highlightButton(startRow, j);
                }
            } else if (startColumn == endColumn) {
                // Highlighting a vertical row
                for (int i = startRow; i <= endRow; i++) {
                    highlightButton(i, startColumn);
                }
            } else {
                // Highlighting a diagonal row
                for (int i = startRow, j = startColumn; i <= endRow && j <= endColumn; i++, j++) {
                    highlightButton(i, j);
                }
            }
        }
    }
    private void highlightButton(int row, int column) {
        buttons[row][column].setOpaque(true);
        buttons[row][column].setBackground(new Color(0, 255, 0));
        buttons[row][column].updateUI();
    }
    private int[] findWinningRow() {
        // Check horizontal
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j <= COLUMNS - 4; j++) {
                if (board[i][j] == currentPlayer &&
                        board[i][j + 1] == currentPlayer &&
                        board[i][j + 2] == currentPlayer &&
                        board[i][j + 3] == currentPlayer) {
                    return new int[]{i, j, i, j + 3};
                }
            }
        }
        // Check vertical
        for (int i = 0; i <= ROWS - 4; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if (board[i][j] == currentPlayer &&
                        board[i + 1][j] == currentPlayer &&
                        board[i + 2][j] == currentPlayer &&
                        board[i + 3][j] == currentPlayer) {
                    return new int[]{i, j, i + 3, j};
                }
            }
        }
        // Check diagonal (top-left to bottom-right)
        for (int i = 0; i <= ROWS - 4; i++) {
            for (int j = 0; j <= COLUMNS - 4; j++) {
                if (board[i][j] == currentPlayer &&
                        board[i + 1][j + 1] == currentPlayer &&
                        board[i + 2][j + 2] == currentPlayer &&
                        board[i + 3][j + 3] == currentPlayer) {
                    return new int[]{i, j, i + 3, j + 3};
                }
            }
        }
        // Check diagonal (top-right to bottom-left)
        for (int i = 0; i <= ROWS - 4; i++) {
            for (int j = 0; j <= COLUMNS - 4; j++) {
                if (board[i][j + 3] == currentPlayer &&
                        board[i + 1][j + 2] == currentPlayer &&
                        board[i + 2][j + 1] == currentPlayer &&
                        board[i + 3][j] == currentPlayer) {
                    return new int[]{i, j + 3, i + 3, j};
                }
            }
        }
        return null;
    }
    private void resetGame() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                board[i][j] = ' ';
                buttons[i][j].setIcon(new CircleIcon(Color.WHITE));
                buttons[i][j].setBackground(null);
            }
        }
        currentPlayer = '1';
    }
    private static class CircleIcon implements Icon {
        private final Color color;
        public CircleIcon(Color color) {
            this.color = color;
        }
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(color);
            g2d.fillOval(x, y, getIconWidth(), getIconHeight());
            g2d.setColor(Color.RED); // Bold outline color
            g2d.setStroke(new BasicStroke(2)); // Bold outline width
            g2d.drawOval(x, y, getIconWidth(), getIconHeight());
            g2d.dispose();
        }
        public int getIconWidth() {
            return 70;
        }
        public int getIconHeight() {
            return 70;
        }
    }
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new ConnectFourGUI().setVisible(true));
    }
}
