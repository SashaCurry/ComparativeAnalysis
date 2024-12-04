package elliptic;

import java.math.BigInteger;
import java.util.Objects;

public class Point {
    private BigInteger x;
    private BigInteger y;
    private BigInteger z;
    private EllipticCurve curve;
    private String type;

    public Point(EllipticCurve curve, BigInteger x, BigInteger y) {
        this.curve = curve;
        this.x = x;
        this.y = y;
        this.z = BigInteger.ZERO;
        this.type = "affine";
    }

    public Point(EllipticCurve curve) {
        this.curve = curve;
        this.x = BigInteger.ZERO;
        this.y = BigInteger.ONE;
        this.z = BigInteger.ZERO;
        this.type = "affine";
    }

    public Point(EllipticCurve curve, String type) {
        this.curve = curve;
        this.x = BigInteger.ZERO;
        this.y = BigInteger.ONE;
        this.z = BigInteger.ZERO;
        this.type = type;
    }

    public Point(EllipticCurve curve, BigInteger x, BigInteger y, BigInteger z) {
        this.curve = curve;
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = "projective";
    }

    public Point toAffine() {
        BigInteger p = curve.getField().getCharacteristic();
        BigInteger xAff = this.x.multiply(z.modInverse(p)).mod(p);
        BigInteger yAff = this.y.multiply(z.modInverse(p)).mod(p);

        return new Point(this.getCurve(), xAff, yAff);
    }

    public Point toProjective() {
        this.z = BigInteger.ONE;

        return new Point(this.getCurve(), this.x, this.y, this.z);
    }

    public BigInteger getX() {
        return this.x;
    }

    public BigInteger getY() {
        return this.y;
    }

    public BigInteger getZ() {
        return this.z;
    }

    public EllipticCurve getCurve() {
        return this.curve;
    }

    public String getType() {
        return this.type;
    }

    public void setX(BigInteger x) {
        this.x = x;
    }

    public void setY(BigInteger y) {
        this.y = y;
    }

    public void setZ(BigInteger z) {
        this.z = z;
    }

    public void setCurve(EllipticCurve curve) {
        this.curve = curve;
    }

    public void setType(String type) {
        this.type = type;
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

        Object thisZ = this.getZ();
        Object thatZ = that.getZ();

        Object thisCurve = this.getCurve();
        Object thatCurve = that.getCurve();

        Object thisType = this.getType();
        Object thatType = that.getType();

        return Objects.equals(thisX, thatX) && Objects.equals(thisY, thatY) && Objects.equals(thisZ, thatZ) &&
                Objects.equals(thisCurve, thatCurve) && Objects.equals(thisType, thatType);
    }

    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        Object x = this.getX();
        result = result * 78 + (x == null ? 25 : x.hashCode());
        Object y = this.getY();
        result = result * 78 + (y == null ? 25 : y.hashCode());
        Object z = this.getZ();
        result = result * 78 + (y == null ? 25 : z.hashCode());
        Object curve = this.getCurve();
        result = result * 78 + (curve == null ? 25 : curve.hashCode());
        Object type = this.getType();
        result = result * 78 + (curve == null ? 25 : type.hashCode());
        return result;
    }

    public String toString() {
        if (type.equals("affine"))
            return this.getX() + " " + this.getY();
        else
            return this.getX() + " " + this.getY() + " " + this.getZ();
    }
}