package homework5;

import org.bitcoinj.params.TestNet3Params;

public class WalletInitTest extends WalletInit {
    public static final String NAME = "test";

    public WalletInitTest() {
        super(TestNet3Params.get(), NAME);
    }

    public static void main(String[] args) throws InterruptedException {
        new WalletInitTest().monitor();
    	//new WalletInitTest();
    }
}