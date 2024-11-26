package elliptic;

public class MessageErr extends RuntimeException {
    public MessageErr(String str) {
        System.out.println(str);
    }
}