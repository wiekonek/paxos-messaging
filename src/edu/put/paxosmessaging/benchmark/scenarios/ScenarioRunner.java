package edu.put.paxosmessaging.benchmark.scenarios;

import soa.paxosstm.common.StorageException;
import soa.paxosstm.dstm.PaxosSTM;
import soa.paxosstm.dstm.internal.TransactionOracle;

import java.util.Arrays;

public class ScenarioRunner {
    static void main(String[] args) throws StorageException {

        PaxosSTM paxos = PaxosSTM.getInstance();
        paxos.start();

        try {
            String oracleParameter = args[0];
            initOracle(oracleParameter);
        }catch (Exception e) {
            e.printStackTrace();
        }

        // TODO: Prepare scenario factory instead of instantiating specific one
        Scenario scenario = new SimpleScenario(Arrays.copyOfRange(args, 1, args.length));
        scenario.run();

        Runtime.getRuntime().gc();
        paxos.enterBarrier("exit", PaxosSTM.getInstance().getNumberOfNodes());
        TransactionOracle.getInstance().printDebugStats();
    }

    private static void initOracle(String oracleType) {
        Class<?> c;
        String className = oracleType;
        if (!oracleType.contains("."))
            className = "soa.paxosstm.dstm.internal.TransactionOracle$" + oracleType;

        TransactionOracle oracle = null;
        try {
            c = Class.forName(className);
            oracle = (TransactionOracle) c.newInstance();
        } catch (ClassNotFoundException e) {
            System.err.println("Oracle " + className + " does not exist. Quitting");
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Oracle " + className + " cannot be instantiated. Stack trace:");
            e.printStackTrace();
            System.exit(1);
        }
        TransactionOracle.setInstance(oracle);
    }
}
