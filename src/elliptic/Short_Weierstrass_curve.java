package elliptic;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;

public class Short_Weierstrass_curve extends EllipticCurve {
    private BigInteger Discriminant;
    private BigInteger p;
    private BigInteger invariant;

    public Short_Weierstrass_curve(Finite_field field, BigInteger a, BigInteger b) throws Message_Err {
        this.Field = field;
        this.Coefficients = new ArrayList(Arrays.asList(a, b));
        this.p = field.getCharacteristic();
        if (BigInteger.valueOf(4L).multiply(a.pow(3)).add(BigInteger.valueOf(27L).multiply(b.pow(2))).mod(this.p).equals(BigInteger.ZERO)) {
            throw new Message_Err("Дискриминант не должен равняться нулю");
        } else {
            this.Discriminant = BigInteger.valueOf(4L).multiply(a.pow(3)).add(BigInteger.valueOf(27L).multiply(b.pow(2))).mod(this.p);
            BigInteger znam = BigInteger.valueOf(4L).multiply(a.pow(3)).add(BigInteger.valueOf(27L).multiply(b.pow(2))).modInverse(this.p);
            this.invariant = BigInteger.valueOf(1728L).multiply(BigInteger.valueOf(4L).multiply(a.pow(3))).multiply(znam).mod(this.p);
            this.Field = field;
        }
    }

    public Short_Weierstrass_curve(Finite_field field, int level) {
        this.p = field.getCharacteristic();
    }


    public boolean isSingular() {
        return this.Discriminant.mod(this.p).equals(BigInteger.ZERO);
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
            Point p1;
            Point p2;
            BigInteger A;
            BigInteger B;
            BigInteger X;
            BigInteger Y;

            p1 = P1;
            p2 = P2;

            A = p2.getX().subtract(p1.getX()).modInverse(this.p);
            B = p2.getY().subtract(p1.getY()).multiply(A).mod(this.p);
            X = B.modPow(BigInteger.TWO, this.p).subtract(p1.getX()).subtract(p2.getX()).mod(this.p);
            Y = B.multiply(p1.getX().subtract(X)).subtract(p1.getY()).mod(this.p);
            return new Point(this, X, Y);
        }
    }


    public Point doublePoint(Point p1) {
        if (p1.equals(new Point(this)))
            return p1;
        else {
            BigInteger V;
            BigInteger W;
            BigInteger X;
            if (p1.getY().equals(BigInteger.ZERO)) {
                return new Point(this);
            } else {
                V = BigInteger.valueOf(3L).multiply(p1.getX().pow(2)).add((BigInteger) this.Coefficients.get(0)).mod(this.p).multiply(BigInteger.TWO.multiply(p1.getY()).modInverse(this.p)).mod(this.p);
                W = V.pow(2).subtract(BigInteger.TWO.multiply(p1.getX())).mod(this.p);
                X = V.multiply(p1.getX().subtract(W)).subtract(p1.getY()).mod(this.p);
                return new Point(this, W, X);
            }
        }
    }


    public Point triplePoint(Point p1) {
        if (p1.equals(new Point(this))) {
            return p1;
        } else {
            BigInteger u;
            BigInteger v;
            BigInteger X;
            BigInteger Z;
            BigInteger E;
            BigInteger d;
            BigInteger D;
            BigInteger I;
            BigInteger del1;

            u = p1.getX();
            v = p1.getY();
            if (v.equals(BigInteger.ZERO)) {
                return p1;
            } else {
                X = v.multiply(BigInteger.TWO).pow(2).mod(this.p);
                Z = u.pow(2).multiply(BigInteger.valueOf(3L)).add((BigInteger) this.Coefficients.get(0)).mod(this.p);
                E = Z.modPow(BigInteger.TWO, this.p);
                d = X.multiply(u.multiply(BigInteger.valueOf(3L))).subtract(E).mod(this.p);
                if (d.equals(BigInteger.ZERO)) {
                    return new Point(this);
                } else {
                    D = d.multiply(v.multiply(BigInteger.TWO)).mod(this.p);
                    I = D.modInverse(this.p);
                    del1 = d.multiply(I).multiply(Z).mod(this.p);
                    BigInteger del2 = X.pow(2).multiply(I).subtract(del1).mod(this.p);
                    BigInteger u1 = del2.subtract(del1).multiply(del2.add(del1)).add(u).mod(this.p);
                    BigInteger v1 = u.subtract(u1).multiply(del2).subtract(v).mod(this.p);
                    return new Point(this, u1, v1);
                }
            }
        }
    }


    public Point fivePoint(Point p1) {
        if (p1.equals((new Point(this)))) {
            return p1;
        } else {
            BigInteger a;
            BigInteger X;
            BigInteger Y;
            BigInteger Z;
            BigInteger XX;
            BigInteger YY;
            BigInteger ZZ;
            BigInteger T;
            BigInteger M;
            BigInteger E;
            BigInteger L;
            BigInteger LL;
            BigInteger U;
            BigInteger EEE;
            BigInteger V;
            BigInteger VV;
            BigInteger N;
            BigInteger W;

            a = p1.getX();
            X = p1.getY();
            Y = this.Coefficients.get(0);
            Z = BigInteger.TWO.multiply(X).modPow(BigInteger.TWO, this.p);
            XX = BigInteger.valueOf(3L).multiply(a.modPow(BigInteger.TWO, this.p)).add(Y).mod(this.p);
            YY = a.multiply(Z).mod(this.p);
            ZZ = BigInteger.valueOf(3L).multiply(YY).subtract(XX.modPow(BigInteger.TWO, this.p)).mod(this.p);
            T = ZZ.modPow(BigInteger.TWO, this.p);
            M = XX.multiply(ZZ).subtract(Z.modPow(BigInteger.TWO, this.p)).mod(this.p);
            E = M.modPow(BigInteger.TWO, this.p);
            L = BigInteger.TWO.multiply(X.multiply(ZZ)).negate().mod(this.p);
            LL = T.multiply(ZZ.subtract(XX.modPow(BigInteger.TWO, this.p))).mod(this.p);
            U = L.multiply(LL.add(E)).mod(this.p);
            EEE = U.modPow(BigInteger.TWO, this.p);
            V = M.multiply(T.multiply(BigInteger.valueOf(3L).multiply(YY).subtract(ZZ)).subtract(E)).add(XX.multiply(T.modPow(BigInteger.TWO, this.p))).mod(this.p);
            VV = Z.multiply(EEE).modInverse(this.p);
            N = BigInteger.TWO.multiply(a).add(Z.multiply(LL.add(E).pow(2).multiply(BigInteger.TWO.multiply(T).multiply(YY.subtract(ZZ)).add(LL).subtract(E)).add(V.pow(2))).multiply(VV)).mod(this.p);
            W = X.add(U.multiply(XX.multiply(T).multiply(LL.add(E)).add(V.multiply(YY.subtract(ZZ).subtract(Z.multiply(N))))).multiply(VV)).mod(this.p);
            return new Point(this, N, W);
        }
    }


    public Point getNegato(Point p1) {
        return new Point(this, p1.getX(), p1.getY().negate().mod(this.p));
    }


    public boolean checkPoint(Point p1) throws Message_Err {
        BigInteger X = p1.getX();
        BigInteger Y = p1.getY();

        if (X.compareTo(this.p.subtract(BigInteger.ONE)) != 1 && Y.compareTo(this.p.subtract(BigInteger.ONE)) != 1) {
            BigInteger A = (BigInteger)this.Coefficients.get(0);
            BigInteger B = (BigInteger)this.Coefficients.get(1);
            return Y.pow(2).subtract(X.pow(3)).subtract(A.multiply(X)).subtract(B).mod(this.p).equals(BigInteger.ZERO);
        } else {
            throw new Message_Err("координаты больше характеристики поля");
        }
    }


    public Point randomPointCurve() throws Message_Err {
        BigInteger x = Primitive_algs.getRandomBigIntegerBetweenRange(BigInteger.ONE, this.getField().getCharacteristic(), new SecureRandom());

        BigInteger f;
        for(f = x.pow(3).add(((BigInteger)this.getCoefficients().get(0)).multiply(x)).add((BigInteger)this.getCoefficients().get(1)).mod(this.p); !Primitive_algs.legendre(f, this.p).equals(BigInteger.ONE); f = x.pow(3).add(((BigInteger)this.getCoefficients().get(0)).multiply(x)).add((BigInteger)this.getCoefficients().get(1)).mod(this.p)) {
            x = Primitive_algs.getRandomBigIntegerBetweenRange(BigInteger.ONE, this.getField().getCharacteristic(), new SecureRandom());
        }

        BigInteger y = (BigInteger)Primitive_algs.TSA(f, this.p).get(0);
        return new Point(this, x, y);
    }


    public BigInteger getDiscriminant() {
        return this.Discriminant;
    }

    public BigInteger getP() {
        return this.p;
    }

    public BigInteger getInvariant() {
        return this.invariant;
    }

    public void setDiscriminant(BigInteger Discriminant) {
        this.Discriminant = Discriminant;
    }

    public void setP(BigInteger p) {
        this.p = p;
    }

    public void setInvariant(BigInteger invariant) {
        this.invariant = invariant;
    }

    public String toString() {
        return "Short_Weierstrass_curve(Discriminant=" + this.getDiscriminant() + ", p=" + this.getP() + ", invariant=" + this.getInvariant() + ")";
    }
}