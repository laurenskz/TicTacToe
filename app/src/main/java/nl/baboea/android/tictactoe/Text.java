package nl.baboea.android.tictactoe;

import android.util.Log;

import java.util.Collections;
import java.util.HashMap;

import nl.baboea.android.tictactoe.math.Vec3;

/**
 * Created by Laurens on 16-12-2015.
 */
public class Text {

    public static final int FONT_WIDTH = 8;
    public static final int FONT_HEIGHT = 8;
    private static float SPACE_WIDTH = 0.46f;
    private static final float WIDTH = 0.2f,HEIGHT = 0.2f;
    public static final String TAG = "Text";
    private static char[] alphabetWithNum = "abcdefghijklmnopqrstuvwxyz0123456789!?+-=:.,".toCharArray();
    private static HashMap<Character,AbstractShape> characters = getCharacters();//This will store our letters etc.
    public static int[] l_size = {36,29,30,34,25,25,34,33,//These are the widths of our characters
            11,20,31,24,48,35,39,29,
            42,31,27,31,34,35,46,35,
            31,27,30,26,28,26,31,28,
            28,28,29,29,14,24,30,18,
            26,14,14,14,25,28,31,0,
            0,38,39,12,36,34,0,0,
            0,38,0,0,0,0,0,0};

    private static HashMap<Character,Float> characterWidths = getCharacterWidths();//This will store our letters etc.

    private static HashMap<Character,Float> getCharacterWidths(){
        HashMap<Character,Float> toRet = new HashMap<Character,Float>();
        for(int i = 0 ; i < alphabetWithNum.length ; i++){
            toRet.put(alphabetWithNum[i],((float)l_size[i])/64);
        }
        return toRet;
    }
    private static HashMap<Character,AbstractShape> getCharacters(){

        HashMap<Character, AbstractShape> toRet = new HashMap<>();
        Texture t = new Texture(R.drawable.font);
        Texture secondTexture = new Texture(R.drawable.blue);
        float widthPerLetter = 1f/FONT_WIDTH;
        float heightPerLetter = 1f/FONT_HEIGHT;
        int i = 0;
        xLoop:
        for(int y = 0 ; y < FONT_HEIGHT ; y++){
            for(int x = 0 ; x < FONT_WIDTH ; x++){
                if(i>=alphabetWithNum.length)break xLoop;
                AbstractShape s = new AbstractShape(Square.getVertices(WIDTH,HEIGHT),textureCoordinates(x * widthPerLetter,(x + 1) * widthPerLetter,(y+1)*heightPerLetter,y*heightPerLetter),Utils.indices,ShaderProgram.multiTextured,t);
                s.setTexture2(secondTexture);
                s.setUseVP(false);
                toRet.put(alphabetWithNum[i],s);
                i++;
            }
        }
        return toRet;
    }

    private static final float[] textureCoordinates(float xMin,float xMax,float yMin,float yMax){
        return new float[]{
                xMin, yMin,
                xMin, yMax,
                xMax, yMax,
                xMax, yMin
        };
    };

    private String text;
    private Model model;

    public Text(String text){
        this.text = text;
        model = new Model();
    }

    public Model getModel() {
        return model;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void draw(){
        Vec3 start = new Vec3(model.getPosition());//Clone it so we have to original position still
        for(int i = 0 ; i < text.length() ; i++){
            char c = text.charAt(i);
            c = Character.toLowerCase(c);//All chars have to be lowercase because we don't define a difference between the two.
            AbstractShape shape = characters.get(c);
            if(shape!=null){
                float procentWidth = characterWidths.get(c);
                shape.draw(model);
                model.getPosition().incrementX(WIDTH*procentWidth*model.getScale().getX());
            }else{
                if(c==' ')//Space is special :D
                    model.getPosition().incrementX(WIDTH*SPACE_WIDTH*model.getScale().getX());
            }
        }
        model.setPosition(start);
    }
}
