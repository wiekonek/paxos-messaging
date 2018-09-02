package benchmark;

import benchmark.core.ArgumentParsingException;
import benchmark.core.Logger;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import edu.put.paxosstm.messaging.core.utils.OracleInitializer;
import soa.paxosstm.common.StorageException;
import soa.paxosstm.dstm.PaxosSTM;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ScenarioRunner {
    public static void main(String[] args) {
        BenchmarkArgumentParser benchmarkArgs;
        try {
            benchmarkArgs = new BenchmarkArgumentParser(args);
        } catch (ArgumentParsingException | IOException e) {
            System.err.println("Benchmark argument parsing exception");
            e.printStackTrace();
            return;
        }
        Logger.logType = benchmarkArgs.getLogType();

        PaxosSTM paxos = PaxosSTM.getInstance();
        try {
            paxos.start();
        } catch (StorageException e) {
            System.err.println("Storage exception");
            e.printStackTrace();
            return;
        }

        OracleInitializer.initDefferedOracle();

        Scenario scenario = null;
        try {
            scenario = ScenarioFactory.createScenario(
                    benchmarkArgs.getScenarioType(),
                    benchmarkArgs.getRoundsNo(),
                    benchmarkArgs.getScenarioArgs()
            );
        } catch (IllegalAccessException |
                InstantiationException |
                NoSuchMethodException |
                InvocationTargetException |
                ArgumentParsingException e) {
            System.err.println("Scenario creation exception");
            e.printStackTrace();
            return;
        }

        try {
            scenario.run();
        } catch (MessagingException e) {
            System.err.println("Messaging exception");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

        System.gc();
    }

}
