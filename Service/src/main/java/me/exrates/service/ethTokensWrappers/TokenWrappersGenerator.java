package me.exrates.service.ethTokensWrappers;

import org.web3j.codegen.SolidityFunctionWrapperGenerator;

/**
 * Created by Maks on 04.01.2018.
 */
public class TokenWrappersGenerator {

    public static void main(String[] args) throws Exception {
        SolidityFunctionWrapperGenerator.run(new String[]{
                "generate",
                "d:/eth/bnb.bin",
                "d:/eth/bnb.abi",
                "-o",
                "c:/Users/Maks/IdeaProjects/exrates/Service/src/main/java",
                "-p",
                "me.exrates.service.ethTokensWrappers"});
    }
}
