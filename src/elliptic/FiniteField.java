package elliptic;

import java.math.BigInteger;
import java.security.SecureRandom;

public class FiniteField {
    private BigInteger Characteristic;

    public FiniteField(BigInteger p) throws MessageErr {
        SecureRandom rnd = new SecureRandom();
        if (!PrimitiveAlgs.miller_rabin(p, rnd))
            throw new MessageErr("характеристика поля не является простым числом");
        else
            this.Characteristic = p;
    }

    public FiniteField() {
    }

    public BigInteger getCharacteristic() {
        return this.Characteristic;
    }

    public void setCharacteristic(BigInteger Characteristic) {
        this.Characteristic = Characteristic;
    }

    public String toString() {
        return "Finite_field(Characteristic = " + this.getCharacteristic() + ")";
    }
}