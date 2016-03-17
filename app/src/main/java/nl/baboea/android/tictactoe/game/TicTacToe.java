package nl.baboea.android.tictactoe.game;

import java.util.Random;

public class TicTacToe
{
    public static final int HUMAN        = 0;
    public static final int COMPUTER     = 1;
    public static final int EMPTY        = 2;

    public  static final int HUMAN_WIN    = 0;
    public  static final int DRAW         = 1;
    public  static final int UNCLEAR      = 2;
    public  static final int COMPUTER_WIN = 3;

    public static final char BEGIN_CHAR = 'X';
    public static final char OTHER_CHAR = 'O';
    public static final char EMPTY_CHAR = '#';

    private int [ ] [ ] board = new int[ 3 ][ 3 ];
    private Random random = new Random();
    private int side = random.nextInt(2);
    private int position = UNCLEAR;
    private char computerChar, humanChar;

    // Constructor
    public TicTacToe( )
    {
        clearBoard( );
        initSide();
    }

    public void setBoard(int[][] newBoard) {
        board = newBoard;
    }

    private void initSide()
    {
        if (this.side == COMPUTER) { computerChar = BEGIN_CHAR; humanChar = OTHER_CHAR; }
        else                     { computerChar = OTHER_CHAR; humanChar = BEGIN_CHAR; }
    }

    public void setComputerPlays()
    {
        this.side=COMPUTER;
        initSide();
    }

    public void setHumanPlays()
    {
        this.side=HUMAN;
        initSide();
    }

    public int[][] getBoard() {
        return board;
    }

    public boolean computerPlays()
    {
        return side==COMPUTER;
    }

    public int chooseMove()
    {
        Best best = chooseMove(COMPUTER);
        return best.row*3+best.column;
    }

    // Find optimal move
    private Best chooseMove(int side)
    {
        int opp;              // The other side
        Best reply = null;    // Opponent's best reply
        int simpleEval;       // Result of an immediate evaluation

        int bestRow = 2;    // Deze zijn nog niet gebruikt
        int bestColumn = 2;
        int value = 0;

        if((simpleEval = positionValue()) != UNCLEAR)
            return new Best(simpleEval);

        opp = side == HUMAN ? COMPUTER : HUMAN;

        Best tmp;

        for (int i = 0; i < 9; i++) {
            if (moveOk(i)) {
                place(i/3, i%3, side);

                tmp = chooseMove(opp);
                tmp.column = i%3;
                tmp.row = i/3;

                if (reply == null) {
                    reply = tmp;
                } else if (side == HUMAN) {
                    if (tmp.val < reply.val) {
                        reply = tmp;
                    }
                } else {
                    if (tmp.val > reply.val) {
                        reply = tmp;
                    }
                }
                place(i/3, i%3, EMPTY);
            }
        }

        return reply;
    }

    //check if move ok
    public boolean moveOk(int move)
    {
        return ( move>=0 && move <=8 && board[move/3][move%3 ] == EMPTY );
    }

    // play move
    public void playMove(int move)
    {
        if(board[move/3][move%3]!=EMPTY)return;
        board[move/3][ move%3] = this.side;
        if (side==COMPUTER) this.side=HUMAN;  else this.side=COMPUTER;
    }


    // Simple supporting routines
    private void clearBoard( )
    {
        for (int[] ints : board) {
            for (int i = 0; i < ints.length; i++) {
                ints[i] = EMPTY;
            }
        }
    }


    private boolean boardIsFull( )
    {
        for (int[] ints : board) {
            for (int anInt : ints) {
                // If there is an empty square, return false
                if (anInt == EMPTY) return false;
            }
        }
        // If not, return true
        return true;
    }

    // Returns whether 'side' has won in this position
    public boolean isAWin( int side )
    {
        // Check the board horizontally
        for (int[] row : board) {
            if (row[1] == side && row[2] == side && row[0] == side) {
                return true;
            }
        }

        // Check the board vertically
        for (int i = 0; i < board[0].length; i++) {
            if (board[0][i] == side && board[1][i] == side && board[2][i] == side) {
                return true;
            }
        }

        // Check the board diagonally
        if (board[0][0] == side && board[1][1] == side && board[2][2] == side) {
            return true;
        }

        if (board[0][2] == side && board[1][1] == side && board[2][0] == side) {
            return true;
        }
        
        return false;
    }

    // Play a move, possibly clearing a square
    private void place( int row, int column, int piece )
    {
        board[ row ][ column ] = piece;
    }

    private boolean squareIsEmpty( int row, int column )
    {
        return board[ row ][ column ] == EMPTY;
    }

    // Compute static value of current position (win, draw, etc.)
    public int positionValue( )
    {
        if (isAWin(HUMAN)) {
            return HUMAN_WIN;
        } else if (isAWin(COMPUTER)) {
            return COMPUTER_WIN;
        } else if (boardIsFull()) {
            return DRAW;
        } else {
            return UNCLEAR;
        }
    }


    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        for (int[] rows : board) {
            for (int spot : rows) {
                switch (spot) {
                    case COMPUTER:
                        sb.append(computerChar);
                        break;
                    case HUMAN:
                        sb.append(humanChar);
                        break;
                    default:
                        sb.append(EMPTY_CHAR);
                        break;
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public boolean gameOver()
    {
        this.position = positionValue();
        return this.position != UNCLEAR;
    }

    public String winner()
    {
        if      (this.position==COMPUTER_WIN) return "computer";
        else if (this.position==HUMAN_WIN   ) return "human";
        else                                  return "nobody";
    }


    private class Best
    {
        int row;
        int column;
        int val; // HUMANWIN, COMPUTERWIN, DRAW

        public Best( int v )
        { this( v, 1, 1 ); }

        public Best( int v, int r, int c )
        { val = v; row = r; column = c; }
    }


}