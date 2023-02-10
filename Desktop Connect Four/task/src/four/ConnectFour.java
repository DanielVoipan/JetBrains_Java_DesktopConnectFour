package four;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class ConnectFour extends JFrame implements ActionListener {

    final static int LINE_NR = 6;
    final static int COL_NR = 7;

    char currentChar;
    List<Integer[]> winList;
    boolean endGame;

    private static final char[][] field = new char[LINE_NR][COL_NR];
    private static final Map<String, JButton> map = new HashMap<>();

    // anonymous to instantiate currentChar
    {
        currentChar = 'X';
        for (int i = 0; i < LINE_NR; i++) {
            for (int j = 0; j < COL_NR; j++) {
                field[i][j] = '_';
            }
        }
        winList = new ArrayList<>();
        endGame = false;
    }

    public ConnectFour() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        // panel for game cells
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(LINE_NR, COL_NR));
        // panel for button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 3, 1));
        // set layout for frame
        setLayout(new BorderLayout());
        setTitle("Connect Four");
        // populate with buttons
        populateWithButtons(panel);
        // add reset button to panel
        JButton buttonReset = new JButton("Reset");
        buttonReset.setName("ButtonReset");
        buttonReset.setEnabled(true);
        buttonReset.addActionListener(e -> {
            buttonReset(panel);
        });
        buttonPanel.add(buttonReset);
        // button added to the right of the panel
        add(buttonPanel, BorderLayout.SOUTH);
        // set visible frame
        setVisible(true);
    }

    // reset button
    public void buttonReset(JPanel panel) {
        JButton button;
        int counterLine = LINE_NR;
        for (int i = 0; i < LINE_NR; i++) {
            for (int j = 0; j < COL_NR; j++) {
                String str = "Button" + ButtonLetter(j + 1) + (i + 1);
                button = map.get(str);
                button.setBackground(Color.GRAY);
                button.setText(" ");
            }
        }
        winList.clear();
        endGame=false;
        currentChar = 'X';
        for (int i = 0; i < LINE_NR; i++) {
            for (int j = 0; j < COL_NR; j++) {
                field[i][j] = '_';
            }
        }
        panel.repaint();
    }

    // buttons to field
    void populateWithButtons(JPanel panel) {
        int counterLine = LINE_NR;
        for (int i = 0; i < LINE_NR; i++) {
            for (int j = 0; j < COL_NR; j++) {
                String celName = ButtonLetter(j + 1) + "" + counterLine;
                JButton button = new JButton(" ");
                button.setFocusPainted(false);
                button.setName("Button"+ celName);
                // set color for button
                button.setBackground(Color.gray);
                panel.add(button);
                button.addActionListener(this);
                button.setActionCommand("Button"+ celName);
                map.put("Button"+ celName, button);
            }
            counterLine--;
        }
        // add panel with cells to center of the frame
        add(panel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (endGame) {
            return;
        }
        String command = ((JButton) e.getSource()).getActionCommand();
        Font font = new Font("Curier", Font.BOLD, 16);
        String out = checkRule(command.substring(6,8));
        JButton button = map.get(out);
        if (button == null) {
            return;
        }
        button.setText(String.valueOf(currentChar));
        button.setFont(font);
        if (currentChar == 'X') {
            currentChar = 'O';
        } else {
            currentChar = 'X';
        }
    }

    // check the rule of the game.
    public String checkRule(String s) {
        String out = null;
        JButton winner;
        int col = LetterButton(s.charAt(0)) - 1;
        for (int i = 0; i < LINE_NR; i++) {
            if (field[i][col] == '_') {
                field[i][col] = currentChar;
                out = "Button" + ButtonLetter(col + 1) + (i + 1);
                boolean win = checkWin();
                if (win) {
                    for (Integer[] w : winList) {
                        int l = w[0];
                        int c = w[1];
                        String str = "Button" + ButtonLetter(c + 1) + (l + 1);
                        winner = map.get(str);
                        winner.setBackground(Color.MAGENTA);
                        endGame = true;
                    }
                    break;
                }
                break;
            }
        }
        return out;
    }

    public boolean checkWin() {
        List<Integer[]> aList = new ArrayList<>();
        boolean win = false;

        // parse the field lines
        for (int i = 0; i < LINE_NR; i++) {
            int nrLines = 0;
            aList.clear();
            for (int j = 0; j < COL_NR; j++) {
                if (field[i][j] == currentChar) {
                    aList.add(new Integer[]{i,j});
                    nrLines++;
                } else {
                    nrLines = 0;
                }
                if(nrLines == 4) {
                    break;
                }
            }
            if (nrLines == 4) {
                winList = aList;
                win = true;
                break;
            }
        }

        if (!win) {
            // parse the columns
            for (int i = 0; i < COL_NR; i++) {
                int nrLines = 0;
                aList.clear();
                for (int j = 0; j < LINE_NR; j++) {
                    if (field[j][i] == currentChar) {
                        aList.add(new Integer[]{j, i});
                        nrLines++;
                    } else {
                        nrLines = 0;
                    }
                    if (nrLines == 4) {
                        break;
                    }
                }
                if (nrLines == 4) {
                    winList = aList;
                    win = true;
                    break;
                }
            }
        }

        if (!win) {
            // parse the diagonals
            for (int i = 0; i < 4; i++) {
                win = parseMainDiagonal(aList, 0, i, currentChar);
                if (win) {
                    break;
                }
                win = parseSecondDiagonal(aList, 0, i, currentChar);
                if (win) {
                    break;
                }
            }
        }
        // continuing with the diagonals
        if (!win)
            win = parseMainDiagonal(aList, 1, 0, currentChar);
        if (!win)
            win = parseMainDiagonal(aList, 2, 0, currentChar);
        if (!win)
            win = parseSecondDiagonal(aList, 1, 0, currentChar);
        if (!win)
            win = parseSecondDiagonal(aList, 2, 0, currentChar);
        return win;
    }

    boolean parseSecondDiagonal(List<Integer[]> aList, int n, int m, char currentChar) {
        boolean win = false;
        int nrLines = 0;
        try {
            int i = 0;
            for (int j = COL_NR - 1; j >= 0; j--) {
                if (field[i + n][j - m] == currentChar) {
                    aList.add(new Integer[]{i + n, j - m});
                    nrLines++;
                } else {
                    aList.clear();
                    nrLines = 0;
                }
                if (nrLines == 4) {
                    winList = aList;
                    win = true;
                    break;
                }
                i++;
            }
        } catch (Exception ignored) {
        }
        return win;
    }

    boolean parseMainDiagonal(List<Integer[]> aList, int n, int m, char currentChar) {
        boolean win = false;
        int nrLines = 0;
        try {
            for (int j = 0; j < COL_NR - 1; j++) {
                if (field[j + n][j + m] == currentChar) {
                    aList.add(new Integer[]{j + n, j + m});
                    nrLines++;
                } else {
                    aList.clear();
                    nrLines = 0;
                }
                if (nrLines == 4) {
                    winList = aList;
                    win = true;
                    break;
                }
            }
        } catch (Exception ignored) {

        }
        return win;
    }

    // return letter based on integer
    static char ButtonLetter(int nr) {
        return switch (nr) {
            case 1 -> 'A';
            case 2 -> 'B';
            case 3 -> 'C';
            case 4 -> 'D';
            case 5 -> 'E';
            case 6 -> 'F';
            case 7 -> 'G';
            default -> 0;
        };
    }

    // return integer based on letter
    static char LetterButton(char c) {
        return switch (c) {
            case 'A' -> 1;
            case 'B' -> 2;
            case 'C' -> 3;
            case 'D' -> 4;
            case 'E' -> 5;
            case 'F' -> 6;
            case 'G' -> 7;
            default -> 0;
        };
    }

    // return what the cell contains
    public char getChar(int i, int j) {
        return field[i][j];
    }

}