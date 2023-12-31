package me.exrates.service.ethereum.ethTokensWrappers;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint64;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
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
public class FST extends Contract implements ethTokenERC20{
    private static final String BINARY = "60806040526006805460a060020a62ffffff02191690553480156200002357600080fd5b5060068054600160a060020a031916331790556200004964010000000062000077810204565b620000717389cc23d79ef2b11e46b9ce72cccf6839fa6a43c864010000000062000235810204565b62000877565b6200008162000858565b6200008b62000858565b6200009562000858565b600654600090760100000000000000000000000000000000000000000000900460ff1615620000c357600080fd5b6006805460b060020a60ff021916760100000000000000000000000000000000000000000000179055505060408051602081810183527389cc23d79ef2b11e46b9ce72cccf6839fa6a43c8825282518082018452655af3107a40008152835191820190935260008082529194509192505b6001811015620001f2578181600181106200014b57fe5b60200201516001604060020a031615156200019e57620001978482600181106200017157fe5b60200201518483600181106200018357fe5b602002015164010000000062000264810204565b50620001e9565b620001e7848260018110620001af57fe5b6020020151848360018110620001c157fe5b6020020151848460018110620001d357fe5b602002015164010000000062000373810204565b505b60010162000134565b6200020564010000000062000546810204565b506040517f5daa87a0e9463431830481fd4b6e3403442dfb9a12b9c07597e9f61d50b633c890600090a150505050565b600654600160a060020a031633146200024d57600080fd5b6200026181640100000000620005e0810204565b50565b600654600090600160a060020a031633146200027f57600080fd5b60065474010000000000000000000000000000000000000000900460ff1615620002a857600080fd5b600154620002c59083640100000000620014676200065282021704565b600155600160a060020a038316600090815260208190526040902054620002fb9083640100000000620014676200065282021704565b600160a060020a0384166000818152602081815260409182902093909355805185815290519192600080516020620022cf83398151915292918290030190a2604080518381529051600160a060020a03851691600091600080516020620022af8339815191529181900360200190a350600192915050565b6006546000908190600160a060020a031633146200039057600080fd5b60065474010000000000000000000000000000000000000000900460ff1615620003b957600080fd5b600154620003d69085640100000000620014676200065282021704565b600155620003f7856001604060020a03851664010000000062000666810204565b600081815260046020526040902054909150620004239085640100000000620014676200065282021704565b600082815260046020908152604080832093909355600160a060020a0388168252600590522054620004649085640100000000620014676200065282021704565b600160a060020a0386166000908152600560205260409020556200049285846401000000006200069a810204565b604080518581529051600160a060020a03871691600080516020620022cf833981519152919081900360200190a2604080516001604060020a0385168152602081018690528151600160a060020a038816927f2ecd071e4d10ed2221b04636ed0724cce66a873aa98c1a31b4bb0e6846d3aab4928290030190a2604080518581529051600160a060020a038716913391600080516020620022af8339815191529181900360200190a3506001949350505050565b600654600090600160a060020a031633146200056157600080fd5b60065474010000000000000000000000000000000000000000900460ff16156200058a57600080fd5b6006805460a060020a60ff021916740100000000000000000000000000000000000000001790556040517fae5184fba832cb2b1f702aca6117b8d265eaf03ad33eb133f19dde0f5920fa0890600090a150600190565b600160a060020a0381161515620005f657600080fd5b600654604051600160a060020a038084169216907f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e090600090a360068054600160a060020a031916600160a060020a0392909216919091179055565b818101828110156200066057fe5b92915050565b6801000000000000000091909102177f57495348000000000000000000000000000000000000000000000000000000001790565b600080808080426001604060020a03871611620006b657600080fd5b620006d4876001604060020a03881664010000000062000666810204565b9450620006ec87600064010000000062000666810204565b6000818152600360205260409020549094506001604060020a031692508215156200073e57600084815260036020526040902080546001604060020a0319166001604060020a0388161790556200084f565b6200075c876001604060020a03851664010000000062000666810204565b91505b6001604060020a03831615801590620007895750826001604060020a0316866001604060020a0316115b15620007ce57506000818152600360205260409020549092506001604060020a0390811691839116620007c6878464010000000062000666810204565b91506200075f565b826001604060020a0316866001604060020a03161415620007ef576200084f565b6001604060020a038316156200082757600085815260036020526040902080546001604060020a0319166001604060020a0385161790555b600084815260036020526040902080546001604060020a0319166001604060020a0388161790555b50505050505050565b6020604051908101604052806001906020820280388339509192915050565b611a2880620008876000396000f3006080604052600436106101d65763ffffffff7c01000000000000000000000000000000000000000000000000000000006000350416623fd35a81146101db57806302d6f7301461020457806305d2035b1461024c57806306fdde0314610261578063095ea7b3146102eb5780630bb2cd6b1461030f578063158ef93e1461034057806317a950ac1461035557806318160ddd14610388578063188214001461039d57806323b872dd146103b25780632a905318146103dc578063313ce567146103f15780633be1e9521461041c5780633f4ba83a1461044f57806340c10f191461046457806342966c681461048857806356780085146104a05780635b7f415c146104b55780635be7fde8146104ca5780635c975abb146104df57806366188463146104f457806366a92cda1461051857806370a082311461052d578063715018a61461054e578063726a431a146105635780637d64bcb4146105945780638456cb59146105a95780638da5cb5b146105be57806395d89b41146105d3578063a9059cbb146105e8578063a9aad58c146101db578063ca63b5b81461060c578063cf3b19671461062d578063d73dd62314610642578063d8aeedf514610666578063dd62ed3e14610687578063f2fde38b146106ae575b600080fd5b3480156101e757600080fd5b506101f06106cf565b604080519115158252519081900360200190f35b34801561021057600080fd5b50610228600160a060020a03600435166024356106d4565b6040805167ffffffffffffffff909316835260208301919091528051918290030190f35b34801561025857600080fd5b506101f0610761565b34801561026d57600080fd5b50610276610771565b6040805160208082528351818301528351919283929083019185019080838360005b838110156102b0578181015183820152602001610298565b50505050905090810190601f1680156102dd5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b3480156102f757600080fd5b506101f0600160a060020a03600435166024356107a8565b34801561031b57600080fd5b506101f0600160a060020a036004351660243567ffffffffffffffff6044351661080e565b34801561034c57600080fd5b506101f06109ac565b34801561036157600080fd5b50610376600160a060020a03600435166109cf565b60408051918252519081900360200190f35b34801561039457600080fd5b506103766109e0565b3480156103a957600080fd5b506102766109e6565b3480156103be57600080fd5b506101f0600160a060020a0360043581169060243516604435610a1d565b3480156103e857600080fd5b50610276610a4a565b3480156103fd57600080fd5b50610406610a81565b6040805160ff9092168252519081900360200190f35b34801561042857600080fd5b5061044d600160a060020a036004351660243567ffffffffffffffff60443516610a86565b005b34801561045b57600080fd5b5061044d610bfa565b34801561047057600080fd5b506101f0600160a060020a0360043516602435610c73565b34801561049457600080fd5b5061044d600435610d6b565b3480156104ac57600080fd5b50610376610d78565b3480156104c157600080fd5b50610376610d7f565b3480156104d657600080fd5b50610376610d84565b3480156104eb57600080fd5b506101f0610de9565b34801561050057600080fd5b506101f0600160a060020a0360043516602435610df9565b34801561052457600080fd5b5061044d610ee9565b34801561053957600080fd5b50610376600160a060020a036004351661108c565b34801561055a57600080fd5b5061044d6110b5565b34801561056f57600080fd5b50610578611123565b60408051600160a060020a039092168252519081900360200190f35b3480156105a057600080fd5b506101f061113b565b3480156105b557600080fd5b5061044d6111bf565b3480156105ca57600080fd5b5061057861123d565b3480156105df57600080fd5b5061027661124c565b3480156105f457600080fd5b506101f0600160a060020a0360043516602435611283565b34801561061857600080fd5b50610376600160a060020a03600435166112ae565b34801561063957600080fd5b50610406610d7f565b34801561064e57600080fd5b506101f0600160a060020a0360043516602435611334565b34801561067257600080fd5b50610376600160a060020a03600435166113cd565b34801561069357600080fd5b50610376600160a060020a03600435811690602435166113e8565b3480156106ba57600080fd5b5061044d600160a060020a0360043516611413565b600081565b600080805b8360010181101561072d57600360006106fc878667ffffffffffffffff16611433565b815260208101919091526040016000205467ffffffffffffffff16925082151561072557610759565b6001016106d9565b60046000610745878667ffffffffffffffff16611433565b815260208101919091526040016000205491505b509250929050565b60065460a060020a900460ff1681565b60408051808201909152600581527f3169727374000000000000000000000000000000000000000000000000000000602082015290565b336000818152600260209081526040808320600160a060020a038716808552908352818420869055815186815291519394909390927f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925928290030190a350600192915050565b6006546000908190600160a060020a0316331461082a57600080fd5b60065460a060020a900460ff161561084157600080fd5b600154610854908563ffffffff61146716565b60015561086b8567ffffffffffffffff8516611433565b60008181526004602052604090205490915061088d908563ffffffff61146716565b600082815260046020908152604080832093909355600160a060020a03881682526005905220546108c4908563ffffffff61146716565b600160a060020a0386166000908152600560205260409020556108e78584611474565b604080518581529051600160a060020a038716917f0f6798a560793a54c3bcfe86a93cde1e73087d944c0ea20544137d4121396885919081900360200190a26040805167ffffffffffffffff85168152602081018690528151600160a060020a038816927f2ecd071e4d10ed2221b04636ed0724cce66a873aa98c1a31b4bb0e6846d3aab4928290030190a2604080518581529051600160a060020a0387169133916000805160206119dd8339815191529181900360200190a3506001949350505050565b600654760100000000000000000000000000000000000000000000900460ff1681565b60006109da8261160e565b92915050565b60015490565b60408051808201909152600581527f3169727374000000000000000000000000000000000000000000000000000000602082015281565b60065460009060a860020a900460ff1615610a3757600080fd5b610a42848484611629565b949350505050565b60408051808201909152600381527f4653540000000000000000000000000000000000000000000000000000000000602082015281565b600690565b6000600160a060020a0384161515610a9d57600080fd5b33600090815260208190526040902054831115610ab957600080fd5b33600090815260208190526040902054610ad9908463ffffffff61178e16565b33600090815260208190526040902055610afd8467ffffffffffffffff8416611433565b600081815260046020526040902054909150610b1f908463ffffffff61146716565b600082815260046020908152604080832093909355600160a060020a0387168252600590522054610b56908463ffffffff61146716565b600160a060020a038516600090815260056020526040902055610b798483611474565b604080518481529051600160a060020a0386169133916000805160206119dd8339815191529181900360200190a36040805167ffffffffffffffff84168152602081018590528151600160a060020a038716927f2ecd071e4d10ed2221b04636ed0724cce66a873aa98c1a31b4bb0e6846d3aab4928290030190a250505050565b600654600160a060020a03163314610c1157600080fd5b60065460a860020a900460ff161515610c2957600080fd5b6006805475ff000000000000000000000000000000000000000000191690556040517f7805862f689e2f13df9f062ff482ad3ad112aca9e0847911ed832e158c525b3390600090a1565b600654600090600160a060020a03163314610c8d57600080fd5b60065460a060020a900460ff1615610ca457600080fd5b600154610cb7908363ffffffff61146716565b600155600160a060020a038316600090815260208190526040902054610ce3908363ffffffff61146716565b600160a060020a03841660008181526020818152604091829020939093558051858152905191927f0f6798a560793a54c3bcfe86a93cde1e73087d944c0ea20544137d412139688592918290030190a2604080518381529051600160a060020a038516916000916000805160206119dd8339815191529181900360200190a350600192915050565b610d7533826117a0565b50565b620f424081565b600681565b6000806000610d943360006106d4565b67ffffffffffffffff909116925090505b8115801590610db357508142115b15610de457610dc0610ee9565b91820191610dcf3360006106d4565b67ffffffffffffffff90911692509050610da5565b505090565b60065460a860020a900460ff1681565b336000908152600260209081526040808320600160a060020a038616845290915281205480831115610e4e57336000908152600260209081526040808320600160a060020a0388168452909152812055610e83565b610e5e818463ffffffff61178e16565b336000908152600260209081526040808320600160a060020a03891684529091529020555b336000818152600260209081526040808320600160a060020a0389168085529083529281902054815190815290519293927f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925929181900390910190a35060019392505050565b6000806000806000610efc336000611433565b60008181526003602052604090205490955067ffffffffffffffff169350831515610f2657600080fd5b8367ffffffffffffffff164267ffffffffffffffff16111515610f4857600080fd5b610f5c338567ffffffffffffffff16611433565b600081815260036020908152604080832054600483528184208054908590553385529284905292205492955067ffffffffffffffff90911693509150610fa8908263ffffffff61146716565b3360009081526020818152604080832093909355600590522054610fd2908263ffffffff61178e16565b3360009081526005602052604090205567ffffffffffffffff82161515611015576000858152600360205260409020805467ffffffffffffffff1916905561104f565b600085815260036020526040808220805467ffffffffffffffff861667ffffffffffffffff19918216179091558583529120805490911690555b60408051828152905133917fb21fb52d5749b80f3182f8c6992236b5e5576681880914484d7f4c9b062e619e919081900360200190a25050505050565b600160a060020a0381166000908152600560205260408120546110ae8361160e565b0192915050565b600654600160a060020a031633146110cc57600080fd5b600654604051600160a060020a03909116907ff8df31144d9c2f0f6b59d69b8b98abd5459d07f2742c4df920b25aae33c6482090600090a26006805473ffffffffffffffffffffffffffffffffffffffff19169055565b7389cc23d79ef2b11e46b9ce72cccf6839fa6a43c881565b600654600090600160a060020a0316331461115557600080fd5b60065460a060020a900460ff161561116c57600080fd5b6006805474ff0000000000000000000000000000000000000000191660a060020a1790556040517fae5184fba832cb2b1f702aca6117b8d265eaf03ad33eb133f19dde0f5920fa0890600090a150600190565b600654600160a060020a031633146111d657600080fd5b60065460a860020a900460ff16156111ed57600080fd5b6006805475ff000000000000000000000000000000000000000000191660a860020a1790556040517f6985a02210a168e66602d3235cb6db0e70f92b3ba4d376a33c0f3d9434bff62590600090a1565b600654600160a060020a031681565b60408051808201909152600381527f4653540000000000000000000000000000000000000000000000000000000000602082015290565b60065460009060a860020a900460ff161561129d57600080fd5b6112a7838361188f565b9392505050565b600080600360006112c0856000611433565b815260208101919091526040016000205467ffffffffffffffff1690505b67ffffffffffffffff81161561132e576001909101906003600061130c8567ffffffffffffffff8516611433565b815260208101919091526040016000205467ffffffffffffffff1690506112de565b50919050565b336000908152600260209081526040808320600160a060020a0386168452909152812054611368908363ffffffff61146716565b336000818152600260209081526040808320600160a060020a0389168085529083529281902085905580519485525191937f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925929081900390910190a350600192915050565b600160a060020a031660009081526005602052604090205490565b600160a060020a03918216600090815260026020908152604080832093909416825291909152205490565b600654600160a060020a0316331461142a57600080fd5b610d758161195e565b6801000000000000000091909102177f57495348000000000000000000000000000000000000000000000000000000001790565b818101828110156109da57fe5b6000808080804267ffffffffffffffff87161161149057600080fd5b6114a4878767ffffffffffffffff16611433565b94506114b1876000611433565b60008181526003602052604090205490945067ffffffffffffffff169250821515611504576000848152600360205260409020805467ffffffffffffffff191667ffffffffffffffff8816179055611605565b611518878467ffffffffffffffff16611433565b91505b67ffffffffffffffff83161580159061154757508267ffffffffffffffff168667ffffffffffffffff16115b15611580575060008181526003602052604090205490925067ffffffffffffffff908116918391166115798784611433565b915061151b565b8267ffffffffffffffff168667ffffffffffffffff1614156115a157611605565b67ffffffffffffffff8316156115db576000858152600360205260409020805467ffffffffffffffff191667ffffffffffffffff85161790555b6000848152600360205260409020805467ffffffffffffffff191667ffffffffffffffff88161790555b50505050505050565b600160a060020a031660009081526020819052604090205490565b6000600160a060020a038316151561164057600080fd5b600160a060020a03841660009081526020819052604090205482111561166557600080fd5b600160a060020a038416600090815260026020908152604080832033845290915290205482111561169557600080fd5b600160a060020a0384166000908152602081905260409020546116be908363ffffffff61178e16565b600160a060020a0380861660009081526020819052604080822093909355908516815220546116f3908363ffffffff61146716565b600160a060020a03808516600090815260208181526040808320949094559187168152600282528281203382529091522054611735908363ffffffff61178e16565b600160a060020a03808616600081815260026020908152604080832033845282529182902094909455805186815290519287169391926000805160206119dd833981519152929181900390910190a35060019392505050565b60008282111561179a57fe5b50900390565b600160a060020a0382166000908152602081905260409020548111156117c557600080fd5b600160a060020a0382166000908152602081905260409020546117ee908263ffffffff61178e16565b600160a060020a03831660009081526020819052604090205560015461181a908263ffffffff61178e16565b600155604080518281529051600160a060020a038416917fcc16f5dbb4873280815c1ee09dbd06736cffcc184412cf7a71a0fdb75d397ca5919081900360200190a2604080518281529051600091600160a060020a038516916000805160206119dd8339815191529181900360200190a35050565b6000600160a060020a03831615156118a657600080fd5b336000908152602081905260409020548211156118c257600080fd5b336000908152602081905260409020546118e2908363ffffffff61178e16565b3360009081526020819052604080822092909255600160a060020a03851681522054611914908363ffffffff61146716565b600160a060020a038416600081815260208181526040918290209390935580518581529051919233926000805160206119dd8339815191529281900390910190a350600192915050565b600160a060020a038116151561197357600080fd5b600654604051600160a060020a038084169216907f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e090600090a36006805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a03929092169190911790555600ddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3efa165627a7a72305820b2434be8c722ea8fd97c4b44908d5b84292667feeb86f8e0b8194cf760b88ae70029ddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef0f6798a560793a54c3bcfe86a93cde1e73087d944c0ea20544137d4121396885";

    public static final String FUNC_CONTINUE_MINTING = "CONTINUE_MINTING";

    public static final String FUNC_GETFREEZING = "getFreezing";

    public static final String FUNC_MINTINGFINISHED = "mintingFinished";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_MINTANDFREEZE = "mintAndFreeze";

    public static final String FUNC_INITIALIZED = "initialized";

    public static final String FUNC_ACTUALBALANCEOF = "actualBalanceOf";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final String FUNC_TOKEN_NAME = "TOKEN_NAME";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_TOKEN_SYMBOL = "TOKEN_SYMBOL";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_FREEZETO = "freezeTo";

    public static final String FUNC_UNPAUSE = "unpause";

    public static final String FUNC_MINT = "mint";

    public static final String FUNC_BURN = "burn";

    public static final String FUNC_TOKEN_DECIMAL_MULTIPLIER = "TOKEN_DECIMAL_MULTIPLIER";

    public static final String FUNC_TOKEN_DECIMALS = "TOKEN_DECIMALS";

    public static final String FUNC_RELEASEALL = "releaseAll";

    public static final String FUNC_PAUSED_S = "paused";

    public static final String FUNC_DECREASEAPPROVAL = "decreaseApproval";

    public static final String FUNC_RELEASEONCE = "releaseOnce";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_TARGET_USER = "TARGET_USER";

    public static final String FUNC_FINISHMINTING = "finishMinting";

    public static final String FUNC_PAUSE = "pause";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_PAUSED = "PAUSED";

    public static final String FUNC_FREEZINGCOUNT = "freezingCount";

    public static final String FUNC_TOKEN_DECIMALS_UINT8 = "TOKEN_DECIMALS_UINT8";

    public static final String FUNC_INCREASEAPPROVAL = "increaseApproval";

    public static final String FUNC_FREEZINGBALANCEOF = "freezingBalanceOf";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final Event INITIALIZED_EVENT = new Event("Initialized", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList());
    ;

    public static final Event PAUSE_EVENT = new Event("Pause", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList());
    ;

    public static final Event UNPAUSE_EVENT = new Event("Unpause", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList());
    ;

    public static final Event BURN_EVENT = new Event("Burn", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event MINT_EVENT = new Event("Mint", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event MINTFINISHED_EVENT = new Event("MintFinished", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList());
    ;

    public static final Event OWNERSHIPRENOUNCED_EVENT = new Event("OwnershipRenounced", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList());
    ;

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList());
    ;

    public static final Event FREEZED_EVENT = new Event("Freezed", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint64>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event RELEASED_EVENT = new Event("Released", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event APPROVAL_EVENT = new Event("Approval", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event TRANSFER_EVENT = new Event("Transfer", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    protected FST(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected FST(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<Boolean> CONTINUE_MINTING() {
        final Function function = new Function(FUNC_CONTINUE_MINTING, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<Tuple2<BigInteger, BigInteger>> getFreezing(String _addr, BigInteger _index) {
        final Function function = new Function(FUNC_GETFREEZING, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_addr), 
                new org.web3j.abi.datatypes.generated.Uint256(_index)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint64>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple2<BigInteger, BigInteger>>(
                new Callable<Tuple2<BigInteger, BigInteger>>() {
                    @Override
                    public Tuple2<BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue());
                    }
                });
    }

    public RemoteCall<Boolean> mintingFinished() {
        final Function function = new Function(FUNC_MINTINGFINISHED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<String> name() {
        final Function function = new Function(FUNC_NAME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> approve(String _spender, BigInteger _value) {
        final Function function = new Function(
                FUNC_APPROVE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_spender), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> mintAndFreeze(String _to, BigInteger _amount, BigInteger _until) {
        final Function function = new Function(
                FUNC_MINTANDFREEZE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_amount), 
                new org.web3j.abi.datatypes.generated.Uint64(_until)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Boolean> initialized() {
        final Function function = new Function(FUNC_INITIALIZED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<BigInteger> actualBalanceOf(String _owner) {
        final Function function = new Function(FUNC_ACTUALBALANCEOF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> totalSupply() {
        final Function function = new Function(FUNC_TOTALSUPPLY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> TOKEN_NAME() {
        final Function function = new Function(FUNC_TOKEN_NAME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
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

    public RemoteCall<String> TOKEN_SYMBOL() {
        final Function function = new Function(FUNC_TOKEN_SYMBOL, 
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

    public RemoteCall<TransactionReceipt> freezeTo(String _to, BigInteger _amount, BigInteger _until) {
        final Function function = new Function(
                FUNC_FREEZETO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_amount), 
                new org.web3j.abi.datatypes.generated.Uint64(_until)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> unpause() {
        final Function function = new Function(
                FUNC_UNPAUSE, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> mint(String _to, BigInteger _amount) {
        final Function function = new Function(
                FUNC_MINT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> burn(BigInteger _value) {
        final Function function = new Function(
                FUNC_BURN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> TOKEN_DECIMAL_MULTIPLIER() {
        final Function function = new Function(FUNC_TOKEN_DECIMAL_MULTIPLIER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> TOKEN_DECIMALS() {
        final Function function = new Function(FUNC_TOKEN_DECIMALS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> releaseAll() {
        final Function function = new Function(
                FUNC_RELEASEALL, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Boolean> paused() {
        final Function function = new Function(FUNC_PAUSED_S,
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> decreaseApproval(String _spender, BigInteger _subtractedValue) {
        final Function function = new Function(
                FUNC_DECREASEAPPROVAL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_spender), 
                new org.web3j.abi.datatypes.generated.Uint256(_subtractedValue)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> releaseOnce() {
        final Function function = new Function(
                FUNC_RELEASEONCE, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> balanceOf(String _owner) {
        final Function function = new Function(FUNC_BALANCEOF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> renounceOwnership() {
        final Function function = new Function(
                FUNC_RENOUNCEOWNERSHIP, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> TARGET_USER() {
        final Function function = new Function(FUNC_TARGET_USER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> finishMinting() {
        final Function function = new Function(
                FUNC_FINISHMINTING, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> pause() {
        final Function function = new Function(
                FUNC_PAUSE, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> owner() {
        final Function function = new Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> symbol() {
        final Function function = new Function(FUNC_SYMBOL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> transfer(String _to, BigInteger _value) {
        final Function function = new Function(
                FUNC_TRANSFER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_to), 
                new org.web3j.abi.datatypes.generated.Uint256(_value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Boolean> PAUSED() {
        final Function function = new Function(FUNC_PAUSED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<BigInteger> freezingCount(String _addr) {
        final Function function = new Function(FUNC_FREEZINGCOUNT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_addr)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> TOKEN_DECIMALS_UINT8() {
        final Function function = new Function(FUNC_TOKEN_DECIMALS_UINT8, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> increaseApproval(String _spender, BigInteger _addedValue) {
        final Function function = new Function(
                FUNC_INCREASEAPPROVAL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_spender), 
                new org.web3j.abi.datatypes.generated.Uint256(_addedValue)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> freezingBalanceOf(String _owner) {
        final Function function = new Function(FUNC_FREEZINGBALANCEOF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> allowance(String _owner, String _spender) {
        final Function function = new Function(FUNC_ALLOWANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_owner), 
                new org.web3j.abi.datatypes.Address(_spender)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> transferOwnership(String _newOwner) {
        final Function function = new Function(
                FUNC_TRANSFEROWNERSHIP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_newOwner)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<FST> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(FST.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<FST> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(FST.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public List<InitializedEventResponse> getInitializedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(INITIALIZED_EVENT, transactionReceipt);
        ArrayList<InitializedEventResponse> responses = new ArrayList<InitializedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            InitializedEventResponse typedResponse = new InitializedEventResponse();
            typedResponse.log = eventValues.getLog();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<InitializedEventResponse> initializedEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, InitializedEventResponse>() {
            @Override
            public InitializedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(INITIALIZED_EVENT, log);
                InitializedEventResponse typedResponse = new InitializedEventResponse();
                typedResponse.log = log;
                return typedResponse;
            }
        });
    }

    public Observable<InitializedEventResponse> initializedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(INITIALIZED_EVENT));
        return initializedEventObservable(filter);
    }

    public List<PauseEventResponse> getPauseEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(PAUSE_EVENT, transactionReceipt);
        ArrayList<PauseEventResponse> responses = new ArrayList<PauseEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            PauseEventResponse typedResponse = new PauseEventResponse();
            typedResponse.log = eventValues.getLog();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<PauseEventResponse> pauseEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, PauseEventResponse>() {
            @Override
            public PauseEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(PAUSE_EVENT, log);
                PauseEventResponse typedResponse = new PauseEventResponse();
                typedResponse.log = log;
                return typedResponse;
            }
        });
    }

    public Observable<PauseEventResponse> pauseEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(PAUSE_EVENT));
        return pauseEventObservable(filter);
    }

    public List<UnpauseEventResponse> getUnpauseEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(UNPAUSE_EVENT, transactionReceipt);
        ArrayList<UnpauseEventResponse> responses = new ArrayList<UnpauseEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            UnpauseEventResponse typedResponse = new UnpauseEventResponse();
            typedResponse.log = eventValues.getLog();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<UnpauseEventResponse> unpauseEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, UnpauseEventResponse>() {
            @Override
            public UnpauseEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(UNPAUSE_EVENT, log);
                UnpauseEventResponse typedResponse = new UnpauseEventResponse();
                typedResponse.log = log;
                return typedResponse;
            }
        });
    }

    public Observable<UnpauseEventResponse> unpauseEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(UNPAUSE_EVENT));
        return unpauseEventObservable(filter);
    }

    public List<BurnEventResponse> getBurnEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(BURN_EVENT, transactionReceipt);
        ArrayList<BurnEventResponse> responses = new ArrayList<BurnEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            BurnEventResponse typedResponse = new BurnEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.burner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<BurnEventResponse> burnEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, BurnEventResponse>() {
            @Override
            public BurnEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(BURN_EVENT, log);
                BurnEventResponse typedResponse = new BurnEventResponse();
                typedResponse.log = log;
                typedResponse.burner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<BurnEventResponse> burnEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(BURN_EVENT));
        return burnEventObservable(filter);
    }

    public List<MintEventResponse> getMintEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(MINT_EVENT, transactionReceipt);
        ArrayList<MintEventResponse> responses = new ArrayList<MintEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            MintEventResponse typedResponse = new MintEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.to = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<MintEventResponse> mintEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, MintEventResponse>() {
            @Override
            public MintEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(MINT_EVENT, log);
                MintEventResponse typedResponse = new MintEventResponse();
                typedResponse.log = log;
                typedResponse.to = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<MintEventResponse> mintEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(MINT_EVENT));
        return mintEventObservable(filter);
    }

    public List<MintFinishedEventResponse> getMintFinishedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(MINTFINISHED_EVENT, transactionReceipt);
        ArrayList<MintFinishedEventResponse> responses = new ArrayList<MintFinishedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            MintFinishedEventResponse typedResponse = new MintFinishedEventResponse();
            typedResponse.log = eventValues.getLog();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<MintFinishedEventResponse> mintFinishedEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, MintFinishedEventResponse>() {
            @Override
            public MintFinishedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(MINTFINISHED_EVENT, log);
                MintFinishedEventResponse typedResponse = new MintFinishedEventResponse();
                typedResponse.log = log;
                return typedResponse;
            }
        });
    }

    public Observable<MintFinishedEventResponse> mintFinishedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(MINTFINISHED_EVENT));
        return mintFinishedEventObservable(filter);
    }

    public List<OwnershipRenouncedEventResponse> getOwnershipRenouncedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(OWNERSHIPRENOUNCED_EVENT, transactionReceipt);
        ArrayList<OwnershipRenouncedEventResponse> responses = new ArrayList<OwnershipRenouncedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OwnershipRenouncedEventResponse typedResponse = new OwnershipRenouncedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<OwnershipRenouncedEventResponse> ownershipRenouncedEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, OwnershipRenouncedEventResponse>() {
            @Override
            public OwnershipRenouncedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(OWNERSHIPRENOUNCED_EVENT, log);
                OwnershipRenouncedEventResponse typedResponse = new OwnershipRenouncedEventResponse();
                typedResponse.log = log;
                typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<OwnershipRenouncedEventResponse> ownershipRenouncedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OWNERSHIPRENOUNCED_EVENT));
        return ownershipRenouncedEventObservable(filter);
    }

    public List<OwnershipTransferredEventResponse> getOwnershipTransferredEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, transactionReceipt);
        ArrayList<OwnershipTransferredEventResponse> responses = new ArrayList<OwnershipTransferredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<OwnershipTransferredEventResponse> ownershipTransferredEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, OwnershipTransferredEventResponse>() {
            @Override
            public OwnershipTransferredEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, log);
                OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
                typedResponse.log = log;
                typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<OwnershipTransferredEventResponse> ownershipTransferredEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OWNERSHIPTRANSFERRED_EVENT));
        return ownershipTransferredEventObservable(filter);
    }

    public List<FreezedEventResponse> getFreezedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(FREEZED_EVENT, transactionReceipt);
        ArrayList<FreezedEventResponse> responses = new ArrayList<FreezedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            FreezedEventResponse typedResponse = new FreezedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.to = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.release = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<FreezedEventResponse> freezedEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, FreezedEventResponse>() {
            @Override
            public FreezedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(FREEZED_EVENT, log);
                FreezedEventResponse typedResponse = new FreezedEventResponse();
                typedResponse.log = log;
                typedResponse.to = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.release = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<FreezedEventResponse> freezedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(FREEZED_EVENT));
        return freezedEventObservable(filter);
    }

    public List<ReleasedEventResponse> getReleasedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(RELEASED_EVENT, transactionReceipt);
        ArrayList<ReleasedEventResponse> responses = new ArrayList<ReleasedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ReleasedEventResponse typedResponse = new ReleasedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<ReleasedEventResponse> releasedEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, ReleasedEventResponse>() {
            @Override
            public ReleasedEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(RELEASED_EVENT, log);
                ReleasedEventResponse typedResponse = new ReleasedEventResponse();
                typedResponse.log = log;
                typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<ReleasedEventResponse> releasedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(RELEASED_EVENT));
        return releasedEventObservable(filter);
    }

    public List<ApprovalEventResponse> getApprovalEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(APPROVAL_EVENT, transactionReceipt);
        ArrayList<ApprovalEventResponse> responses = new ArrayList<ApprovalEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ApprovalEventResponse typedResponse = new ApprovalEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
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
                typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<ApprovalEventResponse> approvalEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(APPROVAL_EVENT));
        return approvalEventObservable(filter);
    }

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
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
                typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<TransferEventResponse> transferEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));
        return transferEventObservable(filter);
    }

    public static FST load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new FST(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static FST load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new FST(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class InitializedEventResponse {
        public Log log;
    }

    public static class PauseEventResponse {
        public Log log;
    }

    public static class UnpauseEventResponse {
        public Log log;
    }

    public static class BurnEventResponse {
        public Log log;

        public String burner;

        public BigInteger value;
    }

    public static class MintEventResponse {
        public Log log;

        public String to;

        public BigInteger amount;
    }

    public static class MintFinishedEventResponse {
        public Log log;
    }

    public static class OwnershipRenouncedEventResponse {
        public Log log;

        public String previousOwner;
    }

    public static class OwnershipTransferredEventResponse {
        public Log log;

        public String previousOwner;

        public String newOwner;
    }

    public static class FreezedEventResponse {
        public Log log;

        public String to;

        public BigInteger release;

        public BigInteger amount;
    }

    public static class ReleasedEventResponse {
        public Log log;

        public String owner;

        public BigInteger amount;
    }

    public static class ApprovalEventResponse {
        public Log log;

        public String owner;

        public String spender;

        public BigInteger value;
    }

    public static class TransferEventResponse {
        public Log log;

        public String from;

        public String to;

        public BigInteger value;
    }
}
