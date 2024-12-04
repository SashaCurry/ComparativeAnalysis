package elliptic;

import java.math.BigInteger;
import java.util.ArrayList;

public abstract class EllipticCurve {
    protected ArrayList<BigInteger> Coefficients;
    protected FiniteField Field;

    public abstract Point addPoints(Point P, Point Q, String type);

    public abstract Point doublePoint(Point P, String type);


    public Point scalarMult(Point P, BigInteger scalar) {
        String type = P.getType();

        if (scalar.equals(BigInteger.ZERO))
            return new Point(this, type);
        else if (scalar.equals(BigInteger.ONE))
            return P;
        else if (scalar.equals(BigInteger.TWO))
            return this.doublePoint(P, type);

        StringBuilder kBin = new StringBuilder(scalar.toString(2)).reverse();
        Point res = new Point(this, type);

        for (int i = 0; i < kBin.length(); ++i) {
            if (kBin.charAt(i) == '1')
                res = this.addPoints(res, P, type);
            P = this.doublePoint(P, type);
        }

        return res;
    }


    public abstract Point getNegato(Point var1);

    public abstract boolean checkPoint(Point var1) throws MessageErr;

    public ArrayList<BigInteger> getCoefficients() {
        return this.Coefficients;
    }

    public FiniteField getField() {
        return this.Field;
    }

    public void setCoefficients(ArrayList<BigInteger> Coefficients) {
        this.Coefficients = Coefficients;
    }

    public void setField(FiniteField Field) {
        this.Field = Field;
    }

    public String toString() {
        return "EllipticCurve(Coefficients = " + this.getCoefficients() + ", Field = " + this.getField() + ")";
    }
}