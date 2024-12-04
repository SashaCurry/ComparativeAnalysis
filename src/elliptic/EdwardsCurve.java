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

    public Point addPoints(Point P, Point Q, String type) {
        if (P.equals(Q))
            return this.doublePoint(P, type);
        else if (P.equals(this.getNegato(Q)))
            return new Point(this, type);
        else if (P.equals(new Point(this, type)))
            return Q;
        else if (Q.equals(new Point(this, type)))
            return P;

        BigInteger a = this.Coefficients.getFirst();
        BigInteger d = this.Coefficients.getLast();
        BigInteger x1 = P.getX(), y1 = P.getY(), z1 = P.getZ();
        BigInteger x2 = Q.getX(), y2 = Q.getY(), z2 = Q.getZ();

        Point res = new Point(this);

        if (type.equals("affine")) {
            BigInteger x1y2 = x1.multiply(y2).mod(this.p);
            BigInteger x2y1 = x2.multiply(y1).mod(this.p);
            BigInteger y1y2 = y1.multiply(y2).mod(this.p);
            BigInteger x1x2 = x1.multiply(x2).mod(this.p);

            BigInteger dx1x2y1y2 = d.multiply(x1y2.multiply(x2y1)).mod(this.p);
            BigInteger denominator = BigInteger.ONE.subtract(dx1x2y1y2.modPow(BigInteger.TWO, this.p)).modInverse(this.p);

            BigInteger xR = x1y2.add(x2y1).multiply(BigInteger.ONE.subtract(dx1x2y1y2)).multiply(denominator).mod(this.p);
            BigInteger yR = y1y2.subtract(a.multiply(x1x2)).multiply(BigInteger.ONE.add(dx1x2y1y2)).multiply(denominator).mod(this.p);

            res.setX(xR);
            res.setY(yR);
            res.setType("affine");
        }
        else if (type.equals("projective")) {
            BigInteger z1z2 = z1.multiply(z2).mod(this.p);
            BigInteger sqr_z1z2 = z1z2.modPow(BigInteger.TWO, this.p);
            BigInteger x1x2 = x1.multiply(x2).mod(this.p);
            BigInteger y1y2 = y1.multiply(y2).mod(this.p);
            BigInteger dx1x2y1y2 = d.multiply(x1x2).multiply(y1y2).mod(this.p);

            BigInteger temp1 = sqr_z1z2.subtract(dx1x2y1y2).mod(this.p);
            BigInteger temp2 = sqr_z1z2.add(dx1x2y1y2).mod(this.p);

            BigInteger xR = z1z2.multiply(temp1).multiply(x1.add(y1).multiply(x2.add(y2)).subtract(x1x2).subtract(y1y2)).mod(this.p);
            BigInteger yR = z1z2.multiply(temp2).multiply(y1y2.subtract(a.multiply(x1x2))).mod(this.p);
            BigInteger zR = temp1.multiply(temp2).mod(this.p);

            res.setX(xR);
            res.setY(yR);
            res.setZ(zR);
            res.setType("projective");
        }

        return res;
    }

    public Point doublePoint(Point P, String type) {
        if (P.equals(new Point(this)))
            return P;

        BigInteger one = BigInteger.ONE, two = BigInteger.TWO;
        BigInteger a = this.getCoefficients().getFirst();
        BigInteger d = this.getCoefficients().getLast();

        BigInteger sqrX = P.getX().modPow(two, this.p);
        BigInteger sqrY = P.getY().modPow(two, this.p);
        BigInteger multXY = P.getX().multiply(P.getY()).mod(this.p);
        BigInteger sqrMultXY = multXY.modPow(two, this.p);

        Point res = new Point(this);

        if (type.equals("affine")) {
            BigInteger denominator = one.subtract(d.modPow(two, this.p).multiply(sqrMultXY.modPow(two, this.p))).modInverse(this.p);

            BigInteger xR = two.multiply(multXY).multiply(one.subtract(d.multiply(sqrMultXY))).multiply(denominator).mod(this.p);
            BigInteger yR = sqrY.subtract(a.multiply(sqrX)).multiply(one.add(d.multiply(sqrMultXY))).multiply(denominator).mod(this.p);

            res.setX(xR);
            res.setY(yR);
            res.setType("affine");
        }
        else if (type.equals("projective")) {
            BigInteger sqr_XaddY = P.getX().add(P.getY()).modPow(BigInteger.TWO, this.p);
            BigInteger axx = a.multiply(sqrX).mod(this.p);
            BigInteger axxyy = axx.add(sqrY).mod(this.p);
            BigInteger sqrZ = P.getZ().modPow(BigInteger.TWO, this.p);

            BigInteger temp = axxyy.subtract(two.multiply(sqrZ)).mod(this.p);

            BigInteger xR = temp.multiply(sqr_XaddY.subtract(sqrX).subtract(sqrY)).mod(this.p);
            BigInteger yR = axxyy.multiply(axx.subtract(sqrY)).mod(this.p);
            BigInteger zR = axxyy.multiply(temp).mod(this.p);

            res.setX(xR);
            res.setY(yR);
            res.setZ(zR);
            res.setType("projective");
        }

        return res;
    }


    public Point getNegato(Point P) {
        if (P.getType().equals("affine"))
            return new Point(this, P.getX().negate().mod(this.p), P.getY());
        else
            return new Point(this, P.getX().negate().mod(this.p), P.getY(), P.getZ());
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