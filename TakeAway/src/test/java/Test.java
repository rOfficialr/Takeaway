import org.springframework.util.DigestUtils;

/**
 * @author 翟某人~
 * @version 1.0
 */
public class Test {
    public static void main(String[] args) {
        System.out.println(DigestUtils.md5DigestAsHex("123456".getBytes()));
    }
}
