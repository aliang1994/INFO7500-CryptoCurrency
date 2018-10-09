package homework2;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.*;
import org.bouncycastle.openssl.jcajce.*;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCSException;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;

public class CryptoReference2 {
	private static final String KEY_ALGORITHM           = "ECDSA";
	private static final String SIGNATURE_ALGORITHM     = "SHA256withECDSA";
	private static final String PROVIDER                = "BC";
	private static final String CURVE_NAME              = "secp256k1";

	private ECGenParameterSpec  ecGenSpec;
	private KeyPairGenerator    keyGen_;
	private SecureRandom        random;

	public void run(String keyname, String password) throws Exception{
		Security.addProvider(new BouncyCastleProvider());

		random = SecureRandom.getInstanceStrong();
		ecGenSpec = new ECGenParameterSpec(CURVE_NAME);
		keyGen_ = KeyPairGenerator.getInstance(KEY_ALGORITHM, PROVIDER);

		keyGen_.initialize(ecGenSpec, random);
		System.out.println("Generating key pair. Please wait....");
		KeyPair kp = keyGen_.generateKeyPair();
		System.out.println("Key generation complete.");
		System.out.println();
		
		PublicKey publicKey = kp.getPublic(); //"pk" == "public key"
		PrivateKey secretKey = kp.getPrivate(); //"sk" == "secret key" == "private key"

		{
			String pkFilename = keyname + "_pk.pem";

			StringWriter sw = new StringWriter();
			JcaPEMWriter wr = new JcaPEMWriter(sw);
			wr.writeObject(kp.getPublic());
			wr.close();
			Writer fw = new FileWriter(pkFilename);
			fw.write(sw.toString());
			fw.close();
			System.out.println("Public Key:\n" + sw.toString());
		}

		String skFilename = keyname + "_sk.pem";

		storeSecretKeyToEncrypted(secretKey, skFilename, password);
		PrivateKey recoveredKey = loadSecretKeyFromEncrypted(skFilename, password);

		System.out.println("secretKey=" + secretKey);
		System.out.println("secretKey.getAlgorithm()=" + secretKey.getAlgorithm());
		System.out.println("recoveredKey=" + secretKey);
		System.out.println("recoveredKey.getAlgorithm()=" + recoveredKey.getAlgorithm());
		System.out.println();

		if(secretKey.equals(recoveredKey))
			System.out.println("Key recovery ok");
		else
			System.err.println("Private key recovery failed");

		if(secretKey.getAlgorithm().equals(recoveredKey.getAlgorithm()))
			System.out.println("Key algorithm ok");
		else
			System.err.println("Key algorithms do not match"); 


		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		
		signature.initSign(secretKey, random);
		String messageStr1 = "Cryptocurrency is the future";
		byte[] message1 = messageStr1.getBytes(StandardCharsets.UTF_8);
		signature.update(message1);
		byte[] sigBytes1 = signature.sign();
		System.out.println("Signature: msg=" + messageStr1 + " sig.len=" + sigBytes1.length + " sig=" + DatatypeConverter.printHexBinary(sigBytes1));

		signature.initSign(secretKey, random);
		String messageStr2 = "Decentralize money";
		byte[] message2 = messageStr2.getBytes(StandardCharsets.UTF_8);
		signature.update(message2);
		byte[] sigBytes2 = signature.sign();
		System.out.println("Signature: msg=" + messageStr2 + " sig.len=" + sigBytes2.length + " sig=" + DatatypeConverter.printHexBinary(sigBytes2));

		signature.initVerify(publicKey);
		signature.update(message1);
		

		if (signature.verify(sigBytes1)) {
			System.out.println("SUCCESS: signature verification succeeded.");
		} else {
			System.out.println("FAILURE: signature verification failed.");
		}
		
		signature.initVerify(publicKey);
		signature.update(message2);

		if (signature.verify(sigBytes2)) {
			System.out.println("SUCCESS: signature verification succeeded.");
		} else {
			System.out.println("FAILURE: signature verification failed.");
		}
	}

	public String storeSecretKeyToEncrypted(PrivateKey sk, String filename, String password) throws Exception {
		JcaPEMWriter privWriter = new JcaPEMWriter(new FileWriter(filename));
		PEMEncryptor penc = (new JcePEMEncryptorBuilder("AES-256-CFB"))
					.build(password.toCharArray());
		privWriter.writeObject(sk, penc);
		privWriter.close();
		return null;
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

	public static void main(String[] args) throws Exception {
		new CryptoReference2().run("mykey", "123456");
	}
}