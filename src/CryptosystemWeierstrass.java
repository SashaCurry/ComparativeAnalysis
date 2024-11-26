import elliptic.*;
import java.io.*;
import java.math.*;
import java.nio.file.*;
import java.security.SecureRandom;
import java.util.*;

public class CryptosystemWeierstrass {
    protected FiniteField field;
    protected EllipticCurve curve;
    protected Point G;
    protected BigInteger n;

    public CryptosystemWeierstrass() throws Exception {
        this.field = new FiniteField(new BigInteger("115792089237316195423570985008687907853269984665640564039457584007908834671663"));
        this.curve = new WeierstrassCurve(field, new BigInteger("0"), new BigInteger("7"));
        this.G = new Point(curve, new BigInteger("55066263022277343669578718895168534326250603453777594175500187360389116729240"),
                                  new BigInteger("32670510020758816978083085130507043184471273380659243275938904335757337482424"));
        this.n = new BigInteger("115792089237316195423570985008687907852837564279074904382605163141518161494337");
    }

    public CryptosystemWeierstrass(BigInteger a, BigInteger b, BigInteger p, BigInteger gX, BigInteger gY, BigInteger n) throws Exception {
        this.field = new FiniteField(p);
        this.curve = new WeierstrassCurve(field, a, b);
        this.G = new Point(curve, gX, gY);
        this.n = n;
    }


    public void generateKeys() {
        try (BufferedWriter writerClosedKey = Files.newBufferedWriter(Path.of("d.txt"));
             BufferedWriter writerOpenKey = Files.newBufferedWriter(Path.of("P.txt"))) {
            BigInteger d = PrimitiveAlgs.getRandomBigIntegerBetweenRange(new BigInteger("2"), n.subtract(BigInteger.ONE), new SecureRandom());
            writerClosedKey.write(String.valueOf(d));

            Point P = curve.scalarMult(G, d);
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
        Point C1 = curve.scalarMult(G, k);

        BigInteger a = curve.getCoefficients().get(0), b = curve.getCoefficients().get(1);
        BigInteger one = BigInteger.ONE, p = field.getCharacteristic();

        int offset = 0;
        BigInteger x = new BigInteger(strCode);
        BigInteger yy = x.pow(3).add(a.multiply(x)).add(b).mod(p);
        for (; !PrimitiveAlgs.symbolLegendre(yy, p).equals(one); offset++) {
            x = x.add(one).mod(p);
            yy = x.pow(3).add(a.multiply(x)).add(b).mod(p);
        }
        BigInteger y = PrimitiveAlgs.sqrtFromZp(yy, p);

        Point C2 = curve.addPoints(new Point(curve, x, y), curve.scalarMult(P, k));
        return new BigInteger[] {C1.getX(), C1.getY(), C2.getX(), C2.getY(), BigInteger.valueOf(offset)};
    }


    public void encryptData() {
        String[] files = getFilesForEncryption();
        String openKeyFile = files[0], inputDataFile = files[1], encryptedDataFile = files[2];

        Point P = null;
        try (BufferedReader reader = Files.newBufferedReader(Path.of(openKeyFile))) {
            String[] str = reader.readLine().trim().split(" ");
            P = new Point(curve, new BigInteger(str[0]), new BigInteger(str[1]));
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
                    writer.write(points[0] + " " + points[1] + " " + points[2] +
                            " " + points[3] + " " + points[4] + "\n");
                    strCode = new StringBuilder();
                }
            }
            if (!strCode.isEmpty()) {
                BigInteger[] points = encryptedPoints(P, String.valueOf(strCode));
                writer.write(points[0] + " " + points[1] + " " + points[2] + " " + points[3] + " " + points[4] + "\n");
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

        BigInteger d;
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
                Point C1 = new Point(curve, new BigInteger(points[0]), new BigInteger(points[1]));
                Point C2 = new Point(curve, new BigInteger(points[2]), new BigInteger(points[3]));

                Point S = curve.scalarMult(C1, d);
                BigInteger M = curve.addPoints(C2, new Point(curve, S.getX(), S.getY().negate())).getX();

                M = M.subtract(new BigInteger(points[4])).mod(field.getCharacteristic());
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