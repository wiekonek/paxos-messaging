package benchmark;

import benchmark.config.BasicScenarioParameters;
import benchmark.scenarios.ProdConsScenario;

import java.io.IOException;


public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: [nodesNo, scenarioNo]");
            return;
        }
        System.out.printf("Running with: [nodesNo=%s scenarioNo=%s]\n", args[0], args[1]);
        int nodesNo = Integer.parseInt(args[0]);
        int scenarioNo = Integer.parseInt(args[1]);

        Benchmark.setup();

        Class<?> scenarioClass;
        BasicScenarioParameters[] parameters;

        switch  (scenarioNo) {
            case 0:
                scenarioClass = ProdConsScenario.class;
                parameters = new BasicScenarioParameters[nodesNo];
                for (int i = 0; i < nodesNo; i++) {
                    parameters[i] = new BasicScenarioParameters(nodesNo, 3);
                }
                break;
            default:
                System.out.println("No scenario with such a number");
                return;
        }

        Benchmark.run(nodesNo, scenarioClass, parameters);
    }

}

