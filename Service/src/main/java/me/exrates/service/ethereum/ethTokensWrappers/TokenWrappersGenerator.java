package me.exrates.service.ethereum.ethTokensWrappers;

import org.web3j.codegen.SolidityFunctionWrapperGenerator;

/**
 * Created by Maks on 04.01.2018.
 */
public class TokenWrappersGenerator {

    public static void main(String[] args) throws Exception {
        SolidityFunctionWrapperGenerator.run(new String[]{
                "generate",
                "/Users/exrates/Vlad/ethereum/MTV.bin",
                "/Users/exrates/Vlad/ethereum/MTV.abi",
                "-o",
                "/Users/exrates/Vlad/exrates/Service/src/main/java",
                "-p",
                "me.exrates.service.ethereum.ethTokensWrappers"});
    }
}
