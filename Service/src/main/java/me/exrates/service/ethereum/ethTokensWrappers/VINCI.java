package me.exrates.service.ethereum.ethTokensWrappers;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.4.0.
 */
public class VINCI extends Contract implements ethTokenNotERC20{
    private static final String BINARY = "60806040526006805460ff1916905561001e33610024602090811b901c565b5061015f565b60035460408051808201909152601d81527f526571756972656d656e7420616c72656164792073617469736669656400000060208201526000916001600160a01b038481169116141561010f576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825283818151815260200191508051906020019080838360005b838110156100d45781810151838201526020016100bc565b50505050905090810190601f1680156101015780820380516001836020036101000a031916815260200191505b509250505060405180910390fd5b50600380546001600160a01b0319166001600160a01b0384169081179091556040517f167d3e9c1016ab80e58802ca9da10ce5c6a0f4debc46a2e7a2cd9e56899a4fb590600090a2506001919050565b6118558061016e6000396000f3fe608060405234801561001057600080fd5b506004361061018e5760003560e01c806379cc6790116100de578063aa271e1a11610097578063d73dd62311610071578063d73dd623146105fa578063d82f94a314610626578063dd62ed3e1461064c578063f46eccc41461067a5761018e565b8063aa271e1a14610453578063ab67aa5814610479578063be45fd621461053f5761018e565b806379cc6790146103a15780637d64bcb4146103cd5780638da5cb5b146103d557806395d89b41146103f9578063983b2d5614610401578063a9059cbb146104275761018e565b80632f54bf6e1161014b57806340c10f191161012557806340c10f191461030457806342966c6814610330578063661884631461034f57806370a082311461037b5761018e565b80632f54bf6e146102ce578063313ce567146102f4578063355274ea146102fc5761018e565b806305d2035b1461019357806306fdde03146101af578063095ea7b31461022c57806313af40351461025857806318160ddd1461027e57806323b872dd14610298575b600080fd5b61019b6106a0565b604080519115158252519081900360200190f35b6101b76106a9565b6040805160208082528351818301528351919283929083019185019080838360005b838110156101f15781810151838201526020016101d9565b50505050905090810190601f16801561021e5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b61019b6004803603604081101561024257600080fd5b506001600160a01b0381351690602001356106cd565b61019b6004803603602081101561026e57600080fd5b50356001600160a01b0316610733565b610286610811565b60408051918252519081900360200190f35b61019b600480360360608110156102ae57600080fd5b506001600160a01b03813581169160208101359091169060400135610817565b61019b600480360360208110156102e457600080fd5b50356001600160a01b0316610832565b610286610846565b61028661084b565b61019b6004803603604081101561031a57600080fd5b506001600160a01b03813516906020013561085b565b61034d6004803603602081101561034657600080fd5b50356108d0565b005b61019b6004803603604081101561036557600080fd5b506001600160a01b0381351690602001356108dd565b6102866004803603602081101561039157600080fd5b50356001600160a01b03166109cd565b61034d600480360360408110156103b757600080fd5b506001600160a01b0381351690602001356109e8565b61019b610a7e565b6103dd610b94565b604080516001600160a01b039092168252519081900360200190f35b6101b7610ba3565b61019b6004803603602081101561041757600080fd5b50356001600160a01b0316610bc7565b61019b6004803603604081101561043d57600080fd5b506001600160a01b038135169060200135610c51565b61019b6004803603602081101561046957600080fd5b50356001600160a01b0316610c68565b61019b6004803603608081101561048f57600080fd5b6001600160a01b038235811692602081013590911691604082013591908101906080810160608201356401000000008111156104ca57600080fd5b8201836020820111156104dc57600080fd5b803590602001918460018302840111640100000000831117156104fe57600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600092019190915250929550610c86945050505050565b61019b6004803603606081101561055557600080fd5b6001600160a01b038235169160208101359181019060608101604082013564010000000081111561058557600080fd5b82018360208201111561059757600080fd5b803590602001918460018302840111640100000000831117156105b957600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600092019190915250929550610d86945050505050565b61019b6004803603604081101561061057600080fd5b506001600160a01b038135169060200135610db5565b61019b6004803603602081101561063c57600080fd5b50356001600160a01b0316610e4e565b6102866004803603604081101561066257600080fd5b506001600160a01b0381358116916020013516610ed8565b61019b6004803603602081101561069057600080fd5b50356001600160a01b0316610f03565b60065460ff1681565b604051806040016040528060058152602001600160d81b6456696e63690281525081565b3360008181526002602090815260408083206001600160a01b038716808552908352818420869055815186815291519394909390927f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925928290030190a350600192915050565b600061073e33610832565b6040518060400160405280601a81526020016000805160206117ea833981519152815250906107ee57604051600160e51b62461bcd0281526004018080602001828103825283818151815260200191508051906020019080838360005b838110156107b357818101518382015260200161079b565b50505050905090810190601f1680156107e05780820380516001836020036101000a031916815260200191505b509250505060405180910390fd5b506001600160a01b03821661080257600080fd5b61080b82610f18565b50919050565b60015490565b6000606061082785858584610c86565b9150505b9392505050565b6003546001600160a01b0391821691161490565b601281565b6b04d8c55aefb8c05b5c00000081565b6005546000906b04d8c55aefb8c05b5c0000009061087f908463ffffffff610fff16565b11156108c65760408051600160e51b62461bcd02815260206004820152600b6024820152600160aa1b6a10d85c081c995858da195902604482015290519081900360640190fd5b61082b8383611012565b6108da33826111c9565b50565b3360009081526002602090815260408083206001600160a01b038616845290915281205480831115610932573360009081526002602090815260408083206001600160a01b0388168452909152812055610967565b610942818463ffffffff6112b816565b3360009081526002602090815260408083206001600160a01b03891684529091529020555b3360008181526002602090815260408083206001600160a01b0389168085529083529281902054815190815290519293927f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925929181900390910190a35060019392505050565b6001600160a01b031660009081526020819052604090205490565b6001600160a01b0382166000908152600260209081526040808320338452909152902054811115610a1857600080fd5b6001600160a01b0382166000908152600260209081526040808320338452909152902054610a4c908263ffffffff6112b816565b6001600160a01b0383166000908152600260209081526040808320338452909152902055610a7a82826111c9565b5050565b6000610a8933610832565b6040518060400160405280601a81526020016000805160206117ea83398151915281525090610afc57604051600160e51b62461bcd0281526020600482018181528351602484015283519092839260449091019190850190808383600083156107b357818101518382015260200161079b565b5060065460ff1615610b585760408051600160e51b62461bcd02815260206004820152601360248201527f4d696e74696e672069732066696e697368656400000000000000000000000000604482015290519081900360640190fd5b6006805460ff191660011790556040517fae5184fba832cb2b1f702aca6117b8d265eaf03ad33eb133f19dde0f5920fa0890600090a150600190565b6003546001600160a01b031681565b604051806040016040528060058152602001600160d81b6456494e43490281525081565b6000610bd233610832565b6040518060400160405280601a81526020016000805160206117ea83398151915281525090610c4557604051600160e51b62461bcd0281526020600482018181528351602484015283519092839260449091019190850190808383600083156107b357818101518382015260200161079b565b5061080b8260016112ca565b60006060610c60848483610d86565b949350505050565b6001600160a01b031660009081526004602052604090205460ff1690565b6001600160a01b0384166000908152600260209081526040808320338452909152812054831115610d015760408051600160e51b62461bcd02815260206004820152601560248201527f5265616368656420616c6c6f7765642076616c75650000000000000000000000604482015290519081900360640190fd5b6001600160a01b0385166000908152600260209081526040808320338452909152902054610d35908463ffffffff6112b816565b6001600160a01b0386166000908152600260209081526040808320338452909152902055610d6284611418565b15610d7a57610d738585858561141e565b9050610c60565b610d7385858585611651565b6000610d9184611418565b15610da957610da23385858561141e565b905061082b565b610da233858585611651565b3360009081526002602090815260408083206001600160a01b0386168452909152812054610de9908363ffffffff610fff16565b3360008181526002602090815260408083206001600160a01b0389168085529083529281902085905580519485525191937f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925929081900390910190a350600192915050565b6000610e5933610832565b6040518060400160405280601a81526020016000805160206117ea83398151915281525090610ecc57604051600160e51b62461bcd0281526020600482018181528351602484015283519092839260449091019190850190808383600083156107b357818101518382015260200161079b565b5061080b8260006112ca565b6001600160a01b03918216600090815260026020908152604080832093909416825291909152205490565b60046020526000908152604090205460ff1681565b60035460408051808201909152601d81527f526571756972656d656e7420616c72656164792073617469736669656400000060208201526000916001600160a01b0384811691161415610faf57604051600160e51b62461bcd0281526020600482018181528351602484015283519092839260449091019190850190808383600083156107b357818101518382015260200161079b565b50600380546001600160a01b0319166001600160a01b0384169081179091556040517f167d3e9c1016ab80e58802ca9da10ce5c6a0f4debc46a2e7a2cd9e56899a4fb590600090a2506001919050565b8181018281101561100c57fe5b92915050565b600061101d33610c68565b6040518060400160405280601a81526020016000805160206117ea8339815191528152509061109057604051600160e51b62461bcd0281526020600482018181528351602484015283519092839260449091019190850190808383600083156107b357818101518382015260200161079b565b5060065460ff16156110ec5760408051600160e51b62461bcd02815260206004820152601360248201527f4d696e74696e672069732066696e697368656400000000000000000000000000604482015290519081900360640190fd5b6005546110ff908363ffffffff610fff16565b600555600154611115908363ffffffff610fff16565b6001556001600160a01b038316600090815260208190526040902054611141908363ffffffff610fff16565b6001600160a01b03841660008181526020818152604091829020939093558051858152905191927f0f6798a560793a54c3bcfe86a93cde1e73087d944c0ea20544137d412139688592918290030190a26040805183815290516001600160a01b0385169160009160008051602061180a8339815191529181900360200190a350600192915050565b6001600160a01b0382166000908152602081905260409020548111156111ee57600080fd5b6001600160a01b038216600090815260208190526040902054611217908263ffffffff6112b816565b6001600160a01b038316600090815260208190526040902055600154611243908263ffffffff6112b816565b6001556040805182815290516001600160a01b038416917fcc16f5dbb4873280815c1ee09dbd06736cffcc184412cf7a71a0fdb75d397ca5919081900360200190a26040805182815290516000916001600160a01b0385169160008051602061180a8339815191529181900360200190a35050565b6000828211156112c457fe5b50900390565b6001600160a01b0382166000908152600460209081526040808320548151808301909252601d82527f526571756972656d656e7420616c726561647920736174697366696564000000928201929092529060ff161515831515141561137357604051600160e51b62461bcd0281526020600482018181528351602484015283519092839260449091019190850190808383600083156107b357818101518382015260200161079b565b506001600160a01b0383166000908152600460205260409020805460ff191683158015919091179091556113da576040516001600160a01b038416907f16baa937b08d58713325f93ac58b8a9369a4359bbefb4957d6d9b402735722ab90600090a261140f565b6040516001600160a01b038416907f4a59e6ea1f075b8fb09f3b05c8b3e9c68b31683a887a4d692078957c58a12be390600090a25b50600192915050565b3b151590565b600061142b858585611764565b61147f5760408051600160e51b62461bcd02815260206004820152601660248201527f4d6f7665206973206e6f74207375636365737366756c00000000000000000000604482015290519081900360640190fd5b836001600160a01b031663c0ee0b8a8685856040518463ffffffff1660e01b815260040180846001600160a01b03166001600160a01b0316815260200183815260200180602001828103825283818151815260200191508051906020019080838360005b838110156114fb5781810151838201526020016114e3565b50505050905090810190601f1680156115285780820380516001836020036101000a031916815260200191505b50945050505050600060405180830381600087803b15801561154957600080fd5b505af115801561155d573d6000803e3d6000fd5b50506040805186815290516001600160a01b0380891694508916925060008051602061180a8339815191529181900360200190a3836001600160a01b0316856001600160a01b03167fe19260aff97b920c7df27010903aeb9c8d2be5d310a2c67824cf3f15396e4c1685856040518083815260200180602001828103825283818151815260200191508051906020019080838360005b8381101561160b5781810151838201526020016115f3565b50505050905090810190601f1680156116385780820380516001836020036101000a031916815260200191505b50935050505060405180910390a3506001949350505050565b600061165e858585611764565b6116b25760408051600160e51b62461bcd02815260206004820152601660248201527f4d6f7665206973206e6f74207375636365737366756c00000000000000000000604482015290519081900360640190fd5b836001600160a01b0316856001600160a01b031660008051602061180a833981519152856040518082815260200191505060405180910390a3836001600160a01b0316856001600160a01b03167fe19260aff97b920c7df27010903aeb9c8d2be5d310a2c67824cf3f15396e4c1685856040518083815260200180602001828103825283818151815260200191508051906020019080838360008381101561160b5781810151838201526020016115f3565b600081611770856109cd565b101561177b57600080fd5b61179482611788866109cd565b9063ffffffff6112b816565b6001600160a01b0385166000908152602081905260409020556117c6826117ba856109cd565b9063ffffffff610fff16565b6001600160a01b038416600090815260208190526040902055506001939250505056fe446f65736e2774206861766520656e6f75676820726967687473000000000000ddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3efa165627a7a723058201625e9496784afda1543964f9d77ad6cc53a90856ac59bdb1c4d5bb5122149ae0029";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_APPROVEANDCALL = "approveAndCall";

    public static final String FUNC_VERSION = "version";

    public static final Event TRANSFER_EVENT = new Event("Transfer", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event APPROVAL_EVENT = new Event("Approval", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    protected VINCI(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected VINCI(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<String> name() {
        final Function function = new Function(FUNC_NAME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> symbol() {
        final Function function = new Function(FUNC_SYMBOL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> decimals() {
        final Function function = new Function(FUNC_DECIMALS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> totalSupply() {
        final Function function = new Function(FUNC_TOTALSUPPLY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> balanceOf(String _owner) {
        final Function function = new Function(FUNC_BALANCEOF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> transfer(String _to, BigInteger _value) {
        final Function function = new Function(
                FUNC_TRANSFER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> transferFrom(String _from, String _to, BigInteger _value) {
        final Function function = new Function(
                FUNC_TRANSFERFROM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_from), 
                new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> approve(String _spender, BigInteger _value) {
        final Function function = new Function(
                FUNC_APPROVE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_spender), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> allowance(String _owner, String _spender) {
        final Function function = new Function(FUNC_ALLOWANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner), 
                new org.web3j.abi.datatypes.Address(_spender)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<TransferEventResponse> transferEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, TransferEventResponse>() {
            @Override
            public TransferEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(TRANSFER_EVENT, log);
                TransferEventResponse typedResponse = new TransferEventResponse();
                typedResponse.log = log;
                typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<TransferEventResponse> transferEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));
        return transferEventObservable(filter);
    }

    public List<ApprovalEventResponse> getApprovalEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(APPROVAL_EVENT, transactionReceipt);
        ArrayList<ApprovalEventResponse> responses = new ArrayList<ApprovalEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ApprovalEventResponse typedResponse = new ApprovalEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._spender = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<ApprovalEventResponse> approvalEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, ApprovalEventResponse>() {
            @Override
            public ApprovalEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(APPROVAL_EVENT, log);
                ApprovalEventResponse typedResponse = new ApprovalEventResponse();
                typedResponse.log = log;
                typedResponse._owner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._spender = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse._value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<ApprovalEventResponse> approvalEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(APPROVAL_EVENT));
        return approvalEventObservable(filter);
    }

    public static RemoteCall<VINCI> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, BigInteger _initialAmount, String _tokenName, BigInteger _decimalUnits, String _tokenSymbol) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_initialAmount), 
                new org.web3j.abi.datatypes.Utf8String(_tokenName), 
                new org.web3j.abi.datatypes.generated.Uint8(_decimalUnits), 
                new org.web3j.abi.datatypes.Utf8String(_tokenSymbol)));
        return deployRemoteCall(VINCI.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<VINCI> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, BigInteger _initialAmount, String _tokenName, BigInteger _decimalUnits, String _tokenSymbol) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_initialAmount), 
                new org.web3j.abi.datatypes.Utf8String(_tokenName), 
                new org.web3j.abi.datatypes.generated.Uint8(_decimalUnits), 
                new org.web3j.abi.datatypes.Utf8String(_tokenSymbol)));
        return deployRemoteCall(VINCI.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public RemoteCall<TransactionReceipt> approveAndCall(String _spender, BigInteger _value, byte[] _extraData) {
        final Function function = new Function(
                FUNC_APPROVEANDCALL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_spender), 
                new org.web3j.abi.datatypes.generated.Uint256(_value), 
                new org.web3j.abi.datatypes.DynamicBytes(_extraData)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> version() {
        final Function function = new Function(FUNC_VERSION, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public static VINCI load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new VINCI(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static VINCI load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new VINCI(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class TransferEventResponse {
        public Log log;

        public String _from;

        public String _to;

        public BigInteger _value;
    }

    public static class ApprovalEventResponse {
        public Log log;

        public String _owner;

        public String _spender;

        public BigInteger _value;
    }
}
