package hw6;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.kits.WalletAppKit;

public class SendMoneyBackToFaucet {
    public static String FAUCET_ADDRESS = "mv4rnyY3Su5gjcDNzbMLKBQkBicCtHUtFB";

    public static void main(String[] args) throws InsufficientMoneyException {
        WalletInitTest wit = new WalletInitTest();
        WalletAppKit kit = wit.getKit();
        Transaction transaction = kit.wallet().createSend(Address.fromBase58(kit.params(), FAUCET_ADDRESS), kit.wallet().getBalance().subtract(Coin.MILLICOIN));
        kit.wallet().commitTx(transaction);
        kit.peerGroup().broadcastTransaction(transaction);
        wit.monitor();
    }
}
