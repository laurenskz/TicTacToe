package nl.baboea.android.tictactoe;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import nl.baboea.android.tictactoe.math.*;

import android.opengl.GLES20;
import android.util.Log;


/**
 * This class is not meant to be seen by anyone, please don't judge me.
 * Created by Laurens on 13-9-2015.
 */
public class AbstractShape {


    private static final float BOUND_DRAW_WIDTH = 0.01f;
    public static final int NO_TEXTURE = -988235;
    private FloatBuffer vertexData;
    private FloatBuffer textureCoordinates;
    private ShortBuffer indexData;
    private ShaderProgram shaderProgram;
    private boolean drawable = true;
    private int vertexPositionID;
    private int textureCoordinateID;
    private int indexBufferSize;
    private int indexBufferID;
    private Mat4f modelMatrix;
    private static final int COORDINATES_PER_VERTEX = 3;
    private static final String vPostion = "vertexPosition";
    private static final String texturePosition = "textureCoordinate";
    private Bounds bounds;
    private int samplerLoc,samplerLoc2;
    private Texture texture, texture2;
    private boolean useVP = true;



    public AbstractShape(float[] vertices, float[] textureCoordinates, short[] indices, ShaderProgram shaderProgram, Texture texture){
        init(textureCoordinates, indices, shaderProgram, vertices);
        this.texture = texture;
    }

    private void init(float[] textureCoordinates, short[] indices, ShaderProgram shaderProgram, float[] vertices) {
        setBounds(vertices);
        vertexData = loadFloatArray(vertices);
        this.textureCoordinates = loadFloatArray(textureCoordinates);
        indexData = loadShortArray(indices);
        this.shaderProgram = shaderProgram;
        indexBufferSize = indices.length;
        setUpShaderIDs();
    }

    private void setBounds(float[] textureCoordinates){
        this.bounds = new Bounds();
        for(int i = 0 ; i < textureCoordinates.length ; i+=3){
            float x = textureCoordinates[i];
            float y = textureCoordinates[i+1];
            if(x<this.bounds.xMin)this.bounds.xMin = x;
            if(x>this.bounds.xMax)this.bounds.xMax = x;
            if(y<this.bounds.yMin)this.bounds.yMin = y;
            if(y>this.bounds.yMax)this.bounds.yMax = y;
        }
    }

    private void setUpShaderIDs(){
        vertexPositionID = GLES20.glGetAttribLocation(shaderProgram.getProgramID(), vPostion);
        if(vertexPositionID==-1)drawable=false;
        textureCoordinateID = GLES20.glGetAttribLocation(shaderProgram.getProgramID(),texturePosition);
        //if(textureCoordinateID == -1)drawable = false;
        int vbo[] = new int[1];
        samplerLoc = GLES20.glGetUniformLocation(shaderProgram.getProgramID(), "texture");
        samplerLoc2 = GLES20.glGetUniformLocation(shaderProgram.getProgramID(), "texture2");
        GLES20.glGenBuffers(1, vbo, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vbo[0]);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBufferSize * 2, indexData, GLES20.GL_STATIC_DRAW);
        indexBufferID = vbo[0];
    }


    FloatBuffer loadFloatArray(float[] array){
        ByteBuffer bb = ByteBuffer.allocateDirect(array.length * 4);//Each float consists of 4 bytes
        bb.order(ByteOrder.nativeOrder()); //Set the endianness right
        FloatBuffer fBuffer = bb.asFloatBuffer(); //Convert the buffer to a float buffer
        fBuffer.put(array); //Put the array in the float buffer
        fBuffer.position(0); // Reset the position
        return fBuffer;//Return the float buffer we put the result in
    }

    ShortBuffer loadShortArray(short[] array){
        ByteBuffer bb = ByteBuffer.allocateDirect(array.length * 2);//Each short consists of 4 bytes
        bb.order(ByteOrder.nativeOrder()); //Set the endianness right
        ShortBuffer sBuffer = bb.asShortBuffer(); //Convert the buffer to a short buffer
        sBuffer.put(array); //Put the array in the short buffer
        sBuffer.position(0); // Reset the position
        return sBuffer;//Return the short buffer we put the result in
    }

    private void generateModelMatrix(Model model){
        modelMatrix = null;//Can we help the garbage collector?
        modelMatrix = Mat4f.rotatation(model.getRotation());//First we rotate
        modelMatrix.scale(model.getScale()); //Then we scale
        modelMatrix.translate(model.getPosition());//Then translate. This order is taken for appropriate results
    }

    private void loadMVP(Model model){
        generateModelMatrix(model);
        if(useVP){
            modelMatrix.multiply(Camera.getViewMatrix());
            modelMatrix.multiply(Mat4f.getProjectionMatrix());
        }
        shaderProgram.load("mvpMatrix", modelMatrix);
    }


    public AbstractShape getBounds(Model model) {
        Bounds bounds = this.bounds.scaled(model);
        bounds.translate(model);
        return new AbstractShape(bounds.toFloats(),new float[0],bounds.toIndici(),ShaderProgram.noTexture,null);
    }

    /**
     * This method draws the abstractshape, all transformations are applied as well as the view and projection matrix
     */
    public void draw(Model model){
        if(!drawable)return;//If the shader is not compatible with the fields we expect it to have there is no point in doing all the draw work
        loadMVP(model);//Set the matrix so that it can be used in the shader
        shaderProgram.use();//Bind the shaderProgram, now our shader program is in use
        prepareDataForDrawing(); //Prepare all the data we need and set it up so that our shader can use it
        if(texture!=null)prepareTexture();//If texture == null this will cause some errors.
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexBufferSize, GLES20.GL_UNSIGNED_SHORT, 0);//Draw the data
    }

    private void prepareDataForDrawing() {
        bindDataToShader(vertexPositionID, COORDINATES_PER_VERTEX, COORDINATES_PER_VERTEX * 4, vertexData);
        bindDataToShader(textureCoordinateID, 2, 0, textureCoordinates);//Link all data to the shader
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBufferID);//Bind the index buffer
    }

    private void prepareTexture() {
        GLES20.glUniform1i(samplerLoc, 0);
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.getId());
        if(texture2!=null){
            GLES20.glUniform1i(samplerLoc2, 1);
            //GLES20.glEnable(GLES20.GL_TEXTURE_2D);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture2.getId());
        }
    }

    public void setTexture2(Texture texture2) {
        this.texture2 = texture2;
    }

    private void bindDataToShader(int id, int coordinatesPerVertex, int stride, FloatBuffer vertexData) {
        GLES20.glEnableVertexAttribArray(id);
        GLES20.glVertexAttribPointer(id, coordinatesPerVertex, GLES20.GL_FLOAT, false, stride, vertexData);
    }

    class Bounds{
        float xMin,xMax,yMin,yMax;
        public Bounds(){
            xMin = Float.MAX_VALUE;
            xMax = Float.MIN_VALUE;
            yMin = Float.MAX_VALUE;
            yMax = Float.MIN_VALUE;
        }
        public Bounds(float xMin, float xMax, float yMin, float yMax) {
            this.xMin = xMin;
            this.xMax = xMax;
            this.yMin = yMin;
            this.yMax = yMax;
        }

        Bounds scaled(Model model){
            Vec3 scale = model.getScale();
            return new Bounds(xMin*scale.getX(),xMax*scale.getX(),yMin*scale.getY(),yMax*scale.getY());
        }

        void translate(Model model){
            Vec3 position = model.getPosition();
            xMin+=position.getX();
            xMax+=position.getX();
            yMin+=position.getY();
            yMax+=position.getY();
        }

        float getWidth(){
            return xMax - xMin;
        }

        float getHeight(){
            return yMax - yMin;
        }

        void print(String from){
            Log.d("Shape",from + ": "+  "xMin = " + xMin+"; xMax = " + xMax + "; yMin = " + yMin + "; yMax = " + yMax);
        }
        /**
         * This method can be used for displaying the bounds.
         * @return
         */
        float[] toFloats(){
            float[] floats = new float[] {
                    xMin,yMax,-0.1f,//Top left (0)
                    xMin,yMin,-0.1f,//Bottom left (1)
                    xMin+BOUND_DRAW_WIDTH,yMin,-0.1f,//Bottom left little to right (2)
                    xMin+BOUND_DRAW_WIDTH,yMax,-0.1f,//Top left little to the right (3)
                    xMax,yMax,-0.1f,//Top right (4)
                    xMax,yMin,-0.1f,//Bottom right (5)
                    xMax - BOUND_DRAW_WIDTH,yMax,-0.1f,//Top right little to the left (6)
                    xMax - BOUND_DRAW_WIDTH ,yMin,-0.1f,//Bottom right little to the left (7)
                    xMax,yMax - BOUND_DRAW_WIDTH,-0.1f,//Top right a little bit down (8)
                    xMax,yMin + BOUND_DRAW_WIDTH,-0.1f,//Bottom right a little bit up (9)
                    xMin,yMax - BOUND_DRAW_WIDTH,-0.1f,//Top left a little bit down (10)
                    xMin,yMin + BOUND_DRAW_WIDTH,-0.1f,//Bottom left a little bit up (11)



            };
            return floats;
        }

        short[] toIndici(){
            short[] shorts = new short[]{
                0,1,2,//Triangle 1, left pilar half 1
                2,3,0,
                11,1,5,
                5,9,11,
                6,7,5,
                5,4,6,
                0,10,8,
                8,4,0

            };
            return shorts;
        }

        boolean collidesWith(Bounds other){
            if(xMin>other.xMax)return false;//If one of these conditions is true the boxes cant overlap. Else they will
            if(yMin>other.yMax)return false;
            if(other.yMin>yMax)return false;
            if(other.xMin>xMax)return false;
            return true;
        }
    }

    public void setUseVP(boolean useVP) {
        this.useVP = useVP;
    }

    public boolean collidesWith(AbstractShape other, Model ownModel, Model otherModel){
        Bounds thisScaledBounds = this.bounds.scaled(ownModel);
        thisScaledBounds.translate(ownModel);
        Bounds otherScaledBounds = other.bounds.scaled(otherModel);
        otherScaledBounds.translate(otherModel);
        //Now both of their bounds are in real world position. If the boxes don't collide there is no collision
        if(!thisScaledBounds.collidesWith(otherScaledBounds))return false;
        //If we reach this part of the code we know that there is collision between the boxes. Now we have to use
        //The collision meshes to determine collision of the actual textures
        float ownWidthPerPixel = thisScaledBounds.getWidth()/this.texture.getWidth();//Calculate the width per pixel of the shape
        float ownHeightPerPixel = thisScaledBounds.getHeight()/this.texture.getHeight();
        float otherWidthPerPixel = otherScaledBounds.getWidth()/other.texture.getWidth();//Calculate the width per pixel of the shape
        float otherHeightPerPixel = otherScaledBounds.getHeight()/other.texture.getHeight();
        short situation = 0;//1 is other.yMin is in between 2 is other.yMax is in between and 3 is they are both in between.
        if(otherScaledBounds.yMin<=thisScaledBounds.yMax&&otherScaledBounds.yMin>=thisScaledBounds.yMin)situation = 1;
        if(otherScaledBounds.yMax<=thisScaledBounds.yMax&&otherScaledBounds.yMax>=thisScaledBounds.yMin)situation += 2;
        //Situation can't be zero. Because then there won't be any collision detected and this method would have returned
        boolean useYMax = situation!=1;
        int direction = useYMax? 1 : -1;//This is the direction in which we have to go with the rows. From the start row (e.g 7) to 8 or 6.
        float startHeight = useYMax?otherScaledBounds.yMax : otherScaledBounds.yMin;
        int ownStartRow = (int)((thisScaledBounds.yMax - startHeight)/ownHeightPerPixel);//We have to be own row further, fortunately indici start with 0
        int endRow = 0;
        if(situation==1){//Then the bottom of the other square is in our square and the top if above. The endrow has to be zero (our top row)
            endRow = 0;//Even though it has been set to zero this still seems like good practice.
        }else if (situation==2){//Then the top of the other square is inside our square but its bottom is beneath our square so the endrow will be our last row
            endRow = this.texture.getHeight()-1;//Included
        }else if(situation==3){//The top of the square is within our square and the bottom row as well. We want to go from the bottom to the top (other.ymax to other.
            endRow = (int)((thisScaledBounds.yMax - otherScaledBounds.yMin)/ownHeightPerPixel);
        }
        CollisionMesh ownCollisionMesh = this.texture.getCollisionMesh();
        CollisionMesh otherCollisionMesh = other.texture.getCollisionMesh();
        int firstAvailableRow = ownCollisionMesh.getPoints()[0].y;
        int lastAvailableRow = ownCollisionMesh.getPoints()[ownCollisionMesh.getPoints().length-1].y;
        //Log.d("Shape", "collidesWith: startRow:  "+ ownStartRow + " end row " + endRow + " direction = " + direction); //DEBUG
        //Log.d("Shape", "First available row = " + firstAvailableRow);
        if(!useYMax&&ownStartRow<firstAvailableRow)return false;
        if(useYMax&&ownStartRow>lastAvailableRow)return false;//No use in trying to see if things overlap in both these conditions (other thing is only in the transparant top or bottom layer
        int otherFirstRow = otherCollisionMesh.getPoints()[0].y;
        if(useYMax){
            for(int row = ownStartRow ; row<=endRow;row+=direction){
                boolean x = pixelsCollide(thisScaledBounds, otherScaledBounds, ownWidthPerPixel, ownHeightPerPixel, otherWidthPerPixel, otherHeightPerPixel, ownCollisionMesh, otherCollisionMesh, otherFirstRow, row);
                if (x) return true;
            }
        }else{
            for(int row = ownStartRow ; row>=endRow;row+=direction){
                boolean x = pixelsCollide(thisScaledBounds, otherScaledBounds, ownWidthPerPixel, ownHeightPerPixel, otherWidthPerPixel, otherHeightPerPixel, ownCollisionMesh, otherCollisionMesh, otherFirstRow, row);
                if (x) return true;
            }
        }

        return false;
    }

    private boolean pixelsCollide(Bounds thisScaledBounds, Bounds otherScaledBounds, float ownWidthPerPixel, float ownHeightPerPixel, float otherWidthPerPixel, float otherHeightPerPixel, CollisionMesh ownCollisionMesh, CollisionMesh otherCollisionMesh, int otherFirstRow, int row) {
        float rowMaxHeight = thisScaledBounds.yMax - row * ownHeightPerPixel;//Since rows start at zero this is right
        float rowMinHeight = rowMaxHeight-ownHeightPerPixel;//THis is the bottom of the row
        float diffOtherTopToRowTop = otherScaledBounds.yMax-rowMaxHeight;
        float diffOtherTopToRowBottom = otherScaledBounds.yMax-rowMinHeight;
        if(diffOtherTopToRowBottom<=0)return false;
        int otherStartRow = (int)(diffOtherTopToRowTop/otherHeightPerPixel);
        if(otherStartRow<0)otherStartRow = 0;
        int otherEndRow = (int) (diffOtherTopToRowBottom/otherHeightPerPixel);
        otherEndRow++;//Has to go all over our own row
        CollisionMesh.Point ownPoint = ownCollisionMesh.getPointWithY(row);
        if(ownPoint==null)return false;
        float ownLeftBound = thisScaledBounds.xMin + ownPoint.xMin*ownWidthPerPixel;
        float ownRightBound = thisScaledBounds.xMin + ownPoint.xMax*ownWidthPerPixel;
        if(otherFirstRow>otherEndRow)return false;//The other first available row is not what we are looking for so on to next iteration
        for(int otherRow = otherStartRow; otherRow<otherEndRow ; otherRow++){
            CollisionMesh.Point otherPoint = otherCollisionMesh.getPointWithY(otherRow);
            if(otherPoint==null)continue;
            float otherLeftBound = otherScaledBounds.xMin + otherPoint.xMin*otherWidthPerPixel;
            float otherRightBound = otherScaledBounds.xMin + otherPoint.xMax*otherWidthPerPixel;
            if(otherLeftBound<ownRightBound&&otherLeftBound>ownLeftBound)return true;//The pixel is inbetween our own and so they overlap
            if(otherRightBound<ownRightBound&&otherRightBound>ownLeftBound)return true;
            if(ownLeftBound<otherRightBound&&ownLeftBound>otherLeftBound)return true;//The pixel is inbetween our own and so they overlap
            if(ownRightBound<otherRightBound&&ownRightBound>otherLeftBound)return true;

        }
        return false;
    }


    public float getWidth(){
        return this.bounds.getWidth();
    }

    public float getHeight(){
        return this.bounds.getHeight();
    }
}
