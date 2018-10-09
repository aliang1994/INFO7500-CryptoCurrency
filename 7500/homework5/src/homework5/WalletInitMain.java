package homework5;

import org.bitcoinj.params.MainNetParams;

public class WalletInitMain extends WalletInit {
    public static final String NAME = "main";

    public WalletInitMain() {
        super(MainNetParams.get(), NAME);
    }

    public static void main(String[] args) {
        new WalletInitMain();
    }
}
