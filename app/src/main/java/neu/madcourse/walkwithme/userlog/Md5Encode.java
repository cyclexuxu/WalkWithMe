package neu.madcourse.walkwithme.userlog;

import org.apache.commons.codec.binary.Hex;

import java.nio.charset.Charset;
import java.security.MessageDigest;
public class Md5Encode {
    public static String md5Encryption(final String input) {
        String result = "";
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(input.getBytes(Charset.forName("UTF8")));
            byte[] resultByte = messageDigest.digest();
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<resultByte.length; i++)
                hexString.append(Integer.toHexString(0xFF & resultByte[i]));
            return hexString.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
}