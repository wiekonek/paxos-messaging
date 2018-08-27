package benchmark;

import benchmark.core.ArgumentParsingException;
import benchmark.core.AvailableScenarios;
import benchmark.core.BenchmarkMessagingContext;
import benchmark.scenarios.ProdConsScenario;
import benchmark.scenarios.ProdConsTopicScenario;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import soa.paxosstm.dstm.PaxosSTM;

import java.io.IOException;

public abstract class Scenario {

    protected final BenchmarkMessagingContext messagingContext;
    protected final PaxosSTM paxos;
    protected final int nodeId;
    protected final String[] args;
    private final int roundsNo;

    public Scenario(int roundsNo, String[] args) {
        this.messagingContext = new BenchmarkMessagingContext();
        this.paxos = PaxosSTM.getInstance();
        this.nodeId = paxos.getId();
        this.roundsNo = roundsNo;
        this.args = args;
    }

    static Class<? extends Scenario> getScenarioClass(AvailableScenarios scenarioType) throws ArgumentParsingException {
        Class<? extends Scenario> scenarioClass;

        switch  (scenarioType) {
            case SimpleQueue:
                scenarioClass = ProdConsScenario.class;
                break;
            case SimpleTopic:
                scenarioClass = ProdConsTopicScenario.class;
                break;
            default:
                System.err.println("No scenario with such a number");
                throw new ArgumentParsingException();
        }
        return scenarioClass;
    }

    protected abstract void round() throws MessagingException;

    protected void barrier(String barrierName) {
        paxos.enterBarrier(barrierName, paxos.getNumberOfNodes());
    }

    void run() throws MessagingException {
        barrier("start-scenario");
        System.out.println("--- Start scenario benchmark!");
        for(int i = 0; i < roundsNo; i++) {
            barrier(String.format("start-round-%d", i));
            System.out.println(String.format("--- Start round %d", i));
            round();
            barrier(String.format("end-round-%d", i));
        }
        barrier("end-scenario");
        System.out.println("--- End benchmark!");
    }
}
