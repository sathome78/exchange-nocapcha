package me.exrates.service.ethTokensWrappers;

import org.glassfish.jersey.spi.Contract;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.concurrent.Future;

public interface ethTokenERC20 {

    Future<Uint256> balanceOf(Address param0);

    Future<TransactionReceipt> approve(Address _spender, Uint256 _value);

    Future<TransactionReceipt> transferFrom(Address _from, Address _to, Uint256 _value);

    Future<TransactionReceipt> transfer(Address _to, Uint256 _value);

    Future<Uint256> allowance(Address param0, Address param1);
}
