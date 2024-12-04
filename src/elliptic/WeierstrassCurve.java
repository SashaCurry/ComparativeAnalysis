package elliptic;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;

public class WeierstrassCurve extends EllipticCurve {
    private BigInteger p;

    public WeierstrassCurve(FiniteField field, BigInteger a, BigInteger b) {
        this.Field = field;
        this.Coefficients = new ArrayList<>(Arrays.asList(a, b));
        this.p = field.getCharacteristic();

        BigInteger discriminant = BigInteger.valueOf(4).multiply(a.pow(3)).add(BigInteger.valueOf(27).multiply(b.pow(2))).mod(this.p);
        if (discriminant.equals(BigInteger.ZERO))
            throw new MessageErr("Дискриминант не должен равняться нулю");
    }


    public Point addPoints(Point P, Point Q, String type) {
        if (P.equals(Q))
            return this.doublePoint(P, type);
        else if (P.equals(this.getNegato(Q)))
            return new Point(this, type);
        else if (P.equals(new Point(this)))
            return Q;
        else if (Q.equals(new Point(this)))
            return P;

        BigInteger denominator = P.getX().subtract(Q.getX()).mod(p);
        if (!denominator.equals(BigInteger.ZERO))
            denominator = denominator.modInverse(p);
        BigInteger λ = P.getY().subtract(Q.getY()).multiply(denominator).mod(p);

        BigInteger xR = λ.pow(2).subtract(P.getX()).subtract(Q.getX()).mod(p);
        BigInteger yR = xR.subtract(P.getX()).multiply(λ).add(P.getY()).negate().mod(p);

        return new Point(this, xR, yR);
    }


    public Point doublePoint(Point P, String type) {
        if (P.equals(new Point(this)))
            return P;
        else if (P.getY().equals(BigInteger.ZERO))
            return new Point(this);

        BigInteger denominator = BigInteger.TWO.multiply(P.getY()).modInverse(this.p);
        BigInteger temp = new BigInteger("3").multiply(P.getX().pow(2))
                                             .add(this.Coefficients.getFirst())
                                             .multiply(denominator).mod(this.p);

        BigInteger xR = temp.pow(2).subtract(BigInteger.TWO.multiply(P.getX())).mod(this.p);
        BigInteger yR = temp.multiply(P.getX().subtract(xR)).subtract(P.getY()).mod(this.p);

        return new Point(this, xR, yR);
    }


    public Point getNegato(Point P) {
        return new Point(this, P.getX(), P.getY().negate().mod(this.p));
    }


    public boolean checkPoint(Point P) throws MessageErr {
        BigInteger x = P.getX();
        BigInteger y = P.getY();

        if (x.compareTo(this.p.subtract(BigInteger.ONE)) > 0 || y.compareTo(this.p.subtract(BigInteger.ONE)) > 0)
            throw new MessageErr("координаты больше характеристики поля");

        BigInteger a = this.Coefficients.get(0);
        BigInteger b = this.Coefficients.get(1);
        return y.pow(2).subtract(x.pow(3)).subtract(a.multiply(x)).subtract(b).mod(this.p).equals(BigInteger.ZERO);
    }


    public Point randomPointCurve() throws MessageErr {
        BigInteger one = BigInteger.ONE;
        BigInteger a = this.getCoefficients().getFirst(), b = this.getCoefficients().getLast();

        BigInteger x = PrimitiveAlgs.getRandomBigIntegerBetweenRange(one, this.p , new SecureRandom());
        BigInteger yy = x.pow(3).add(a.multiply(x)).add(b).mod(this.p);

        while (!PrimitiveAlgs.symbolLegendre(yy, this.p).equals(one)) {
            x = PrimitiveAlgs.getRandomBigIntegerBetweenRange(one, this.p, new SecureRandom());
            yy = x.pow(3).add(a.multiply(x)).add(b).mod(this.p);
        }

        BigInteger y = PrimitiveAlgs.sqrtFromZp(yy, this.p);
        return new Point(this, x, y);
    }


    public BigInteger getP() {
        return this.p;
    }

    public void setP(BigInteger p) {
        this.p = p;
    }

    public String toString() {
        return "Weierstrass_curve(p = " + this.getP() + ")";
    }
}