package elliptic;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

public class EdwardsCurve extends EllipticCurve {
    private BigInteger p;

    public EdwardsCurve(FiniteField field, BigInteger a, BigInteger d) throws MessageErr {
        if (field.getCharacteristic().equals(BigInteger.TWO))
            throw new MessageErr("p не должен быть равен 2");

        this.p = field.getCharacteristic();
        if (d.equals(BigInteger.ONE))
            throw new MessageErr("d не должен быть равен 1");
        else if (d.equals(a))
            throw new MessageErr("Коэффициенты a и d не могут быть одинаковыми");
        else {
            this.Coefficients = new ArrayList<>(Arrays.asList(a, d));
            this.Field = field;
        }
    }

    public Point addPoints(Point P, Point Q) {
        if (P.equals(Q))
            return this.doublePoint(P);
        else if (P.equals(this.getNegato(Q)))
            return new Point(this);
        else if (P.equals(new Point(this)))
            return Q;
        else if (Q.equals(new Point(this)))
            return P;

        BigInteger a = this.Coefficients.getFirst();
        BigInteger d = this.Coefficients.getLast();

        BigInteger xPyQ = P.getX().multiply(Q.getY()).mod(this.p);
        BigInteger xQyP = Q.getX().multiply(P.getY()).mod(this.p);
        BigInteger yPyQ = P.getY().multiply(Q.getY()).mod(this.p);
        BigInteger xPxQ = P.getX().multiply(Q.getX()).mod(this.p);

        BigInteger temp = d.multiply(xPyQ.multiply(xQyP)).mod(this.p);
        BigInteger denominator = BigInteger.ONE.subtract(temp.modPow(BigInteger.TWO, this.p)).modInverse(this.p);

        BigInteger xR = xPyQ.add(xQyP).multiply(BigInteger.ONE.subtract(temp)).multiply(denominator).mod(this.p);
        BigInteger yR = yPyQ.subtract(a.multiply(xPxQ)).multiply(BigInteger.ONE.add(temp)).multiply(denominator).mod(this.p);

        return new Point(this, xR, yR);
    }

    public Point doublePoint(Point P) {
        if (P.equals(new Point(this)))
            return P;

        BigInteger one = BigInteger.ONE, two = BigInteger.TWO;
        BigInteger a = this.getCoefficients().getFirst();
        BigInteger d = this.getCoefficients().getLast();

        BigInteger sqrX = P.getX().modPow(two, this.p);
        BigInteger sqrY = P.getY().modPow(two, this.p);
        BigInteger multXY = P.getX().multiply(P.getY()).mod(this.p);

        BigInteger temp = multXY.modPow(two, this.p);
        BigInteger denominator = one.subtract(d.modPow(two, this.p).multiply(temp.modPow(two, this.p))).modInverse(this.p);

        BigInteger xR = two.multiply(multXY).multiply(one.subtract(d.multiply(temp))).multiply(denominator).mod(this.p);
        BigInteger yR = sqrY.subtract(a.multiply(sqrX)).multiply(one.add(d.multiply(temp))).multiply(denominator).mod(this.p);

        return new Point(this, xR, yR);
    }


    public Point getNegato(Point P) {
        return new Point(this, P.getX().negate().mod(this.p), P.getY());
    }

    public boolean checkPoint(Point P) throws MessageErr {
        BigInteger x = P.getX();
        BigInteger y = P.getY();

        if (x.compareTo(this.p.subtract(BigInteger.ONE)) > 0 || y.compareTo(this.p.subtract(BigInteger.ONE)) > 0)
            throw new MessageErr("координаты больше характеристики поля");

        BigInteger a = this.Coefficients.getFirst();
        BigInteger d = this.Coefficients.getLast();
        return a.multiply(x.pow(2)).add(y.pow(2)).subtract(BigInteger.ONE.add(d.multiply(x.pow(2)).multiply(y.pow(2)))).mod(this.p).equals(BigInteger.ZERO);
    }
}