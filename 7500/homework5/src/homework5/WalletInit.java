package homework5;


import org.bitcoinj.core.Address;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.listeners.TransactionConfidenceEventListener;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.WalletTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public abstract class WalletInit {
    public static final String WALLETS_DIR = "/Users/aliceliang/Documents/7500";

    private final WalletAppKit kit;
    private final NetworkParameters parameters;
    protected final static Logger LOGGER = LoggerFactory.getLogger(WalletInit.class);

    public InetAddress[] getInetAddresses(String dn) {
        InetAddress[] inetAddressArray = null;
        try {
            return InetAddress.getAllByName(dn);
        } catch (UnknownHostException ex) {

        }
        return null;
    }

    public WalletInit(NetworkParameters parameters, String walletName) {
        if (WALLETS_DIR == null) {
            LOGGER.info("Failed: Directory for wallet has not been set.");
            System.exit(1);
        }
        File walletDir = new File(WALLETS_DIR + File.separator + walletName);

        LOGGER.info("DNS look up to find peers");
        List<InetAddress> addrs = new ArrayList();
        for (String dn : new String [] {/*"eligius.st", "test-insight.bitpay.com", "insight.bitpay.com",*/ /*"testnet-seed.bitcoin.jonasschnelli.ch", */ "seed.tbtc.petertodd.org" /*, "testnet-seed.bluematt.me"/*, "testnet-seed.bitcoin.schildbach.de"*/}) {
            InetAddress[] inetAddressArray = getInetAddresses(dn);
            if (inetAddressArray == null) {
                LOGGER.warn("Cannot find IP address for " + dn);
                continue;
            }
            for (int i = 0; i < inetAddressArray.length; i++) {
                InetAddress addr = inetAddressArray[i];
                if (addr instanceof Inet4Address) {
                    printAddrInfo(dn + " #" + (i + 1), addr);
                    addrs.add(addr);
                }
            }
        }


        this.parameters = parameters;
        kit = new WalletAppKit(parameters, walletDir, walletName) {
            @Override
            protected void onSetupCompleted() {
                super.onSetupCompleted();

                LOGGER.info("Adding " + addrs.size() + " addresses to peer group.");
                for (InetAddress addr : addrs) {
                    kit.peerGroup().addAddress(addr);
                }
            }
        };

        LOGGER.info("Starting to sync blockchain. This might take a few minutes");
        kit.setAutoSave(true);
        kit.startAsync();
        kit.awaitRunning();

        kit.wallet().addTransactionConfidenceEventListener(new TransactionConfidenceEventListener() {
            @Override
            public void onTransactionConfidenceChanged(Wallet wallet, Transaction tx) {
                LOGGER.info("----------------------------------------------------------------------------------");
                LOGGER.info("Detected transaction confidence change: TxHash=" + tx.getHashAsString() + " numBroadcastPeers=" + tx.getConfidence().numBroadcastPeers() + " Confidence: \"" + tx.getConfidence() + "\"");
            }
        });
        kit.wallet().allowSpendingUnconfirmedTransactions();
        LOGGER.info("Synced blockchain.");
        LOGGER.info("You've got {} in your pocket", kit.wallet().getBalance().toFriendlyString());


        LOGGER.info("Successfully initialized/loaded wallet: " + walletDir);
        Address current = kit.wallet().currentReceiveAddress();
        LOGGER.info("Current receive: " + current.toBase58());
        LOGGER.info("All watched addresses: " + kit.wallet().getWatchedAddresses());
        for (Address addr : kit.wallet().getWatchedAddresses()) {
            LOGGER.info(addr.toBase58());
        }


    }

    public static void printAddrInfo(String whichHost, InetAddress inetAddress) {
        System.out.println("--------------------------");
        System.out.println("Which Host:" + whichHost);
        System.out.println("Canonical Host Name:" + inetAddress.getCanonicalHostName());
        System.out.println("Host Name:" + inetAddress.getHostName());
        System.out.println("Host Address:" + inetAddress.getHostAddress());
    }

    public void monitor() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm z");

        while (true) {
            LOGGER.info("----------------------------------------------------------------------------------");
            LOGGER.info("Printing all transactions on network that are associated with keys in your wallet: " + f.format(new Date()));
            List<WalletTransaction> txs = new ArrayList();
            for (WalletTransaction t : kit.wallet().getWalletTransactions()) {
                txs.add(t);
            }
            Collections.sort(txs, (o1,o2) ->o1.getTransaction().getUpdateTime().compareTo(o2.getTransaction().getUpdateTime()));
            int i = 1;
            for (WalletTransaction t : txs) {
                LOGGER.info("TxHash=" + t.getTransaction().getHashAsString() + " LastUpdated=[" + f.format(t.getTransaction().getUpdateTime()) + "] Confidence: \"" + t.getTransaction().getConfidence() + "\"");
                i++;
            }
            LOGGER.info("----------------------------------------------------------------------------------");
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public WalletAppKit getKit() {
        return kit;
    }

    public Wallet getWallet() {
        return kit.wallet();
    }

}
