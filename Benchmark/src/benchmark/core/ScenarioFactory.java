package benchmark.core;

import java.lang.reflect.InvocationTargetException;

class ScenarioFactory {
    static Scenario createScenario(String scenarioClassName) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<?> c = Class.forName(scenarioClassName);
        return (Scenario) c.newInstance();
    }
}
