package elliptic;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;

public abstract class EllipticCurve {
    protected ArrayList<BigInteger> Coefficients;
    protected Finite_field Field;

    public EllipticCurve() {
    }

    public abstract Point addPoints(Point P, Point Q);

    public abstract Point doublePoint(Point P);

    public abstract Point triplePoint(Point P);

    public abstract Point fivePoint(Point P);


    public Point scalarMult(Point P, BigInteger scalar) {
        if (scalar.equals(BigInteger.TWO))
            return this.doublePoint(P);
        else if (scalar.equals(BigInteger.valueOf(3L)))
            return this.triplePoint(P);
        else if (scalar.equals(BigInteger.ZERO))
            return new Point(this);
        else if (scalar.equals(BigInteger.ONE))
            return P;

        Point prec;
        String binary_k;
        Point R1;
        int i;

        binary_k = scalar.toString(2);
        StringBuilder reversed = (new StringBuilder(binary_k)).reverse();
        binary_k = reversed.toString();
        R1 = new Point(this);
        prec = P;

        for (i = 0; i < binary_k.length(); ++i) {
            if (binary_k.charAt(i) == '1') {
                R1 = this.addPoints(R1, prec);
            }

            prec = this.doublePoint(prec);
        }

        return R1;
    }


    public abstract Point getNegato(Point var1);


    public Point substract(Point p1, Point p2) throws Message_Err {
        return this.addPoints(p1, this.getNegato(p2));
    }


    public abstract boolean checkPoint(Point var1) throws Message_Err;


    public Point randomPointCurve(BigInteger x_max, BigInteger y_max) throws Message_Err {
        BigInteger i = Primitive_algs.getRandomBigIntegerBetweenRange(BigInteger.ONE, x_max, new SecureRandom());
        BigInteger j = Primitive_algs.getRandomBigIntegerBetweenRange(BigInteger.ONE, y_max, new SecureRandom());

        Point p1;
        for (p1 = new Point(this, i, j); p1.getX().equals(BigInteger.ZERO) || p1.getY().equals(BigInteger.ZERO) || p1.getX().compareTo(this.getField().getCharacteristic().subtract(BigInteger.ONE)) == 1 || p1.getY().compareTo(this.getField().getCharacteristic().subtract(BigInteger.ONE)) == 1 || !this.checkPoint(p1); p1 = new Point(this, i, j)) {
            i = Primitive_algs.getRandomBigIntegerBetweenRange(BigInteger.ONE, x_max, new SecureRandom());
            j = Primitive_algs.getRandomBigIntegerBetweenRange(BigInteger.ONE, y_max, new SecureRandom());
        }

        return p1;
    }


    public ArrayList<BigInteger> getCoefficients() {
        return this.Coefficients;
    }


    public Finite_field getField() {
        return this.Field;
    }


    public void setCoefficients(ArrayList<BigInteger> Coefficients) {
        this.Coefficients = Coefficients;
    }


    public void setField(Finite_field Field) {
        this.Field = Field;
    }


    public String toString() {
        return "EllipticCurve(Coefficients=" + this.getCoefficients() + ", Field=" + this.getField() + ")";
    }
}