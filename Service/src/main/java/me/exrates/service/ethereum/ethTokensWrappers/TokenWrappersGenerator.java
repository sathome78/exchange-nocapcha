package me.exrates.service.ethereum.ethTokensWrappers;

import org.web3j.codegen.SolidityFunctionWrapperGenerator;

/**
 * Created by Maks on 04.01.2018.
 */
public class TokenWrappersGenerator {

    public static void main(String[] args) throws Exception {
        SolidityFunctionWrapperGenerator.run(new String[]{
                "generate",
                "/home/yagi/crypto/eth/KWATT.bin",
                "/home/yagi/crypto/eth/KWATT.abi",
                "-o",
                "/home/yagi/workspace/becomeJavaSenior/exrates/Service/src/main/java",
                "-p",
                "me.exrates.service.ethereum.ethTokensWrappers"});
    }
}
