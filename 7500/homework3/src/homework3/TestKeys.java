package homework3;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.StringReader;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

import static homework3.ScroogeCoinServer.*;

/**
 * Created by sbugrara on 1/28/17.
 */
public class TestKeys {
	public static final String SCROOGE_SK_STR = "-----BEGIN EC PRIVATE KEY-----\n" +
			"Proc-Type: 4,ENCRYPTED\n" +
			"DEK-Info: AES-256-CFB,0587A246BBC0CAE2A613CEB217AAF7C0\n" +
			"\n" +
			"QytmaepLSuS3Ndm19aUVh/X5YJDTfzlFx7xxns0DQ/CNfZOMtcBKRZ8ve74ew+jJ\n" +
			"pD4yEIfqcnF+zG90Umz5W3++PYefpQFXSN13FY+E/aQRabomjodfy1y5korRC8Ms\n" +
			"0eMfTkmQM78FuSWx9sKjyg1DgJVpqw==\n" +
			"-----END EC PRIVATE KEY-----";
	public static final String SCROOGE_PK_STR = "-----BEGIN PUBLIC KEY-----\n" +
			"MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAErwwIT7QDDxQdSUzELjC1E6Cb3jXDaTxR\n" +
			"5kgL/vV1I5PAJ7TdihhQyXA+4cZVkuKhGLzZZwQW/KuMCPuZVpmNLw==\n" +
			"-----END PUBLIC KEY-----";

	public static final String USER1_SK_STR = "-----BEGIN EC PRIVATE KEY-----\n" +
			"Proc-Type: 4,ENCRYPTED\n" +
			"DEK-Info: AES-256-CFB,F323258DF1FD3543F9DF862446993F16\n" +
			"\n" +
			"q5WOmhDvqfuqrvsRpjF9TBiiBdklkqmHcIcBhjUJGtTLJ01twTXxUf213a5zXJ1a\n" +
			"S0ao4WXza0n+BJGdgQ7w7GUa5pwzIqC2zl4DRc/B42mxnjXXTTEM5wX9g7A9OjDw\n" +
			"28Xsugxz28H17riccPjm1aJnC+0jXw==\n" +
			"-----END EC PRIVATE KEY-----";
	public static final String USER1_PK_STR = "-----BEGIN PUBLIC KEY-----\n" +
			"MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEzGZxfxu4jKPjP87Xj7mXdK2RX9dKCfaE\n" +
			"5KNOiGpdAJNt+/xLh9KEJ71M+l8+KkEKmHsxNFIwoz85olFjqQdVlw==\n" +
			"-----END PUBLIC KEY-----";

	public static final String USER2_SK_STR = "-----BEGIN EC PRIVATE KEY-----\n" +
			"Proc-Type: 4,ENCRYPTED\n" +
			"DEK-Info: AES-256-CFB,7DDEC865BE0B29BD5B265C6C2183534D\n" +
			"\n" +
			"UXd0vv35JJhM2oUfcviuMWFGHPiYwTI0Zq1FnAhb2ZpVrLGxRQUXC66VJvuERm9o\n" +
			"Mhtcl9P0+t0PQt3qLmtC1y9adRqOE4JUyBjU1tHPMHI+/4Te4fcyzdZ0bji0KJBx\n" +
			"cmTgA7JWqXUe1bo/0EdIPLEZz8m8nQ==\n" +
			"-----END EC PRIVATE KEY-----";
	public static final String USER2_PK_STR = "-----BEGIN PUBLIC KEY-----\n" +
			"MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEwVBlrPEXFcSxMjtm4Qk15aMU4nODpjZr\n" +
			"yM5P//So6BoHeSC6ErkRG98cd9kr5OBHCQNU6XROKiwumxOFYePGOw==\n" +
			"-----END PUBLIC KEY-----";

	public static final String USER3_SK_STR = "-----BEGIN EC PRIVATE KEY-----\n" +
			"Proc-Type: 4,ENCRYPTED\n" +
			"DEK-Info: AES-256-CFB,7D8D0087F0DE54042DF53E79377FDDB2\n" +
			"\n" +
			"Go0tCvQsYjInes93xiWlB9Ed2ymO97F0zE0icGDgsFlqf1vG55PVp8XX/t2mkRT/\n" +
			"oMcQFGetM9OSf8hoBoYdk4U0JPAqgMzHaV/wAyCT3VqF4I35uzlXZ8JER+csnk0X\n" +
			"AWJC6U42OrCa5+nnmyIk3N3tfwIyeg==\n" +
			"-----END EC PRIVATE KEY-----";
	public static final String USER3_PK_STR = "-----BEGIN PUBLIC KEY-----\n" +
			"MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEJH0/tWJb2xVYIe1KmfZniqupUcsvoX+G\n" +
			"WHqRyVWYfjAHXLbNz4QbK3pgD/5hcwt42Q00FTsXfkrfThCwW3XV7g==\n" +
			"-----END PUBLIC KEY-----";

	public static final String USER4_SK_STR = "-----BEGIN EC PRIVATE KEY-----\n" +
			"Proc-Type: 4,ENCRYPTED\n" +
			"DEK-Info: AES-256-CFB,AE6D11D8A471D07266522876A54A382B\n" +
			"\n" +
			"+dFGlVD1XFMER12HWlk5bnHsI63e82+e0nvrlPE0o73/H6PapsmM9trXholgRv0x\n" +
			"VJVVo3C4PfU1ap3+igRuLiyKkHiUvMLOrRkg6NIcXvBIs5p/z6CGyroYnOM5rHr9\n" +
			"xQ1Xbu+KblCdXB2HDs4w/FUx4ZmbjQ==\n" +
			"-----END EC PRIVATE KEY-----";
	public static final String USER4_PK_STR = "-----BEGIN PUBLIC KEY-----\n" +
			"MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEvJdHudLgfesLVzpjCu9bL+e0/jNEOUl+\n" +
			"6Mw7i1phuIQjCwUMtvaiUqJkFkhtpd6rpW+L9WMJ5SzGy8EODYqWRw==\n" +
			"-----END PUBLIC KEY-----";

	public static final String USER5_SK_STR = "-----BEGIN EC PRIVATE KEY-----\n" +
			"Proc-Type: 4,ENCRYPTED\n" +
			"DEK-Info: AES-256-CFB,9748B0CB480CEFB54A186B2C51E0D7A9\n" +
			"\n" +
			"/FLZQoVDr7nlvZyDrgJUnEW0UNAxhGGyAbmGFCvOWqT2JOlRKhwcP/t/21SYJEAv\n" +
			"Wl9gdjFJIWD83BWwfpCyduLA4eFIB3a8sbG+UUZ0C9JWMA5OhRTBRAjdtiiLksWd\n" +
			"lDTaiNjHhfd0MrYZ2Wv9aHuDKn4d1Q==\n" +
			"-----END EC PRIVATE KEY-----";
	public static final String USER5_PK_STR = "-----BEGIN PUBLIC KEY-----\n" +
			"MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEWfxYZTtMpiNLJvG+W0Die5TaqTuGeUfq\n" +
			"s70SoVkI97urFkFZllfbHbEW+mrk3x1a1397y4xn422yFe4PtXhk4Q==\n" +
			"-----END PUBLIC KEY-----";


	public static final String PASSWORD = "123456";

	public static KeyPair getTestKeyPair(String pkstr, String skstr) throws Exception {
		PrivateKey sk = Util.loadSecretKeyFromEncrypted(new StringReader(skstr), TestKeys.PASSWORD);
		PublicKey pk = Util.loadPublicKey(new StringReader(pkstr));
		return new KeyPair(pk, sk);
	}

	public static void main(String[] args) throws Exception {
		Security.addProvider(new BouncyCastleProvider());

		String keynamePrefix = "user";
		int count = 5;
		String password = "123456";

		ECGenParameterSpec ecGenSpec = new ECGenParameterSpec(CURVE_NAME);
		KeyPairGenerator keyGen_ = KeyPairGenerator.getInstance(KEY_ALGORITHM, PROVIDER);
		SecureRandom random = SecureRandom.getInstanceStrong();

		keyGen_.initialize(ecGenSpec, random);

		for (int i = 0; i < count; i++) {
			KeyPair kp = keyGen_.generateKeyPair();
			PublicKey publicKey = kp.getPublic(); //"pk" == "public key"
			PrivateKey secretKey = kp.getPrivate(); //"sk" == "secret key" == "private key"

			System.out.println("USER" + i + ":");
			String skstr = Util.storeSecretKeyToEncrypted(secretKey, keynamePrefix + i + "_sk.pem", password);
			System.out.println(skstr);
			String pkstr = Util.storePublicKey(publicKey, keynamePrefix + i + "_pk.pem");
			System.out.println(pkstr);
			System.out.println();
		}
	}
}
