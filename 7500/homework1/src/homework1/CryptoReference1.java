package homework1;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class CryptoReference1 {

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        String messageStr = "Cryptocurrency is the future";
        byte[] message = messageStr.getBytes(StandardCharsets.UTF_8);  //ASCII bytes 
//        StringBuffer sb = new StringBuffer("");
//        for (byte m: message){
//        	sb.append(m + " ");
//        }
        String messageHex = DatatypeConverter.printHexBinary(message); //bytes to hex:  67-43, 114-72
//        System.out.println("bytes:" + sb);
        System.out.println("hexed message: " + messageHex);
        System.out.println("# hex digits in message: " + messageHex.length());
        System.out.println("# bits in message: " + message.length*8);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(message); //SHA256 hash of message bytes
        String hashHex = DatatypeConverter.printHexBinary(hash);
        System.out.println();
        System.out.println("HashHex: " + hashHex);
        System.out.println("# hex digits in hashHex: " + hashHex.length());
        System.out.println("# bits in hashHex: " + hash.length*8);

        //Generate a pseudo-random 256-bit message.
        Random ran = new Random();
        byte[] randomMessage = new byte[32]; //32 byte 256 bit array
        ran.nextBytes(randomMessage); //pseudo-random
        String randomMessageHex = DatatypeConverter.printHexBinary(randomMessage);
        System.out.println();
        System.out.println("Random message: " + randomMessageHex);
        System.out.println("# hex digits in random message: " + randomMessageHex.length());
        System.out.println("# bits in random message: " + randomMessage.length*8);

        //Convert a hex string into a byte array. API requires omitting the leading "0x".
        String idHex = "ED00AF5F774E4135E7746419FEB65DE8AE17D6950C95CEC3891070FBB5B03C77";
        byte[] id = DatatypeConverter.parseHexBinary(idHex);
        System.out.println();
        System.out.println("Convert a byte array to string in hex: " + DatatypeConverter.printHexBinary(id));

        //Concatenate two byte arrays
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write( message );
        outputStream.write( id );
        byte concat[] = outputStream.toByteArray( );
        String concatHex = DatatypeConverter.printHexBinary(concat);
        System.out.println();
        System.out.println("message ◦ id: " + concatHex);
        System.out.println("DONE");
    }
}
