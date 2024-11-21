package elliptic;

import java.math.BigInteger;
import java.security.SecureRandom;

public class Finite_field {
    private BigInteger Characteristic;
    private int Degree;
    private BigInteger Order;

    public Finite_field(BigInteger p, int m) throws Exception {
        SecureRandom rnd = new SecureRandom();
        if (!Primitive_algs.miller_rabin(p, rnd)) {
            throw new Message_Err("характеристика поля не является простым числом");
        } else {
            this.Characteristic = p;
            this.Degree = m;
            this.Order = p.pow(m);
        }
    }

    public Finite_field() {
    }

    public BigInteger getCharacteristic() {
        return this.Characteristic;
    }

    public int getDegree() {
        return this.Degree;
    }

    public BigInteger getOrder() {
        return this.Order;
    }

    public void setCharacteristic(BigInteger Characteristic) {
        this.Characteristic = Characteristic;
    }

    public void setDegree(int Degree) {
        this.Degree = Degree;
    }

    public void setOrder(BigInteger Order) {
        this.Order = Order;
    }

    public String toString() {
        return "Finite_field(Characteristic=" + this.getCharacteristic() + ", Degree=" + this.getDegree() + ", Order=" + this.getOrder() + ")";
    }
}