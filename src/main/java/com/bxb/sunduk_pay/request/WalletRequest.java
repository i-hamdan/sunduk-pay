package com.bxb.sunduk_pay.request;

import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.util.TransactionType;
import lombok.Data;
import lombok.NonNull;

@Data
public class WalletRequest {
    @NonNull
    private String uuid; //  User ka unique identifier (wallet fetch/update ke liye)
    private String WalletId; // Wallet ka unique ID (jisme SubWallets hain)
    private String subWalletName; //  Source SubWallet ka naam (kabhi kabhi create ya update me)
    private Double amount; //  Transfer amount ya subwallet me paisa add/update karne ke liye
    //private SubWalletType subWalletType; //  (Optional) Subwallet ka type agar categorize karte ho
    @NonNull
    private RequestType requestType; //  Factory pattern me operation identify karne ke liye (CREATE, UPDATE, etc.)
    private TransactionType transactionType; //  Kis type ka transaction ho raha hai (TRANSFER, RECHARGE, etc.)
    private Double targetBalance; //  Subwallet create/update karte waqt goal/limit amount set karne ke liye
    private String subWalletId; //  Source SubWallet ID (update, transfer, etc. ke liye)
    private String targetSubwalletId; //  Destination SubWallet ID (transfer me paisa yahan add hoga)
}
