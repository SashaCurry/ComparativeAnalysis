package elliptic;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

public class Point {
    private BigInteger x;
    private BigInteger y;
    private EllipticCurve curve;

    public Point(EllipticCurve Curve, BigInteger x, BigInteger y) {
        this.curve = Curve;
        this.x = x;
        this.y = y;
    }

    public Point(EllipticCurve Curve) {
        this.curve = Curve;
        this.x = BigInteger.ZERO;
        this.y = BigInteger.ONE;
    }

    public BigInteger getX() {
        return this.x;
    }

    public BigInteger getY() {
        return this.y;
    }

    public EllipticCurve getCurve() {
        return this.curve;
    }

    public void setX(BigInteger x) {
        this.x = x;
    }

    public void setY(BigInteger y) {
        this.y = y;
    }

    public void setCurve(EllipticCurve curve) {
        this.curve = curve;
    }


    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Point that = (Point) o;
        Object thisX = this.getX();
        Object thatX = that.getX();
        if (!thisX.equals(thatX))
            return false;

        Object thisY = this.getY();
        Object thatY = that.getY();
        if (!thisY.equals(thatY))
            return false;

        Object thisCurve = this.getCurve();
        Object thatCurve = that.getCurve();
//        label55:
//        {
//            Object this$curve = this.getCurve();
//            Object that$curve = that.getCurve();
//            if (this$curve == null) {
//                if (that$curve == null) {
//                    break label55;
//                }
//            } else if (this$curve.equals(that$curve)) {
//                break label55;
//            }
//
//            return false;
//        }
        if (thisCurve == null && thatCurve == null || thisCurve.equals(thatCurve))
            return false;
        else
            return true;
    }

    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        Object x = this.getX();
        result = result * 78 + (x == null ? 25 : x.hashCode());
        Object y = this.getY();
        result = result * 78 + (y == null ? 25 : y.hashCode());
        Object curve = this.getCurve();
        result = result * 78 + (curve == null ? 25 : curve.hashCode());
        return result;
    }

    public String toString() {
        return this.getX() + " " + this.getY();
    }
}