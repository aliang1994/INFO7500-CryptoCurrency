package homework5;


import java.util.Arrays;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomAddressGenerator {
	protected final static Logger LOGGER = LoggerFactory.getLogger(CustomAddressGenerator.class);
	private static final NetworkParameters PARAMS = MainNetParams.get();
	
	
	
	private static String checkValidChar(String prefix){
		String validstr = prefix.replaceAll("[0oilOIL]","X");  // replace all invalid characters with X 
		return validstr;
	}
	
	private static String checkLength(String prefix){
		char x = 'X';
		int length = prefix.length();
		if(length<5){   //"1" plus first four letters of last name
			char[] repeat = new char[5-length];
			Arrays.fill(repeat, x);
			prefix += new String(repeat);
		}
		else if(length>5){
			prefix = prefix.substring(0, 5);
		}
		return prefix;
	}

	/*  @param prefix	string of letters in base58 encoding
	 *  @returns 		a Bitcoin address on mainnet which starts with 1 followed prefix.
     */
	
	public static String get(String prefix) {
		String validpr = checkValidChar(prefix);
		String checked = checkLength(validpr);
		//System.out.println(checked);
		ECKey keys = null;
		
		while(true){
			keys = new ECKey();
			Address addr = keys.toAddress(PARAMS);
			LOGGER.info("current address is: " + addr.toString());
			
			boolean b = addr.toString().startsWith(checked);
			
			
			if (b==true){
				LOGGER.info("customed address is: " + addr.toString());
				break;
			}
		}		
		return keys.toString();
	}
	
	public static void main(String[] args)  {
        LOGGER.info("encoding starts");
        String key = get("1Liang");
        LOGGER.info("encoding ends");
    }
}