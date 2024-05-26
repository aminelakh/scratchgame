package com.cyberspeed.scratchgame.service;

import com.cyberspeed.scratchgame.dto.Config;
import org.apache.commons.cli.*;

import java.io.IOException;

/**
 *
 */
public class Runner {

    public static void main(String[] args) {

        Options options = new Options();
        Option configArg = new Option("config","config",true,"Json configuration");
        Option betAmountArg = new Option("betting-amount","betting-amount",true,"Amount to bet");
        configArg.setRequired(true);
        betAmountArg.setRequired(true);
        options.addOption(configArg);
        options.addOption(betAmountArg);
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        Config config;

        try {
            cmd = parser.parse(options, args);
            String configArgValue = cmd.getOptionValue("config");
            String betAmountArgValue = cmd.getOptionValue("betting-amount");

            if(configArgValue !=null){
                config = Config.loadConfig(configArgValue);
            }else{
                config = Config.loadConfig("src/main/resources/config.json");
            }

            ScratchGame scratchGame=new ScratchGame(config);

            /* I can add more validation here to not bet a non numeric or a negative amount*/
            if (betAmountArgValue !=null){
                scratchGame.bet(Double.valueOf(betAmountArgValue));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
