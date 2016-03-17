package nl.baboea.android.tictactoe.math;

/**
 * Created by Laurens on 14-9-2015.
 */
public class Vec3 {
    public static final float DEFAULT_EPSILON = 0.1f;
    private float x = 0, y = 0, z = 0;

    public Vec3() {
    }

    public Vec3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3(Vec3 o){
        this(o.x,o.y,o.z);
    }

    public void incrementX(float x) {
        this.x += x;
    }

    public void incrementY(float y) {
        this.y += y;
    }

    public void incrementZ(float z) {
        this.z += z;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public Vec3 clone(){
        return new Vec3(x,y,z);
    }

    public Vec3 minus(Vec3 other){
        return new Vec3(this.x-other.x, this.y-other.y,this.z-other.z);
    }

    public float length(){
        return (float)Math.sqrt(x*x+y*y+z*z);
    }

    public boolean equals(Object other, float epsilon){
        if(! (other instanceof Vec3))return false;
        Vec3 otherV = (Vec3) other;
        boolean x = equals(otherV.getX(),getX(),epsilon);
        boolean y = equals(otherV.getY(),getY(),epsilon);
        boolean z = equals(otherV.getZ(),getZ(),epsilon);
        return x&&y&&z;
    }

    @Override
    public boolean equals(Object o) {
        return equals(o, DEFAULT_EPSILON);
    }


    @Override
    public String toString() {
        return "[Vec3 x = " + x + ", y = " + y + ", z = " +z +"]";
    }

    private boolean equals(float one, float two, float epsilon){
        return Math.abs(one-two)<=epsilon;
    }
}
