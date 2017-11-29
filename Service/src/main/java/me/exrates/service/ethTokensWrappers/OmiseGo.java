package me.exrates.service.ethTokensWrappers;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

/**
 * Auto generated code.<br>
 * <strong>Do not modify!</strong><br>
 * Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>, or {@link org.web3j.codegen.SolidityFunctionWrapperGenerator} to update.
 *
 * <p>Generated with web3j version 2.3.1.
 */
public final class OmiseGo extends Contract implements ethTokenERC20 {
    private static final String BINARY = "6003805460a060020a61ffff0219169055600060045560a0604052600860608190527f4f4d47546f6b656e000000000000000000000000000000000000000000000000608090815261005491600591906100c1565b506040805180820190915260038082527f4f4d4700000000000000000000000000000000000000000000000000000000006020909201918252610099916006916100c1565b5060126007555b60038054600160a060020a03191633600160a060020a03161790555b610161565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061010257805160ff191683800117855561012f565b8280016001018555821561012f579182015b8281111561012f578251825591602001919060010190610114565b5b5061013c929150610140565b5090565b61015e91905b8082111561013c5760008155600101610146565b5090565b90565b610f4c806101706000396000f300606060405236156100f95763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166305d2035b81146100fb57806306fdde031461011f578063095ea7b3146101af57806318160ddd146101d057806323b872dd146101f2578063313ce567146102195780633f4ba83a1461023b57806340c10f191461025f5780635c975abb1461029257806370a08231146102b65780637d64bcb4146102e45780638456cb59146103085780638da5cb5b1461032c57806395d89b4114610358578063a9059cbb146103e8578063c14a3b8c14610409578063dd62ed3e14610447578063f2fde38b1461047b575bfe5b341561010357fe5b61010b610499565b604080519115158252519081900360200190f35b341561012757fe5b61012f6104a9565b604080516020808252835181830152835191928392908301918501908083838215610175575b80518252602083111561017557601f199092019160209182019101610155565b505050905090810190601f1680156101a15780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156101b757fe5b6101ce600160a060020a0360043516602435610537565b005b34156101d857fe5b6101e06105d7565b60408051918252519081900360200190f35b34156101fa57fe5b6101ce600160a060020a03600435811690602435166044356105dd565b005b341561022157fe5b6101e0610607565b60408051918252519081900360200190f35b341561024357fe5b61010b61060d565b604080519115158252519081900360200190f35b341561026757fe5b61010b600160a060020a0360043516602435610695565b604080519115158252519081900360200190f35b341561029a57fe5b61010b610769565b604080519115158252519081900360200190f35b34156102be57fe5b6101e0600160a060020a0360043516610779565b60408051918252519081900360200190f35b34156102ec57fe5b61010b610798565b604080519115158252519081900360200190f35b341561031057fe5b61010b61080d565b604080519115158252519081900360200190f35b341561033457fe5b61033c61089a565b60408051600160a060020a039092168252519081900360200190f35b341561036057fe5b61012f6108a9565b604080516020808252835181830152835191928392908301918501908083838215610175575b80518252602083111561017557601f199092019160209182019101610155565b505050905090810190601f1680156101a15780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156103f057fe5b6101ce600160a060020a0360043516602435610937565b005b341561041157fe5b61033c600160a060020a036004351660243560443561095f565b60408051600160a060020a039092168252519081900360200190f35b341561044f57fe5b6101e0600160a060020a03600435811690602435166109f3565b60408051918252519081900360200190f35b341561048357fe5b6101ce600160a060020a0360043516610a20565b005b60035460a860020a900460ff1681565b6005805460408051602060026001851615610100026000190190941693909304601f8101849004840282018401909252818152929183018282801561052f5780601f106105045761010080835404028352916020019161052f565b820191906000526020600020905b81548152906001019060200180831161051257829003601f168201915b505050505081565b801580159061056a5750600160a060020a0333811660009081526002602090815260408083209386168352929052205415155b156105755760006000fd5b600160a060020a03338116600081815260026020908152604080832094871680845294825291829020859055815185815291517f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b9259281900390910190a35b5050565b60045481565b60035460a060020a900460ff16156105f55760006000fd5b610600838383610a79565b5b5b505050565b60075481565b60035460009033600160a060020a0390811691161461062c5760006000fd5b60035460a060020a900460ff1615156106455760006000fd5b6003805474ff0000000000000000000000000000000000000000191690556040517f7805862f689e2f13df9f062ff482ad3ad112aca9e0847911ed832e158c525b3390600090a15060015b5b5b90565b60035460009033600160a060020a039081169116146106b45760006000fd5b60035460a860020a900460ff16156106cc5760006000fd5b6004546106df908363ffffffff610b9d16565b600455600160a060020a03831660009081526001602052604090205461070b908363ffffffff610b9d16565b600160a060020a038416600081815260016020908152604091829020939093558051858152905191927f0f6798a560793a54c3bcfe86a93cde1e73087d944c0ea20544137d412139688592918290030190a25060015b5b5b92915050565b60035460a060020a900460ff1681565b600160a060020a0381166000908152600160205260409020545b919050565b60035460009033600160a060020a039081169116146107b75760006000fd5b6003805475ff000000000000000000000000000000000000000000191660a860020a1790556040517fae5184fba832cb2b1f702aca6117b8d265eaf03ad33eb133f19dde0f5920fa0890600090a15060015b5b90565b60035460009033600160a060020a0390811691161461082c5760006000fd5b60035460a060020a900460ff16156108445760006000fd5b6003805474ff0000000000000000000000000000000000000000191660a060020a1790556040517f6985a02210a168e66602d3235cb6db0e70f92b3ba4d376a33c0f3d9434bff62590600090a15060015b5b5b90565b600354600160a060020a031681565b6006805460408051602060026001851615610100026000190190941693909304601f8101849004840282018401909252818152929183018282801561052f5780601f106105045761010080835404028352916020019161052f565b820191906000526020600020905b81548152906001019060200180831161051257829003601f168201915b505050505081565b60035460a060020a900460ff161561094f5760006000fd5b6105d38282610bb9565b5b5b5050565b600354600090819033600160a060020a039081169116146109805760006000fd5b60035460a860020a900460ff16156109985760006000fd5b3085846109a3610cb1565b600160a060020a039384168152919092166020820152604080820192909252905190819003606001906000f08015156109d857fe5b90506109e48185610695565b508091505b5b5b509392505050565b600160a060020a038083166000908152600260209081526040808320938516835292905220545b92915050565b60035433600160a060020a03908116911614610a3c5760006000fd5b600160a060020a03811615610a74576003805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0383161790555b5b5b50565b600060606064361015610a8c5760006000fd5b600160a060020a038086166000908152600260209081526040808320338516845282528083205493881683526001909152902054909250610ad3908463ffffffff610b9d16565b600160a060020a038086166000908152600160205260408082209390935590871681522054610b08908463ffffffff610c8716565b600160a060020a038616600090815260016020526040902055610b31828463ffffffff610c8716565b600160a060020a038087166000818152600260209081526040808320338616845282529182902094909455805187815290519288169391927fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef929181900390910190a35b5b5050505050565b6000828201610bae84821015610ca0565b8091505b5092915050565b60406044361015610bca5760006000fd5b600160a060020a033316600090815260016020526040902054610bf3908363ffffffff610c8716565b600160a060020a033381166000908152600160205260408082209390935590851681522054610c28908363ffffffff610b9d16565b600160a060020a038085166000818152600160209081526040918290209490945580518681529051919333909316927fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef92918290030190a35b5b505050565b6000610c9583831115610ca0565b508082035b92915050565b801515610a745760006000fd5b5b50565b60405161025f80610cc28339019056006060604052341561000c57fe5b60405160608061025f8339810160409081528151602083015191909201515b4281116100385760006000fd5b60008054600160a060020a03808616600160a060020a031992831617909255600180549285169290911691909117905560028190555b5050505b6101de806100816000396000f300606060405263ffffffff7c01000000000000000000000000000000000000000000000000000000006000350416634e71d92d811461003a575bfe5b341561004257fe5b61004a61004c565b005b6001546000903373ffffffffffffffffffffffffffffffffffffffff9081169116146100785760006000fd5b6002544210156100885760006000fd5b6000805460408051602090810184905281517f70a0823100000000000000000000000000000000000000000000000000000000815273ffffffffffffffffffffffffffffffffffffffff3081166004830152925192909316936370a082319360248082019492918390030190829087803b151561010157fe5b6102c65a03f1151561010f57fe5b505060405151915050600081116101265760006000fd5b60008054600154604080517fa9059cbb00000000000000000000000000000000000000000000000000000000815273ffffffffffffffffffffffffffffffffffffffff9283166004820152602481018690529051919092169263a9059cbb926044808201939182900301818387803b151561019d57fe5b6102c65a03f115156101ab57fe5b5050505b505600a165627a7a723058204c37435b790331d68c36965a2a6a779ee7dfa9e5ef15513f34e6101f902c29460029a165627a7a72305820bd47f6ac2bce50684ad966d758abb0d15a897d3c3e985efd1d4b5ce46e7353360029";

    private OmiseGo(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    private OmiseGo(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<MintEventResponse> getMintEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Mint", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<MintEventResponse> responses = new ArrayList<MintEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            MintEventResponse typedResponse = new MintEventResponse();
            typedResponse.to = (Address) eventValues.getIndexedValues().get(0);
            typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(0);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<MintEventResponse> mintEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Mint", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, MintEventResponse>() {
            @Override
            public MintEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                MintEventResponse typedResponse = new MintEventResponse();
                typedResponse.to = (Address) eventValues.getIndexedValues().get(0);
                typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(0);
                return typedResponse;
            }
        });
    }

    public List<MintFinishedEventResponse> getMintFinishedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("MintFinished", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList());
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<MintFinishedEventResponse> responses = new ArrayList<MintFinishedEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            MintFinishedEventResponse typedResponse = new MintFinishedEventResponse();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<MintFinishedEventResponse> mintFinishedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("MintFinished", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList());
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, MintFinishedEventResponse>() {
            @Override
            public MintFinishedEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                MintFinishedEventResponse typedResponse = new MintFinishedEventResponse();
                return typedResponse;
            }
        });
    }

    public List<PauseEventResponse> getPauseEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Pause", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList());
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<PauseEventResponse> responses = new ArrayList<PauseEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            PauseEventResponse typedResponse = new PauseEventResponse();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<PauseEventResponse> pauseEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Pause", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList());
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, PauseEventResponse>() {
            @Override
            public PauseEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                PauseEventResponse typedResponse = new PauseEventResponse();
                return typedResponse;
            }
        });
    }

    public List<UnpauseEventResponse> getUnpauseEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Unpause", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList());
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<UnpauseEventResponse> responses = new ArrayList<UnpauseEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            UnpauseEventResponse typedResponse = new UnpauseEventResponse();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<UnpauseEventResponse> unpauseEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Unpause", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList());
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, UnpauseEventResponse>() {
            @Override
            public UnpauseEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                UnpauseEventResponse typedResponse = new UnpauseEventResponse();
                return typedResponse;
            }
        });
    }

    public List<ApprovalEventResponse> getApprovalEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Approval", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<ApprovalEventResponse> responses = new ArrayList<ApprovalEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            ApprovalEventResponse typedResponse = new ApprovalEventResponse();
            typedResponse.owner = (Address) eventValues.getIndexedValues().get(0);
            typedResponse.spender = (Address) eventValues.getIndexedValues().get(1);
            typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(0);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<ApprovalEventResponse> approvalEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Approval", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, ApprovalEventResponse>() {
            @Override
            public ApprovalEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                ApprovalEventResponse typedResponse = new ApprovalEventResponse();
                typedResponse.owner = (Address) eventValues.getIndexedValues().get(0);
                typedResponse.spender = (Address) eventValues.getIndexedValues().get(1);
                typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(0);
                return typedResponse;
            }
        });
    }

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Transfer", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.from = (Address) eventValues.getIndexedValues().get(0);
            typedResponse.to = (Address) eventValues.getIndexedValues().get(1);
            typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(0);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<TransferEventResponse> transferEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Transfer", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, TransferEventResponse>() {
            @Override
            public TransferEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                TransferEventResponse typedResponse = new TransferEventResponse();
                typedResponse.from = (Address) eventValues.getIndexedValues().get(0);
                typedResponse.to = (Address) eventValues.getIndexedValues().get(1);
                typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(0);
                return typedResponse;
            }
        });
    }

    public Future<Bool> mintingFinished() {
        Function function = new Function("mintingFinished", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<Utf8String> name() {
        Function function = new Function("name", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<TransactionReceipt> approve(Address _spender, Uint256 _value) {
        Function function = new Function("approve", Arrays.<Type>asList(_spender, _value), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public Future<Uint256> totalSupply() {
        Function function = new Function("totalSupply", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<TransactionReceipt> transferFrom(Address _from, Address _to, Uint256 _value) {
        Function function = new Function("transferFrom", Arrays.<Type>asList(_from, _to, _value), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public Future<Uint256> decimals() {
        Function function = new Function("decimals", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<TransactionReceipt> unpause() {
        Function function = new Function("unpause", Arrays.<Type>asList(), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public Future<TransactionReceipt> mint(Address _to, Uint256 _amount) {
        Function function = new Function("mint", Arrays.<Type>asList(_to, _amount), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public Future<Bool> paused() {
        Function function = new Function("paused", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<Uint256> balanceOf(Address _owner) {
        Function function = new Function("balanceOf", 
                Arrays.<Type>asList(_owner), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<TransactionReceipt> finishMinting() {
        Function function = new Function("finishMinting", Arrays.<Type>asList(), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public Future<TransactionReceipt> pause() {
        Function function = new Function("pause", Arrays.<Type>asList(), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public Future<Address> owner() {
        Function function = new Function("owner", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<Utf8String> symbol() {
        Function function = new Function("symbol", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<TransactionReceipt> transfer(Address _to, Uint256 _value) {
        Function function = new Function("transfer", Arrays.<Type>asList(_to, _value), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public Future<TransactionReceipt> mintTimelocked(Address _to, Uint256 _amount, Uint256 _releaseTime) {
        Function function = new Function("mintTimelocked", Arrays.<Type>asList(_to, _amount, _releaseTime), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public Future<Uint256> allowance(Address _owner, Address _spender) {
        Function function = new Function("allowance", 
                Arrays.<Type>asList(_owner, _spender), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public Future<TransactionReceipt> transferOwnership(Address newOwner) {
        Function function = new Function("transferOwnership", Arrays.<Type>asList(newOwner), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public static Future<OmiseGo> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, BigInteger initialWeiValue) {
        return deployAsync(OmiseGo.class, web3j, credentials, gasPrice, gasLimit, BINARY, "", initialWeiValue);
    }

    public static Future<OmiseGo> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, BigInteger initialWeiValue) {
        return deployAsync(OmiseGo.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "", initialWeiValue);
    }

    public static OmiseGo load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new OmiseGo(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static OmiseGo load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new OmiseGo(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class MintEventResponse {
        public Address to;

        public Uint256 value;
    }

    public static class MintFinishedEventResponse {
    }

    public static class PauseEventResponse {
    }

    public static class UnpauseEventResponse {
    }

    public static class ApprovalEventResponse {
        public Address owner;

        public Address spender;

        public Uint256 value;
    }

    public static class TransferEventResponse {
        public Address from;

        public Address to;

        public Uint256 value;
    }
}
