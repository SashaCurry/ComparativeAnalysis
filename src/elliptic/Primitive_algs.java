package elliptic;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;

public class Primitive_algs {
    public Primitive_algs() {
    }

    public static BigInteger getRandomBigIntegerBetweenRange(BigInteger min, BigInteger max, SecureRandom random) {
        BigInteger randomNumber;
        do {
            randomNumber = new BigInteger(max.bitLength(), random);
        } while (randomNumber.compareTo(max) > 0 || randomNumber.compareTo(min) < 0);

        return randomNumber;
    }

    public static int jacobi(BigInteger a, BigInteger n) throws Exception {
        BigInteger b = a.mod(n);
        int t = 1;

        BigInteger n_1;
        label35:
        for (n_1 = n; !b.equals(BigInteger.ZERO); b = b.mod(n_1)) {
            while (true) {
                BigInteger r;
                do {
                    if (!b.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
                        BigInteger s = n_1;
                        n_1 = b;
                        b = s;
                        if (n_1.mod(BigInteger.valueOf(4L)).equals(BigInteger.valueOf(3L)) && b.mod(BigInteger.valueOf(4L)).equals(BigInteger.valueOf(3L))) {
                            t *= -1;
                        }
                        continue label35;
                    }

                    b = b.divide(BigInteger.TWO);
                    r = n_1.mod(BigInteger.valueOf(8L));
                } while (!r.equals(BigInteger.valueOf(3L)) && !r.equals(BigInteger.valueOf(5L)));

                t *= -1;
            }
        }

        if (n_1.equals(BigInteger.ONE)) {
            return t;
        } else {
            return 0;
        }
    }

    public static BigInteger legendre(BigInteger a, BigInteger p) {
        BigInteger zero = BigInteger.ZERO, one = BigInteger.ONE, _one = BigInteger.ONE.negate();
        if (a.remainder(p).equals(zero))
            return zero;
        while (a.compareTo(zero) < 0)
            a = a.add(p);
        BigInteger res = a.modPow(p.subtract(one).divide(BigInteger.TWO), p);
        return res.equals(one) ? one : _one;
    }

    public static ArrayList<BigInteger> PAE(BigInteger a, BigInteger b) {
        BigInteger x_0 = BigInteger.ONE;
        BigInteger y_0 = BigInteger.ZERO;
        BigInteger x_1 = BigInteger.ZERO;

        BigInteger y_swap;
        for (BigInteger y_1 = BigInteger.ONE; !b.equals(BigInteger.ZERO); y_0 = y_swap) {
            BigInteger q = a.divide(b);
            BigInteger r = a.mod(b);
            a = b;
            b = r;
            BigInteger x_swap = x_1;
            x_1 = x_0.subtract(q.multiply(x_1));
            x_0 = x_swap;
            y_swap = y_1;
            y_1 = y_0.subtract(q.multiply(y_1));
        }

        return new ArrayList(List.of(a, x_0, y_0));
    }

    public static BigInteger POW(BigInteger a, BigInteger b, BigInteger m) {
        BigInteger res = BigInteger.ONE;
        BigInteger a_i = a;
        String binary_b = (new StringBuilder(b.toString(2))).reverse().toString();

        for (int i = 0; i < binary_b.length(); ++i) {
            if (binary_b.charAt(i) == '1') {
                res = res.multiply(a_i).mod(m);
            }

            a_i = a_i.pow(2).mod(m);
        }

        return res;
    }


//        public static boolean soloveyShtrassen(BigInteger n, SecureRandom random) throws Exception {
//        if (n.equals(BigInteger.TWO)) {
//            return true;
//        } else {
//            int k = BigIntegerMath.log2(n, RoundingMode.CEILING);
//
//            for(int i = 0; i < k; ++i) {
//                BigInteger a = getRandomBigIntegerBetweenRange(BigInteger.valueOf(2L), n.subtract(BigInteger.ONE), random);
//                if (((BigInteger)PAE(a, n).get(0)).compareTo(BigInteger.ONE) > 0) {
//                    return false;
//                }
//
//                BigInteger deg = n.subtract(BigInteger.ONE).divide(BigInteger.TWO);
//                int j = jacobi(a, n);
//                if (j >= 0 && !a.modPow(deg, n).equals(BigInteger.valueOf((long)j))) {
//                    return false;
//                }
//            }
//
//            return true;
//        }
//    }
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


    public static BigInteger g(BigInteger x, BigInteger n) {
        return x.pow(2).subtract(BigInteger.ONE).mod(n);
    }


    public static BigInteger RHO_Pollard(BigInteger l) throws Exception {
        if (l.mod(BigInteger.TWO) == BigInteger.ZERO) {
            return BigInteger.TWO;
        } else {
            BigInteger x = BigInteger.TWO;
            BigInteger y = BigInteger.TWO;

            BigInteger d;
            BigInteger abs;
            for (d = BigInteger.ONE; d.equals(BigInteger.ONE); d = (BigInteger) PAE(abs, l).get(0)) {
                x = g(x, l);
                y = g(g(y, l), l);
                if (x.compareTo(y) >= 0) {
                    abs = x.subtract(y);
                } else {
                    abs = y.subtract(x);
                }
            }

            if (d.equals(l)) {
                throw new Exception("не нашелся делитель");
            } else {
                return d;
            }
        }
    }

    public static BigInteger primitiveRoot(BigInteger m, int bit, SecureRandom random) throws Exception {
        if (!miller_rabin(m, random)) {
            throw new Exception("модуль должен быть простым");
        } else {
            BigInteger low = BigInteger.TWO.pow(bit - 1);

            for (BigInteger high = BigInteger.TWO.pow(bit); low.compareTo(high) < 0; low = low.add(BigInteger.ONE)) {
                if (((BigInteger) PAE(low, m).get(0)).equals(BigInteger.ONE)) {
                    BigInteger phi = m.subtract(BigInteger.ONE);
                    BigInteger phi_1 = phi;
                    boolean flag = true;

                    while (!phi_1.equals(BigInteger.ONE)) {
                        BigInteger p;
                        if (!miller_rabin(phi_1, random)) {
                            p = RHO_Pollard(phi_1);
                        } else {
                            p = phi_1;
                        }

                        BigInteger l = phi.divide(p);
                        phi_1 = phi_1.divide(p);
                        if (POW(low, l, m).equals(BigInteger.ONE)) {
                            flag = false;
                            break;
                        }
                    }

                    if (flag) {
                        break;
                    }
                }
            }

            return low;
        }
    }

    public static ArrayList<BigInteger> TSA(BigInteger n, BigInteger p) throws Message_Err {
        if (!legendre(n, p).equals(BigInteger.ONE)) {
            throw new Message_Err("невозможно найти квадратный корень по модулю");
        } else {
            BigInteger q = p.subtract(BigInteger.ONE);

            BigInteger ss;
            for (ss = BigInteger.ZERO; q.and(BigInteger.ONE).equals(BigInteger.ZERO); q = q.shiftRight(1)) {
                ss = ss.add(BigInteger.ONE);
            }

            BigInteger z;
            if (ss.equals(BigInteger.ONE)) {
                z = n.modPow(p.add(BigInteger.ONE).divide(BigInteger.valueOf(4L)), p);
                return new ArrayList(Arrays.asList(z, p.subtract(z)));
            } else {
                for (z = BigInteger.TWO; !legendre(z, p).equals(p.subtract(BigInteger.ONE)); z = z.add(BigInteger.ONE)) {
                }

                BigInteger c = z.modPow(q, p);
                BigInteger r = n.modPow(q.add(BigInteger.ONE).divide(BigInteger.TWO), p);
                BigInteger t = n.modPow(q, p);

                BigInteger i;
                for (BigInteger m = ss; !t.equals(BigInteger.ONE); m = i) {
                    i = BigInteger.ZERO;

                    for (BigInteger zz = t; !zz.equals(BigInteger.ONE) && i.compareTo(m.subtract(BigInteger.ONE)) < 0; i = i.add(BigInteger.ONE)) {
                        zz = zz.multiply(zz).mod(p);
                    }

                    BigInteger b = c;

                    for (BigInteger e = m.subtract(i).subtract(BigInteger.ONE); e.compareTo(BigInteger.ZERO) > 0; e = e.subtract(BigInteger.ONE)) {
                        b = b.multiply(b).mod(p);
                    }

                    r = r.multiply(b).mod(p);
                    c = b.multiply(b).mod(p);
                    t = t.multiply(c).mod(p);
                }

                return new ArrayList(Arrays.asList(r, p.subtract(r)));
            }
        }
    }

    public static List<Integer> NAF(BigInteger scalar) {
        BigInteger k = scalar;
        List<Integer> res = new ArrayList();
        List<Integer> binary_k = new ArrayList();
        char[] var4 = ((new StringBuilder(k.toString(2))).reverse().toString() + "00").toCharArray();
        int i = var4.length;

        int c_next;
        for (c_next = 0; c_next < i; ++c_next) {
            char ch = var4[c_next];
            binary_k.add(Integer.parseInt(String.valueOf(ch)));
        }

        int c = 0;

        for (i = 0; i < binary_k.size() - 1; ++i) {
            c_next = ((Integer) binary_k.get(i) + (Integer) binary_k.get(i + 1) + c) / 2;
            res.add((Integer) binary_k.get(i) + c - 2 * c_next);
            c = c_next;
        }

        Collections.reverse(res);
        return res;
    }

    public static BigInteger into_NAF(List<Integer> NAF) {
        BigInteger res = BigInteger.ZERO;
        int len = NAF.size() - 1;

        for (int i = 0; i <= len; ++i) {
            if ((Integer) NAF.get(i) > 0) {
                res = res.add(BigInteger.TWO.pow(len - i));
            } else if ((Integer) NAF.get(i) < 0) {
                res = res.subtract(BigInteger.TWO.pow(len - i));
            }
        }

        return res;
    }

    public static List<Integer> w_NAF(BigInteger scalar, int w) {
        List<Integer> w_NAF = new ArrayList();

        for (BigInteger k = scalar; k.compareTo(BigInteger.ZERO) == 1; k = k.divide(BigInteger.TWO)) {
            if (k.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
                w_NAF.add(0);
            } else {
                BigInteger r = k.mod(BigInteger.TWO.pow(w));
                if (r.compareTo(BigInteger.TWO.pow(w - 1)) == 1) {
                    r = r.subtract(BigInteger.TWO.pow(w));
                }

                w_NAF.add(r.intValue());
                k = k.subtract(r);
            }
        }

        return w_NAF;
    }

    public static ArrayList<List> wmb_NAF(BigInteger scalar, List<BigInteger> base, int w) {
        List<Integer> wmb_NAF = new ArrayList();
        List<BigInteger> bases_NAF = new ArrayList();
        BigInteger k = scalar;

        while (true) {
            while (k.compareTo(BigInteger.ZERO) == 1) {
                int base_index = -1;

                for (int i = 0; i < base.size(); ++i) {
                    if (k.mod((BigInteger) base.get(i)).equals(BigInteger.ZERO)) {
                        base_index = i;
                        break;
                    }
                }

                if (base_index != -1) {
                    wmb_NAF.add(0);
                    bases_NAF.add((BigInteger) base.get(base_index));
                    k = k.divide((BigInteger) base.get(base_index));
                } else {
                    BigInteger k_i = k.mod(((BigInteger) base.get(0)).pow(w));
                    k = k.subtract(k_i);
                    wmb_NAF.add(k_i.intValue());
                    Iterator var8 = base.iterator();

                    while (var8.hasNext()) {
                        BigInteger b = (BigInteger) var8.next();
                        if (k.mod(b).equals(BigInteger.ZERO)) {
                            bases_NAF.add(b);
                            k = k.divide(b);
                            break;
                        }
                    }
                }
            }

            ArrayList<List> res = new ArrayList();
            res.add(wmb_NAF);
            res.add(bases_NAF);
            return res;
        }
    }


    public static BigInteger sqrtFromZp(BigInteger a, BigInteger p) {
        BigInteger zero = BigInteger.ZERO, one = BigInteger.ONE, two = BigInteger.TWO;

        BigInteger m = zero, q = p.subtract(one);
        while (q.mod(two).compareTo(one) != 0) {
            m = m.add(one);
            q = q.divide(two);
        }

        BigInteger b = BigInteger.valueOf(new Random().nextLong()).mod(p);
        while (!legendre(b, p).equals(one.negate()))
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