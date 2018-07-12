package edu.put.paxosstm.messaging.benchmark.scenarios;

import soa.paxosstm.dstm.PaxosSTM;
import soa.paxosstm.dstm.internal.TransactionOracle;

public class ScenarioRunner {
    public static void main(String[] args) throws Exception {
        PaxosSTM.getInstance().start();
        System.out.println("Params: " + String.join(", ", args));

        if(args[0] == null) throw new Exception("Missing oracleParameter argument");
        if(args[1] == null) throw new Exception("Missing params argument");

        String oracleParameter = args[0];
        initOracle(oracleParameter);

//        TODO: Prepare scenario factory instead of instantiating specific one scenario
//        String className = SimpleScenario.class;
//        Class<?> c = Class.forName(className);
//        c.getConstructor( String.) .newInstance();

//        Scenario scenario = new SimpleScenario(args[1]);
//        Scenario scenario = new SimpleQueueScenario(args[1]);
        Scenario scenario = new MessagingSystemScenario(args[1]);
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
