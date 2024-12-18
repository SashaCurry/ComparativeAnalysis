import java.math.BigInteger;
import java.util.*;


public class Main {
    private static void mainRSA(Scanner in) {
        for (;;) {
            System.out.print("\nВведите размер ключа (по умолчанию 2048 бит): ");
            String keySize = in.nextLine();

            try {
                if (keySize.isEmpty())
                    new CryptosystemRSA().run();
                else
                    new CryptosystemRSA(Integer.parseInt(keySize)).run();
                return;
            } catch (NumberFormatException ignored) {
                System.out.println("Неккоректное число!");
            }
        }
    }


    private static void mainWeierstrassElCurve(Scanner in) throws Exception {
        for (;;) {
            System.out.println("\nВыберите размер ключа (по умолчанию 256 бит):");
            System.out.println("1 - 192 бит \n2 - 224 бит \n3 - 256 бит \n4 - 384 бит \n5 - 521 бит \n6 - назад");

            BigInteger a, b, p, gX, gY, n;
            try {
                char choice = in.nextLine().charAt(0);
                switch (choice) {
                    case '1':
                        a = new BigInteger("-3");
                        b = new BigInteger("2455155546008943817740293915197451784769108058161191238065");
                        p = new BigInteger("6277101735386680763835789423207666416083908700390324961279");
                        gX = new BigInteger("602046282375688656758213480587526111916698976636884684818");
                        gY = new BigInteger("174050332293622031404857552280219410364023488927386650641");
                        n = new BigInteger("6277101735386680763835789423176059013767194773182842284081");
                        break;
                    case '2':
                        a = new BigInteger("0");
                        b = new BigInteger("5");
                        p = new BigInteger("26959946667150639794667015087019630673637144422540572481099315275117");
                        gX = new BigInteger("16983810465656793445178183341822322175883642221536626637512293983324");
                        gY = new BigInteger("13272896753306862154536785447615077600479862871316829862783613755813");
                        n = new BigInteger("26959946667150639794667015087019621000763961761961070413207966856054");
                        break;
                    case '3':
                        a = new BigInteger("-3");
                        b = new BigInteger("41058363725152142129326129780047268409114441015993725554835256314039467401291");
                        p = new BigInteger("115792089210356248762697446949407573530086143415290314195533631308867097853951");
                        gX = new BigInteger("48439561293906451759052585252797914202762949526041747995844080717082404635286");
                        gY = new BigInteger("36134250956749795798585127919587881956611106672985015071877198253568414405109");
                        n = new BigInteger("115792089210356248762697446949407573529996955224135760342422259061068512044369");
                        break;
                    case '4':
                        a = new BigInteger("-3");
                        b = new BigInteger("27580193559959705877849011840389048093056905856361568521428707301988689241309860865136260764883745107765439761230575");
                        p = new BigInteger("39402006196394479212279040100143613805079739270465446667948293404245721771496870329047266088258938001861606973112319");
                        gX = new BigInteger("26247035095799689268623156744566981891852923491109213387815615900925518854738050089022388053975719786650872476732087");
                        gY = new BigInteger("8325710961489029985546751289520108179287853048861315594709205902480503199884419224438643760392947333078086511627871");
                        n = new BigInteger("39402006196394479212279040100143613805079739270465446667946905279627659399113263569398956308152294913554433653942643");
                        break;
                    case '5':
                        a = new BigInteger("-3");
                        b = new BigInteger("1093849038073734274511112390766805569936207598951683748994586394495953116150735016013708737573759623248592132296706313309438452531591012912142327488478985984");
                        p = new BigInteger("6864797660130609714981900799081393217269435300143305409394463459185543183397656052122559640661454554977296311391480858037121987999716643812574028291115057151");
                        gX = new BigInteger("2661740802050217063228768716723360960729859168756973147706671368418802944996427808491545080627771902352094241225065558662157113545570916814161637315895999846");
                        gY = new BigInteger("3757180025770020463545507224491183603594455134769762486694567779615544477440556316691234405012945539562144444537289428522585666729196580810124344277578376784");
                        n = new BigInteger("6864797660130609714981900799081393217269435300143305409394463459185543183397655394245057746333217197532963996371363321113864768612440380340372808892707005449");
                        break;
                    default:
                        return;
                }

                new CryptosystemWeierstrass(a, b, p, gX, gY, n).run();
                return;
            } catch (IndexOutOfBoundsException ignored) {
                new CryptosystemWeierstrass().run();
            }
        }
    }


    private static void mainEdwardsElCurve(Scanner in) throws Exception {
        for (;;) {
            System.out.println("\nВыберите размер ключа (по умолчанию 255 бит):");
            System.out.println("1 - 255 бит \n2 - 448 бит \n3 - назад");

            BigInteger a, d, p, gX, gY, n;
            try {
                char choice = in.nextLine().charAt(0);
                switch (choice) {
                    case '1':
                        a = new BigInteger("-1");
                        d = new BigInteger("37095705934669439343138083508754565189542113879843219016388785533085940283555");
                        p = new BigInteger("57896044618658097711785492504343953926634992332820282019728792003956564819949");
                        gX = new BigInteger("15112221349535400772501151409588531511454012693041857206046113283949847762202");
                        gY = new BigInteger("46316835694926478169428394003475163141307993866256225615783033603165251855960");
                        n = new BigInteger("7237005577332262213973186563042994240857116359379907606001950938285454250989");
                        break;
                    case '2':
                        a = new BigInteger("1");
                        d = new BigInteger("726838724295606890549323807888004534353641360687318060281490199180612328166730772686396383698676545930088884461843637361053498018326358");
                        p = new BigInteger("726838724295606890549323807888004534353641360687318060281490199180612328166730772686396383698676545930088884461843637361053498018365439");
                        gX = new BigInteger("224580040295924300187604334099896036246789641632564134246125461686950415467406032909029192869357953282578032075146446173674602635247710");
                        gY = new BigInteger("298819210078481492676017930443930673437544040154080242095928241372331506189835876003536878655418784733982303233503462500531545062832660");
                        n = new BigInteger("181709681073901722637330951972001133588410340171829515070372549795146003961539585716195755291692375963310293709091662304773755859649779");
                        break;
                    default:
                        return;
                }

                new CryptosystemEdwards(a, d, p, gX, gY, n).run();
                return;
            } catch (IndexOutOfBoundsException ignored) {
                new CryptosystemEdwards().run();
            }
        }
    }


    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        for (; ; ) {
            System.out.println("\n1 - RSA \n2 - Эллиптическая кривая Вейерштрасса \n3 - Эллиптическая кривая Эдвардса" +
                               "\n4 - Выход");
            try {
                char choice = in.nextLine().charAt(0);
                switch (choice) {
                    case '1' -> mainRSA(in);
                    case '2' -> mainWeierstrassElCurve(in);
                    case '3' -> mainEdwardsElCurve(in);
                    default -> {
                        in.close();
                        return;
                    }
                }
            } catch (IndexOutOfBoundsException ignored) {
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}