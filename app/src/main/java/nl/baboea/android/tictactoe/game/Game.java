package nl.baboea.android.tictactoe.game;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;


import nl.baboea.android.tictactoe.Constants;
import nl.baboea.android.tictactoe.Input;
import nl.baboea.android.tictactoe.Model;
import nl.baboea.android.tictactoe.R;
import nl.baboea.android.tictactoe.Text;
import nl.baboea.android.tictactoe.Texture;
import nl.baboea.android.tictactoe.TexturedSquare;
import nl.baboea.android.tictactoe.math.Vec3;

/**
 * Created by Laurens op 't Zandt & Jules van Dongen on 18-9-2015.
 */
public class Game {


    private final Activity context;//For toast messages
    private TexturedSquare x,o,board;//The three textures needed for this game, X the O and the board (Located in drawable folder)
    private Model boardModel;//The model of the board, for scale, position and rotation
    private Model[] models = new Model[9];//The models containing the positions for the X's and O's
    private Vec3 oScale, xScale;//Because the texture size vary the x and o need a different scale.
    private TicTacToe game;//The game we will play


    /**
     * Constructor
     * @param context Needed for Toast messages
     */
    public Game(Activity context) {
        this.context = context;
        initializeGame();
    }

    /**
     * This method initializes the game, all textures are loaded and the scales and positions of the models are set.
     */
    private void initializeGame(){
        x =  new TexturedSquare(new Texture(R.drawable.tictacx));
        o =  new TexturedSquare(new Texture(R.drawable.tictaco));
        board =  new TexturedSquare(new Texture(R.drawable.tictacboard));
        boardModel = new Model();
        boardModel.setScale(new Vec3(2f,2f,1f));
        setSquarePositions();
        xScale = new Vec3(0.7f,0.7f,1f);
        oScale = new Vec3(1.2f,1.2f,1f);
        game = new TicTacToe();
    }

    /**
     * Sets the positions of the 9 tic tac toe squares.
     */
    private void setSquarePositions() {
        for (int i = 0; i < models.length; i++) {
            models[i] = new Model();
            int row = i/3;
            int column = i%3;
            float x = -0.6f,y=-0.6f;
            x+=column*0.6f;
            y+=row*0.6f;
            models[i].setPosition(new Vec3(x, y, 1f));
        }
    }


    /**
     * Create a toast message, needed because of threading issues with opengl
     * @param message
     * @param length
     */
    private void toast(final String message, final int length){
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, length).show();
            }
        });
    }


    /**
     * This method draws our game. A model is used to draw a texture to give it a position and scale and rotation
     */
    public void draw() {
        board.draw(boardModel);
        int[][] board = game.getBoard();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if(board[i][j]==TicTacToe.HUMAN){
                    models[(3*i)+j].setScale(oScale);
                    o.draw(models[(3*i)+j]);
                }
                else if(board[i][j]==TicTacToe.COMPUTER){
                    models[(3*i)+j].setScale(xScale);
                    x.draw(models[(3*i)+j]);
                }
            }
        }
    }

    /**
     * This method updates our game, we perform game logic here
     * @param milliSecondsPassedSinceLastFrame
     */
    public void update(long milliSecondsPassedSinceLastFrame){
        if(game.gameOver()){
            toast(game.winner() + " won the game", Toast.LENGTH_SHORT);
            sleep(2000);
            game = new TicTacToe();
        }
        if(game.computerPlays()){
            game.playMove(game.chooseMove());
            return;
        }
        if(Input.pressed){
            game.playMove(getInputSquare());
            return;
        }

//        Log.d("Game", "update " + game.toString());
    }

    private void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return The square tapped by the player, represented as an integer between 0 and 8.
     */
    private int getInputSquare() {
        float neededX = 0.9375f/3;
        float xRatio = Input.lastX/ Constants.WIDTH;
        xRatio-=0.03125f;
        int column = (int) (xRatio/neededX);
        float screenRatio = Constants.HEIGHT/Constants.WIDTH;
        float yRatio = Input.lastY/Constants.HEIGHT;
        float bothBorders = screenRatio - 0.9375f;
        yRatio -= (bothBorders/2);
        int row = (int)(yRatio/neededX);
        if(row==0){
            row = 2;
        }else if(row == 2){
            row = 0;
        }
        int answer = row*3+column;
        return answer;
    }
}
