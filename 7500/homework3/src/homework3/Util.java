package homework3;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
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

import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

public class Util {

	public static byte[] toByteArray(List<Byte> blist) {
		byte[] barr = new byte[blist.size()];
		int i = 0;
		for(Byte b : blist)
			barr[i++] = b;
	   	return barr;
	}

	public static String storePublicKey(PublicKey pk, String filename) throws IOException {
		StringWriter sw = new StringWriter();
		try (JcaPEMWriter wr = new JcaPEMWriter(sw)) {
			wr.writeObject(pk);
		}

		String str = sw.toString();
		try (FileWriter fw = new FileWriter(filename)) {
			fw.write(str);
		}

		return str;
	}
	
	public static String storeSecretKeyToEncrypted(PrivateKey sk, String filename, String password) throws Exception {
		StringWriter sw = new StringWriter();
		JcaPEMWriter privWriter = new JcaPEMWriter(sw);
		PEMEncryptor penc = (new JcePEMEncryptorBuilder("AES-256-CFB"))
				.build(password.toCharArray());
		privWriter.writeObject(sk, penc);
		privWriter.close();

		String str = sw.toString();
		try (FileWriter fw = new FileWriter(filename)) {
			fw.write(str);
		};

		return str;
	}

	public static PrivateKey loadSecretKeyFromEncrypted(String filename, String password) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, PKCSException, OperatorCreationException {
		return loadSecretKeyFromEncrypted(new FileReader(filename), password);
	}

	public static PrivateKey loadSecretKeyFromEncrypted(Reader reader, String password) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, PKCSException, OperatorCreationException {
		PEMParser pemParser = new PEMParser(reader);
		Object object = pemParser.readObject();
		PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder().build(password.toCharArray());
		JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
		KeyPair kp = converter.getKeyPair(((PEMEncryptedKeyPair) object).decryptKeyPair(decProv));
		return kp.getPrivate();
	}

	public static PublicKey loadPublicKey(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, PKCSException, OperatorCreationException {
		return loadPublicKey(new FileReader(filename));
	}

	public static PublicKey loadPublicKey(Reader reader) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, PKCSException, OperatorCreationException {
		PEMParser pemParser = new PEMParser(reader);
		SubjectPublicKeyInfo object = (SubjectPublicKeyInfo) pemParser.readObject();
		JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
		return converter.getPublicKey(object);
	}

	public static byte[] sign(byte[] message, PrivateKey sk, String alg, SecureRandom random) {
		try {
			Signature signature = Signature.getInstance(alg);
			signature.initSign(sk, random);
			signature.update(message);
			return signature.sign();
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
}