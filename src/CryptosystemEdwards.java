import elliptic.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.*;

public class CryptosystemEdwards {
    protected FiniteField field;
    protected EllipticCurve curve;
    protected Point G;
    protected BigInteger n;

    public CryptosystemEdwards() throws Exception {
        this.field = new FiniteField(new BigInteger("57896044618658097711785492504343953926634992332820282019728792003956564819949"));
        this.curve = new EdwardsCurve(this.field, new BigInteger("-1"), new BigInteger("37095705934669439343138083508754565189542113879843219016388785533085940283555"));
        this.G = new Point(curve, new BigInteger("15112221349535400772501151409588531511454012693041857206046113283949847762202"),
                                  new BigInteger("46316835694926478169428394003475163141307993866256225615783033603165251855960"));
        this.n = new BigInteger("7237005577332262213973186563042994240857116359379907606001950938285454250989");
    }

    public CryptosystemEdwards(BigInteger a, BigInteger d, BigInteger p, BigInteger gX, BigInteger gY, BigInteger n) throws Exception {
        this.field = new FiniteField(p);
        this.curve = new EdwardsCurve(field, a, d);
        this.G = new Point(curve, gX, gY);
        this.n = n;
    }


    public void generateKeys() {
        try (BufferedWriter writerClosedKey = Files.newBufferedWriter(Path.of("d.txt"));
             BufferedWriter writerOpenKey = Files.newBufferedWriter(Path.of("P.txt"))) {
            BigInteger d = PrimitiveAlgs.getRandomBigIntegerBetweenRange(new BigInteger("2"), n.subtract(BigInteger.ONE), new SecureRandom());
            writerClosedKey.write(String.valueOf(d));

            Point P = curve.scalarMult(G.toProjective(), d);
            writerOpenKey.write(P.toString());

            System.out.println("\nЗакрытый ключ записан в файл \"d.txt\" \nОткрытый ключ записан в \"P.txt\"");
        } catch (IOException ignored) {
        }
    }


    protected String[] getFilesForEncryption() {
        Scanner in = new Scanner(System.in);
        String[] files = new String[3];

        System.out.print("\nФайл с открытым ключом (по умолчанию P.txt): ");
        files[0] = in.nextLine();
        if (files[0].isEmpty())
            files[0] = "P.txt";

        System.out.print("Файл для шифрования данных (по умолчанию inputData.txt): ");
        files[1] = in.nextLine();
        if (files[1].isEmpty())
            files[1] = "inputData.txt";

        System.out.print("Файл для записи зашифрованных данных (по умолчанию encryptedData.txt): ");
        files[2] = in.nextLine();
        if (files[2].isEmpty())
            files[2] = "encryptedData.txt";

        return files;
    }


    protected BigInteger[] encryptedPoints(Point P, String strCode) {
        BigInteger k = BigInteger.valueOf(new Random().nextLong() + 1).mod(n);
        Point C1 = curve.scalarMult(G.toProjective(), k);

        BigInteger a = curve.getCoefficients().get(0), d = curve.getCoefficients().get(1);
        BigInteger one = BigInteger.ONE, p = field.getCharacteristic();

        int offset = 0;
        BigInteger x = new BigInteger(strCode);
        BigInteger sqrX = x.modPow(BigInteger.TWO, p);

        BigInteger denominator = one.subtract(d.multiply(x.pow(2))).modInverse(p);
        BigInteger yy = one.subtract(a.multiply(x.pow(2))).multiply(denominator).mod(p);
        for (; !PrimitiveAlgs.symbolLegendre(yy, p).equals(one); offset++) {
            x = x.add(BigInteger.ONE).mod(p);
            denominator = one.subtract(d.multiply(x.pow(2))).modInverse(p);
            yy = one.subtract(a.multiply(x.pow(2))).multiply(denominator).mod(p);
        }
        BigInteger y = PrimitiveAlgs.sqrtFromZp(yy, p);

        Point tempPoint = curve.scalarMult(P, k);
        Point C2 = curve.addPoints(new Point(curve, x, y, one), tempPoint, "projective");
        return new BigInteger[] {C1.getX(), C1.getY(), C1.getZ(), C2.getX(), C2.getY(), C2.getZ(), BigInteger.valueOf(offset)};
    }


    public void encryptData() {
        String[] files = getFilesForEncryption();
        String openKeyFile = files[0], inputDataFile = files[1], encryptedDataFile = files[2];

        Point P = null;
        try (BufferedReader reader = Files.newBufferedReader(Path.of(openKeyFile))) {
            String[] str = reader.readLine().trim().split(" ");
            P = new Point(curve, new BigInteger(str[0]), new BigInteger(str[1]), new BigInteger(str[2]));
        } catch (IOException ignored) {
            System.out.println("Ошибка при открытии файла " + openKeyFile);
            return;
        }

        StringBuilder strCode = new StringBuilder();
        int sizeP = String.valueOf(this.field.getCharacteristic()).length();

        try (BufferedReader reader = Files.newBufferedReader(Path.of(inputDataFile));
             BufferedWriter writer = Files.newBufferedWriter(Path.of(encryptedDataFile))) {
            while (reader.ready()) {
                strCode.append(String.format("%04d", reader.read()));

                if (strCode.length() + 4 >= sizeP) {
                    BigInteger[] points = encryptedPoints(P, String.valueOf(strCode));
                    writer.write(points[0] + " " + points[1] + " " + points[2] + " " + points[3] + " " +
                                 points[4] + " " + points[5] + " " + points[6] + "\n");
                    strCode = new StringBuilder();
                }
            }
            if (!strCode.isEmpty()) {
                BigInteger[] points = encryptedPoints(P, String.valueOf(strCode));
                writer.write(points[0] + " " + points[1] + " " + points[2] + " " + points[3] + " " +
                             points[4] + " " + points[5] + " " + points[6] + "\n");
            }
        } catch (IOException ignored) {
            System.out.println("Ошибка при открытии файла " + inputDataFile);
            return;
        }
        System.out.println("\nДанные успешно зашифрованы в " + encryptedDataFile);
    }


    protected String[] getFilesForDecryption() {
        Scanner in = new Scanner(System.in);
        String[] files = new String[3];

        System.out.print("\nФайл с закрытым ключом (по умолчанию d.txt): ");
        files[0] = in.nextLine();
        if (files[0].isEmpty())
            files[0] = "d.txt";

        System.out.print("Файл для расшифрования данных (по умолчанию encryptedData.txt): ");
        files[1] = in.nextLine();
        if (files[1].isEmpty())
            files[1] = "encryptedData.txt";

        System.out.print("Файл для записи зашифрованных данных (по умолчанию decryptedData.txt): ");
        files[2] = in.nextLine();
        if (files[2].isEmpty())
            files[2] = "decryptedData.txt";

        return files;
    }


    public void decryptData() {
        String[] files = getFilesForDecryption();
        String closedKeyFile = files[0], encryptedDataFile = files[1], decryptedDataFile = files[2];

        BigInteger d = null;
        try (BufferedReader reader = Files.newBufferedReader(Path.of(closedKeyFile))) {
            String str = reader.readLine().trim();
            d = new BigInteger(str);
        } catch (IOException ignored) {
            System.out.println("Ошибка при открытии файла " + closedKeyFile);
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(Path.of(encryptedDataFile));
             BufferedWriter writer = Files.newBufferedWriter(Path.of(decryptedDataFile))) {
            while (reader.ready()) {
                String[] points = reader.readLine().trim().split(" ");
                Point C1 = new Point(curve, new BigInteger(points[0]), new BigInteger(points[1]), new BigInteger(points[2]));
                Point C2 = new Point(curve, new BigInteger(points[3]), new BigInteger(points[4]), new BigInteger(points[5]));

                Point S = curve.scalarMult(C1, d);                                                      //S = d * C1
                BigInteger M = curve.addPoints(C2, curve.getNegato(S), "projective").toAffine().getX(); //M = C2 - S

                M = M.subtract(new BigInteger(points[6])).mod(field.getCharacteristic());
                String strCode = String.valueOf(M);
                while (strCode.length() % 4 != 0)
                    strCode = "0" + strCode;

                for (int i = 0; i < strCode.length(); i += 4)
                    writer.write(Integer.parseInt(strCode.substring(i, i + 4)));
            }
        } catch (IOException ignored) {
            System.out.println("Ошибка при открытии файла " + encryptedDataFile);
            return;
        }
        System.out.println("\nДанные успешно расшифрованы в " + decryptedDataFile);
    }


    public void run() {
        Scanner in = new Scanner(System.in);
        for (; ; ) {
            System.out.println("\n1 - сгенерировать ключ \n2 - зашифровать данные \n3 - расшифровать данные" +
                    "\n4 - назад");
            try {
                char choice = in.nextLine().charAt(0);
                long startTime = System.currentTimeMillis();
                switch (choice) {
                    case '1':
                        generateKeys();
                        System.out.println("Время работы: " + (System.currentTimeMillis() - startTime) + "ms");
                        break;
                    case '2':
                        encryptData();
                        System.out.println("Время работы: " + (System.currentTimeMillis() - startTime) + "ms");
                        break;
                    case '3':
                        decryptData();
                        System.out.println("Время работы: " + (System.currentTimeMillis() - startTime) + "ms");
                        break;
                    default:
                        return;
                }
            } catch (IndexOutOfBoundsException ignored) {
            }
        }
    }
}