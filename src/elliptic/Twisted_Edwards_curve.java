package elliptic;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

public class Twisted_Edwards_curve extends EllipticCurve {
    private BigInteger p;
    private BigInteger Discriminant;

    public Twisted_Edwards_curve(Finite_field field, BigInteger a, BigInteger d) throws Message_Err {
        if (field.getCharacteristic().equals(BigInteger.TWO)) {
            throw new Message_Err("p не должен быть равен 2");
        } else {
            this.p = field.getCharacteristic();
            if (d.equals(BigInteger.ONE)) {
                throw new Message_Err("d не должен быть равен 1");
            } else if (d.equals(a)) {
                throw new Message_Err("Коэффициенты a и d не могут быть одинаковыми");
            } else {
                this.Coefficients = new ArrayList(Arrays.asList(a, d));
                this.Field = field;
            }
        }
    }

    public Point addPoints(Point P1, Point P2) {
        if (P1.equals(P2))
            return this.doublePoint(P1);
        else if (P1.equals(this.getNegato(P2)))
            return new Point(this);
        else if (P1.equals(new Point(this)))
            return P2;
        else if (P2.equals(new Point(this)))
            return P1;
        else {
            BigInteger X1;
            BigInteger Y1;
            BigInteger Z1;
            BigInteger X2;
            BigInteger Y2;
            BigInteger Z2;
            BigInteger T1;
            BigInteger T2;
            BigInteger A;
            BigInteger B;

            X1 = P1.getX().multiply(P2.getY()).mod(this.p);
            Y1 = P2.getX().multiply(P1.getY()).mod(this.p);
            Z1 = P1.getY().multiply(P2.getY()).mod(this.p);
            X2 = P1.getX().multiply(P2.getX()).mod(this.p);
            Y2 = this.Coefficients.get(0);
            Z2 = this.Coefficients.get(1);
            T1 = Z2.multiply(X1.multiply(Y1)).mod(this.p);
            T2 = BigInteger.ONE.subtract(T1.modPow(BigInteger.TWO, this.p)).modInverse(this.p);
            A = X1.add(Y1).multiply(BigInteger.ONE.subtract(T1)).multiply(T2).mod(this.p);
            B = Z1.subtract(Y2.multiply(X2)).multiply(BigInteger.ONE.add(T1)).multiply(T2).mod(this.p);
            return new Point(this, A, B);
        }
    }

    public Point doublePoint(Point p1) {
        if (p1.equals(new Point(this)))
            return p1;
        else {
            BigInteger X1;
            BigInteger Y1;
            BigInteger Z1;
            BigInteger A;
            BigInteger B;
            BigInteger C;
            BigInteger D;
            BigInteger E;

            X1 = p1.getY().modPow(BigInteger.TWO, this.p);
            Y1 = p1.getX().modPow(BigInteger.TWO, this.p);
            Z1 = p1.getX().multiply(p1.getY()).mod(this.p);
            A = Z1.modPow(BigInteger.TWO, this.p);
            B = this.Coefficients.get(1);
            C = BigInteger.ONE.subtract(B.modPow(BigInteger.TWO, this.p).multiply(A.modPow(BigInteger.TWO, this.p))).modInverse(this.p);
            D = BigInteger.TWO.multiply(Z1).multiply(BigInteger.ONE.subtract(B.multiply(A))).multiply(C).mod(this.p);
            E = X1.subtract(((BigInteger) this.Coefficients.get(0)).multiply(Y1)).multiply(BigInteger.ONE.add(B.multiply(A))).multiply(C).mod(this.p);
            return new Point(this, D, E);
        }
    }

    public Point triplePoint(Point p1) {
        if (p1.equals(new Point(this)))
            return p1;
        else
            return null;
    }

    public Point fivePoint(Point p1) {
        return p1.equals(new Point(this)) ? p1 : null;
    }

    public Point getNegato(Point p1) {
        return new Point(this, p1.getX().negate().mod(this.p), p1.getY());
    }

    public boolean checkPoint(Point p1) throws Message_Err {
        BigInteger d = this.Coefficients.get(1);
        BigInteger a = this.Coefficients.get(0);
        BigInteger x = p1.getX();
        BigInteger y = p1.getY();
        if (x.compareTo(this.p.subtract(BigInteger.ONE)) != 1 && y.compareTo(this.p.subtract(BigInteger.ONE)) != 1) {
            return a.multiply(x.pow(2)).add(y.pow(2)).subtract(BigInteger.ONE.add(d.multiply(x.pow(2)).multiply(y.pow(2)))).mod(this.p).equals(BigInteger.ZERO);
        } else {
            throw new Message_Err("координаты больше характеристики поля");
        }
    }

//    public static void main(String[] args) throws Exception {
//        Finite_field field = new Finite_field(new BigInteger("1009"), 1);
//        EllipticCurve curve = new Twisted_Edwards_curve(field, new BigInteger("1"), new BigInteger("-11"));
//
//        Point P1 = new Point(curve, new BigInteger("127"), new BigInteger("668"));
//        Point P2 = new Point(curve, new BigInteger("998"), new BigInteger("3"));
//
//        Point P3 = new Point(curve, new BigInteger("7"), new BigInteger("415"));
//        Point P4 = new Point(curve, new BigInteger("23"), new BigInteger("487"));
//
//        System.out.println(curve.addPoints(P3, P4));
//    }
}