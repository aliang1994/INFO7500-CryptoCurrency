package homework1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

public class question3 {
	private static int count = 0; // count x
	
	
	 /** 
	  * Find an x such that H (x ◦ id) ∈ Y where
	  * a. H = SHA-256
	  * b. id = 0xED00AF5F774E4135E7746419FEB65DE8AE17D6950C95CEC3891070FBB5B03C77
	  * c. Y is the set of all 256 bit values that have some byte with the value 0x1D
	  */

	 public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
		 
		 while(count<10){
			 // id
			 String idhex = "ED00AF5F774E4135E7746419FEB65DE8AE17D6950C95CEC3891070FBB5B03C77";
			 byte[] id = DatatypeConverter.parseHexBinary(idhex);
			 
			 
			 // random x
			 Random ran = new Random();
		     byte[] randomByteArray = new byte[32]; //32 byte 256 bit array
		     ran.nextBytes(randomByteArray);
		     
		     String randomHex = DatatypeConverter.printHexBinary(randomByteArray);
		     
		     
		     //concatenate bytes in x and id
		     ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		     outputStream.write(randomByteArray);
		     outputStream.write(id);
		     byte concat[] = outputStream.toByteArray( );
		     
		     
		     //SHA 256 hashing
		     MessageDigest digest = MessageDigest.getInstance("SHA-256");
		     byte[] hashconcat = digest.digest(concat); 
		     
		     String hashhex = DatatypeConverter.printHexBinary(hashconcat);

		     
		     //target Y: 256 bits / 32 bytes --- 64 hex digits , two of which are "1D"
		     for(byte b: hashconcat){
		    	 if(b==0x1D){
		    		 System.out.println();
		    		 //System.out.println("hashed hex is :" + hashhex);
		    		 System.out.println("input x is :" + randomHex);
		    		 count++;
		    	 }
		     }
		 }
	 }
}
