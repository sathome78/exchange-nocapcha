package me.exrates.service.ethTokensWrappers;

import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.util.concurrent.Future;

public interface ethTokenNotERC20 {

    Future<Uint256> balanceOf(Address param0);

    Future<TransactionReceipt> transfer(Address _to, Uint256 _value);
}
