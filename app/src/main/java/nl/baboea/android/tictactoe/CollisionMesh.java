package nl.baboea.android.tictactoe;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;

/**
 * Created by Laurens on 15-11-2015.
 */
public class CollisionMesh {

    private Point[] points;
    private int indexLastReturned = 0;

    public CollisionMesh(Bitmap bitmap) {
        this.points = toPoints(bitmap);
    }

    public CollisionMesh(int id) {
        Bitmap bitmap = BitmapFactory.decodeStream(MainActivity.resources.openRawResource(id));
        this.points = toPoints(bitmap);
        bitmap.recycle();
    }

    private Point[] toPoints(Bitmap bitmap){
        ArrayList<Point> temp = new ArrayList<>();
        for(int y = 0 ; y < bitmap.getHeight() ; y++){
            Point point = toPoint(bitmap,y);
            if(point!=null)temp.add(point);
        }
        return temp.toArray(new Point[temp.size()]);
    }

    private Point toPoint(Bitmap bitmap, int y){
        int smallest = Integer.MAX_VALUE, biggest = Integer.MIN_VALUE;
        for(int x = 0 ; x < bitmap.getWidth() ; x++){
            //Here we need to check the alpha value of a pixel.
            int pixel = bitmap.getPixel(x, y);//First we get the pixel
            byte alphaValue = (byte)((pixel >> 24) & 0xFF);//The alpha value is the last 8 byte of the pixel.
            //If the alpha value is 0 (0x00) the pixel needs to be ignored
            if(alphaValue==(byte)0x00)continue;
            if(x<smallest)smallest = x;
            if(x>biggest)biggest = x;
        }
        if(smallest==Integer.MAX_VALUE)return null;
        //If smallest has been set than biggest has been set too (to the same value in the worst case)
        return new Point(y,smallest,biggest);
    }

    public Point getPointWithY(int y){
        for(int i = indexLastReturned ; i < points.length ; i ++){
            if(foundPoint(i,y))return points[i];
        }
        for(int i = 0; i < points.length ; i++){
            if(foundPoint(i,y))return points[i];
        }
        return null;
    }

    private boolean foundPoint(int i, int y){
        if(points[i].y==y){
            indexLastReturned = i;
            return true;
        }
        return false;
    }

    public Point[] getPoints() {
        return points;
    }

    class Point{
        int y, xMin, xMax;

        Point(int y, int xMin, int xMax) {
            this.y = y;
            this.xMin = xMin;
            this.xMax = xMax;
        }
    }
}
