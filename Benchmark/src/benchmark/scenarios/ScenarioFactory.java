package benchmark.scenarios;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ScenarioFactory {
    public static Scenario createScenarioFromSimpleName(String scenarioSimpleName, String params) throws ClassNotFoundException, IllegalAccessException, IOException, InstantiationException {

        if (scenarioSimpleName.equals(MessagingSystemScenario.class.getSimpleName())) {
            return new MessagingSystemScenario(params);
        }

        throw new ClassNotFoundException();
    }

    public static Scenario createScenario(String scenarioClassName, String params) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> c = Class.forName(scenarioClassName);
        return (Scenario) c.getConstructor(String.class).newInstance(params);
    }
}
