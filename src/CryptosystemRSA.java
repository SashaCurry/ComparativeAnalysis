import java.io.*;
import java.math.BigInteger;
import java.nio.file.*;
import java.util.*;

public class CryptosystemRSA {
    private BigInteger p, q;
    private final int keySize;
    private final Random randomizer = new Random();

    public CryptosystemRSA() {
        this.keySize = 2048;
    }

    public CryptosystemRSA(int keySize) {
        this.keySize = keySize;
    }

    private boolean miller_rabin(BigInteger n) {
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
                a = BigInteger.valueOf(randomizer.nextLong()).mod(n);
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

    public void generateKeys() {
        do
            this.p = BigInteger.probablePrime((int)Math.ceil(keySize / 2d), randomizer);
        while (!miller_rabin(this.p));

        do
            this.q = BigInteger.probablePrime((int)Math.floor(keySize / 2d), randomizer);
        while (!miller_rabin(this.q));

        BigInteger n = p.multiply(q), one = BigInteger.ONE;
        BigInteger phiN = p.subtract(one).multiply(q.subtract(one));

        BigInteger e;
        do
            e = BigInteger.probablePrime(randomizer.nextInt() % 512 + 512, randomizer).mod(phiN);
        while (!e.gcd(phiN).equals(one));

        BigInteger d = e.modInverse(phiN);

        try (BufferedWriter writerOpenKey = Files.newBufferedWriter(Path.of("(e, n).txt"));
             BufferedWriter writerClosedKey = Files.newBufferedWriter(Path.of("(d, n).txt"))) {
            writerOpenKey.write(e + " " + n);
            writerClosedKey.write(d + " " + n);
        } catch (IOException ignored) {
        }

        System.out.println("\nЗакрытый ключ записан в файл \"(d, n).txt\" \nОткрытый ключ записан в \"(e, n).txt\"");
    }


    protected String[] getFilesForEncryption() {
        Scanner in = new Scanner(System.in);
        String[] files = new String[3];

        System.out.print("\nФайл с открытым ключом (по умолчанию (e, n).txt): ");
        files[0] = in.nextLine();
        if (files[0].isEmpty())
            files[0] = "(e, n).txt";

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


    public void encryptData() {
        String[] files = getFilesForEncryption();
        String openKeyFile = files[0], inputDataFile = files[1], encryptedDataFile = files[2];

        BigInteger e = null, n = null;
        int sizeN = 0;
        try (BufferedReader reader = Files.newBufferedReader(Path.of(openKeyFile))) {
            String[] str = reader.readLine().trim().split(" ");
            e = new BigInteger(str[0]);
            n = new BigInteger(str[1]);
            sizeN = str[1].length();
        } catch (IOException ignored) {
            System.out.println("Ошибка при открытии файла " + openKeyFile);
            return;
        }

        StringBuilder strCode = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(Path.of(inputDataFile));
             BufferedWriter writer = Files.newBufferedWriter(Path.of(encryptedDataFile))) {
            while (reader.ready()) {
                strCode.append(String.format("%04d", reader.read()));

                if (strCode.length() + 4 >= sizeN) {
                    BigInteger m = new BigInteger(String.valueOf(strCode));
                    BigInteger c = m.modPow(e, n);
                    writer.write(c + "\n");

                    strCode = new StringBuilder();
                }
            }
            if (!strCode.isEmpty()) {
                BigInteger m = new BigInteger(String.valueOf(strCode));
                BigInteger c = m.modPow(e, n);
                writer.write(c + "\n");
            }
        } catch (IOException ignored) {
            System.out.println("Ошибка при открытии файла " + inputDataFile);
            return;
        }
        System.out.println("Данные успешно зашифрованы в " + encryptedDataFile);
    }


    protected String[] getFilesForDecryption() {
        Scanner in = new Scanner(System.in);
        String[] files = new String[3];

        System.out.print("\nФайл с закрытым ключом (по умолчанию (d, n).txt): ");
        files[0] = in.nextLine();
        if (files[0].isEmpty())
            files[0] = "(d, n).txt";

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

        BigInteger d = null, n = null;
        try (BufferedReader reader = Files.newBufferedReader(Path.of(closedKeyFile))) {
            String[] str = reader.readLine().trim().split(" ");
            d = new BigInteger(str[0]);
            n = new BigInteger(str[1]);
        } catch (IOException ignored) {
            System.out.println("Ошибка при открытии файла " + closedKeyFile);
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(Path.of(encryptedDataFile));
             BufferedWriter writer = Files.newBufferedWriter(Path.of(decryptedDataFile))) {
            while (reader.ready()) {
                String str = reader.readLine().trim();
                BigInteger c = new BigInteger(str);
                BigInteger m = c.modPow(d, n);

                String strCode = String.valueOf(m);
                while (strCode.length() % 4 != 0)
                    strCode = "0" + strCode;

                for (int i = 0; i < strCode.length(); i += 4)
                    writer.write(Integer.parseInt(strCode.substring(i, i + 4)));
            }

        } catch (IOException ignored) {
            System.out.println("Ошибка при открытии файла " + encryptedDataFile);
            return;
        }
        System.out.println("Данные успешно расшифрованы в " + decryptedDataFile);
    }


//    public void tableCayley() {
//        BigInteger n = p.multiply(q);
//        BigInteger one = BigInteger.ONE;
//
//        List<BigInteger> elems = new ArrayList<>();
//        for (BigInteger el = BigInteger.ONE; !el.equals(n); el = el.add(one))
//            if (el.gcd(n).equals(one))
//                elems.add(el);
//
//
//        System.out.print("     ");
//        for (BigInteger el : elems) {
//            System.out.format("%5s", el);
//        }
//        for (BigInteger el1 : elems) {
//            System.out.format("\n%5s", el1);
//            for (BigInteger el2 : elems) {
//                System.out.format("%5s", el1.multiply(el2).mod(n));
//            }
//        }
//    }


    public void run() {
        Scanner in = new Scanner(System.in);
        for (; ; ) {
            System.out.println("\n1 - сгенерировать ключ \n2 - зашифровать данные \n3 - расшифровать данные" +
                               "\n4 - выход");
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
