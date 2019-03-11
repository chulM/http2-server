import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class Test {

    public static void main(String[] args){


        String byteStr = "556e657870656374656420485454502f312e7820726571756573743a20474554202f696e64657820";
        byte[] bytes = DatatypeConverter.parseHexBinary(byteStr);
        try {
            String result= new String(bytes, "UTF-8");
            System.err.println(result);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

}
