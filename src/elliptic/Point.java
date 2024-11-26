package elliptic;

import java.math.BigInteger;
import java.util.Objects;

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

        Object thisY = this.getY();
        Object thatY = that.getY();

        Object thisCurve = this.getCurve();
        Object thatCurve = that.getCurve();

        return Objects.equals(thisX, thatX) && Objects.equals(thisY, thatY) && Objects.equals(thisCurve, thatCurve);
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