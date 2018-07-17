package benchmark.scenarios;

import edu.put.paxosstm.messaging.core.utils.OracleInitializer;
import soa.paxosstm.dstm.PaxosSTM;

public class ScenarioRunner {
    public static void main(String[] args) throws Exception {
        PaxosSTM.getInstance().start();
        System.out.println("Params: " + String.join(", ", args));

        if(args == null || args.length == 0) throw new Exception("Params missing");
        if(args[0] == null) throw new Exception("Missing oracleParameter argument");
        if(args[1] == null) throw new Exception("Missing scenario name");
        if(args[2] == null) throw new Exception("Missing params argument");

        OracleInitializer.initOracle(args[0]);

        Scenario scenario = ScenarioFactory.createScenarioFromSimpleName(args[1], args[2]);
        scenario.run();

        System.gc();
        PaxosSTM.getInstance().enterBarrier("exit", PaxosSTM.getInstance().getNumberOfNodes());
    }

}
