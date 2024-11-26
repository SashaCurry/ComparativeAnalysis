package elliptic;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;

public class PrimitiveAlgs {
    public static BigInteger getRandomBigIntegerBetweenRange(BigInteger min, BigInteger max, SecureRandom random) {
        BigInteger randomNumber;
        do {
            randomNumber = new BigInteger(max.bitLength(), random);
        } while (randomNumber.compareTo(max) > 0 || randomNumber.compareTo(min) < 0);

        return randomNumber;
    }


    public static BigInteger symbolLegendre(BigInteger a, BigInteger p) {
        BigInteger zero = BigInteger.ZERO, one = BigInteger.ONE, _one = BigInteger.ONE.negate();
        if (a.remainder(p).equals(zero))
            return zero;
        while (a.compareTo(zero) < 0)
            a = a.add(p);
        BigInteger res = a.modPow(p.subtract(one).divide(BigInteger.TWO), p);
        return res.equals(one) ? one : _one;
    }


    public static boolean miller_rabin(BigInteger n, SecureRandom random) {
        BigInteger zero = BigInteger.ZERO, one = BigInteger.ONE, two = BigInteger.TWO;
        if (n.equals(zero))
            return false;
        else if (n.equals(one) || n.equals(two) || n.equals(BigInteger.valueOf(3)))
            return true;

        BigInteger d = n.subtract(one);
        int s = 0;
        while (d.remainder(two).equals(zero)) {
            s += 1;
            d = d.divide(two);
        }

        BigInteger nDec = n.subtract(one);
        for (int i = 0; i < 10; i++) {
            BigInteger a;
            do
                a = getRandomBigIntegerBetweenRange(BigInteger.valueOf(2L), n.subtract(BigInteger.ONE), random);
            while (a.equals(zero));

            BigInteger x = a.modPow(d, n);
            if (x.equals(one) || x.equals(nDec))
                continue;

            boolean flag = false;
            for (int j = 0; j < s; j++) {
                x = x.pow(2).mod(n);
                if (x.equals(nDec)) {
                    flag = true;
                    break;
                }
            }
            if (!flag)
                return false;
        }
        return true;
    }


    public static BigInteger sqrtFromZp(BigInteger a, BigInteger p) {
        if (!symbolLegendre(a, p).equals(BigInteger.ONE))
            throw new MessageErr("невозможно найти квадратный корень по модулю");

        BigInteger zero = BigInteger.ZERO, one = BigInteger.ONE, two = BigInteger.TWO;

        BigInteger m = zero, q = p.subtract(one);
        while (q.mod(two).compareTo(one) != 0) {
            m = m.add(one);
            q = q.divide(two);
        }

        BigInteger b = BigInteger.valueOf(new Random().nextLong()).mod(p);
        while (!symbolLegendre(b, p).equals(one.negate()))
            b = b.add(one).mod(p);

        List<BigInteger> kArr = new ArrayList<>();
        for (; ; ) {
            int k = 0;
            while (a.modPow(two.pow(k).multiply(q), p).compareTo(one) != 0)
                k++;
            kArr.add(BigInteger.valueOf(k));
            if (k == 0)
                break;

            BigInteger partCompute = two.pow(m.subtract(kArr.getLast()).intValue());
            a = a.multiply(b.modPow(partCompute, p)).mod(p);
        }

        BigInteger r = a.modPow(q.add(one).divide(two), p);
        for (int i = kArr.size() - 2; i >= 0; i--) {
            BigInteger partCompute = two.pow(m.subtract(kArr.get(i)).subtract(one).intValue());
            r = b.modPow(partCompute, p).modInverse(p).multiply(r).mod(p);
        }

        return r;
    }
}