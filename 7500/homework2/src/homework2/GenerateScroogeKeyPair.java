package homework2;

import java.io.FileWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;

import javax.xml.bind.DatatypeConverter;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMEncryptor;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.bouncycastle.openssl.jcajce.JcePEMEncryptorBuilder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCSException;

public class GenerateScroogeKeyPair {
	
	private static final String KEY_ALGORITHM           = "ECDSA";
	private static final String PROVIDER                = "BC";
	private static final String CURVE_NAME              = "secp256k1";

	private ECGenParameterSpec  ecGenSpec;
	private KeyPairGenerator    keyGen_;
	private SecureRandom        random;
	
	public void generateKeys(String keyname, String password) throws Exception{
		Security.addProvider(new BouncyCastleProvider());

		random = SecureRandom.getInstanceStrong();
		ecGenSpec = new ECGenParameterSpec(CURVE_NAME);
		keyGen_ = KeyPairGenerator.getInstance(KEY_ALGORITHM, PROVIDER);

		keyGen_.initialize(ecGenSpec, random);
		KeyPair kp = keyGen_.generateKeyPair();
		System.out.println("Key generation complete.");
		System.out.println();
		
		//get key pair
		PublicKey publicKey = kp.getPublic(); 
		PrivateKey secretKey = kp.getPrivate(); 
		
		//save public key
		String pkFilename = keyname + "_pk.pem";

		StringWriter sw = new StringWriter();
		JcaPEMWriter wr = new JcaPEMWriter(sw);
		wr.writeObject(kp.getPublic());
		wr.close();
		Writer fw = new FileWriter(pkFilename);
		fw.write(sw.toString());
		fw.close();
		System.out.println("Public Key:\n" + sw.toString());
		
		//encrypt and save private key
		String skFilename = keyname + "_sk.pem";
		storeSecretKeyToEncrypted(secretKey, skFilename, password);
	}

	public String storeSecretKeyToEncrypted(PrivateKey sk, String filename, String password) throws Exception {
		JcaPEMWriter privWriter = new JcaPEMWriter(new FileWriter(filename));
		PEMEncryptor penc = (new JcePEMEncryptorBuilder("AES-256-CFB"))
					.build(password.toCharArray());
		privWriter.writeObject(sk, penc);
		privWriter.close();
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		new GenerateScroogeKeyPair().generateKeys("scroogekey", "060313");
	}
}
