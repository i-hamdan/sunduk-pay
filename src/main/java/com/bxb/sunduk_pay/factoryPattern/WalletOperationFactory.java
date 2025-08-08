package com.bxb.sunduk_pay.factoryPattern;

import com.bxb.sunduk_pay.util.RequestType;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WalletOperationFactory {
private final List<WalletOperation> walletOperations;
@Autowired
    public WalletOperationFactory(List<WalletOperation> walletOperations) {
        this.walletOperations = walletOperations;
    }
    Map<RequestType, WalletOperation>walletServiceMap = new HashMap<>();

    @PostConstruct
private void putValues(){
for (WalletOperation service: walletOperations){
    walletServiceMap.put(service.getRequestType(),service);
}
}
public WalletOperation getWalletService (RequestType requestType){
return walletServiceMap.get(requestType);
}

}
