package homework3;

import java.security.KeyPair;
import java.util.List;
import java.util.Set;

public interface ScroogeCoinServer {

	public static final String KEY_ALGORITHM           = "ECDSA";
	public static final String SIGNATURE_ALGORITHM     = "SHA256withECDSA";
	public static final String PROVIDER                = "BC";
	public static final String CURVE_NAME              = "secp256k1";
	public static final String MESSAGE_DIGEST_ALGORITHM = "SHA-256";


	public void init(KeyPair scrooge);
	public List<HashPointer> epochHandler(List<Transaction> txs);
	public boolean isValid(Transaction tx);
	public Set<UTXO> getUTXOs();
}
