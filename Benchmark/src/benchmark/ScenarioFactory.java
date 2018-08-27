package benchmark;

import benchmark.core.ArgumentParsingException;
import benchmark.core.AvailableScenarios;

import java.lang.reflect.InvocationTargetException;

class ScenarioFactory {
    static Scenario createScenario(AvailableScenarios scenarioType, int roundsNo, String[] args) throws
            IllegalAccessException,
            InstantiationException,
            NoSuchMethodException,
            InvocationTargetException,
            ArgumentParsingException {
        Class<? extends Scenario> scenario =  Scenario.getScenarioClass(scenarioType);
        return scenario.getConstructor(int.class, String[].class).newInstance(roundsNo, args);
    }
}
