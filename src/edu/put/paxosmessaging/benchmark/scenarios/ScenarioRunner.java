package edu.put.paxosmessaging.benchmark.scenarios;

import com.sun.jmx.snmp.Enumerated;
import soa.paxosstm.common.StorageException;
import soa.paxosstm.dstm.PaxosSTM;
import soa.paxosstm.dstm.internal.TransactionOracle;

import java.util.Arrays;
import java.util.Enumeration;

public class ScenarioRunner {
    public static void main(String[] args) throws StorageException, InterruptedException {
        PaxosSTM.getInstance().start();
//        System.out.println("Params: " + String.join(", ", args));
        try {
            String oracleParameter = args[0];
            initOracle(oracleParameter);
        }catch (Exception e) {
            e.printStackTrace();
        }

//        String className = SimpleScenario.class;
//        Class<?> c = Class.forName(className);
//        c.getConstructor( String.) .newInstance();

        // TODO: Prepare scenario factory instead of instantiating specific one scenario
//        Scenario scenario = new SimpleScenario(Arrays.copyOfRange(args, 1, args.length));
        Scenario scenario = new SimpleQueueScenario(null);
        scenario.run();


        System.gc();
        PaxosSTM.getInstance().enterBarrier("exit", PaxosSTM.getInstance().getNumberOfNodes());
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
