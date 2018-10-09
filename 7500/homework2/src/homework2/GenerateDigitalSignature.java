package homework2;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;

import javax.xml.bind.DatatypeConverter;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCSException;

public class GenerateDigitalSignature {
	
	private static final String KEY_ALGORITHM           = "ECDSA";
	private static final String SIGNATURE_ALGORITHM     = "SHA256withECDSA";
	private static final String PROVIDER                = "BC";
	private static final String CURVE_NAME              = "secp256k1";

	private ECGenParameterSpec  ecGenSpec;
	private KeyPairGenerator    keyGen_;
	private SecureRandom        random;

	public static void main(String[] args) throws Throwable {
		new GenerateDigitalSignature().run("scroogekey_sk.pem", "060313");
	}
	
	public void run(String skFilename, String password) throws NoSuchAlgorithmException, InvalidKeySpecException, OperatorCreationException, IOException, PKCSException, InvalidKeyException, SignatureException {
		Security.addProvider(new BouncyCastleProvider());
		
		//retrieve encrypted sk
		PrivateKey secretKey = loadSecretKeyFromEncrypted(skFilename, password);
		
		//generate signature using sk
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initSign(secretKey, random);
		String msgstr = "Pay 3 bitcoins to Alice";
		byte[] message1 = msgstr.getBytes(StandardCharsets.UTF_8);
		signature.update(message1);
		byte[] sigBytes1 = signature.sign();
		
		System.out.println("Signature: msg= " + msgstr);
		System.out.println("sig.len= " + sigBytes1.length);
		System.out.println("sig.hex= " + DatatypeConverter.printHexBinary(sigBytes1));
	}
	
	public static PrivateKey loadSecretKeyFromEncrypted(String filename, String password) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, PKCSException, OperatorCreationException {
		File secretKeyFile = new File(filename); // private key file in PEM format
		PEMParser pemParser = new PEMParser(new FileReader(secretKeyFile));
		Object object = pemParser.readObject();
		PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder().build(password.toCharArray());
		JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
		KeyPair kp = converter.getKeyPair(((PEMEncryptedKeyPair) object).decryptKeyPair(decProv));
		return kp.getPrivate();
	}
}
