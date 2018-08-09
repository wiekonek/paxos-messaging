package benchmark.core;

import edu.put.paxosstm.messaging.core.utils.OracleInitializer;
import soa.paxosstm.dstm.PaxosSTM;

import java.util.Arrays;

public class ScenarioRunner {
    public static void main(String[] args) throws Exception {
        System.out.println("Params: " + String.join(", ", args));

        if(args.length == 0) throw new Exception("Params missing");
        if(args[0] == null) throw new Exception("Missing oracleParameter argument");
        if(args[1] == null) throw new Exception("Missing scenario name");

        PaxosSTM paxos = PaxosSTM.getInstance();
        paxos.start();
        paxos.enterBarrier("start-scenario", PaxosSTM.getInstance().getNumberOfNodes());

        OracleInitializer.initOracle(args[0]);

        Scenario scenario = ScenarioFactory.createScenario(args[1]);
        scenario.run(args.length > 2 ? Arrays.copyOfRange(args, 2, args.length) : new String[] {});

        System.gc();
    }

}
