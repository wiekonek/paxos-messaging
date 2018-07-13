package edu.put.paxosstm.messaging.benchmark.scenarios;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ScenarioFactory {
    public static Scenario createScenarioFromSimpleName(String scenarioSimpleName, String params) throws ClassNotFoundException, IllegalAccessException, IOException, InstantiationException {

        if (scenarioSimpleName.equals(MessagingSystemScenario.class.getSimpleName())) {
            return new MessagingSystemScenario(params);
        }

        if (scenarioSimpleName.equals(SimpleScenario.class.getSimpleName())) {
            return new SimpleScenario(params);
        }

        if(scenarioSimpleName.equals(SimpleQueueScenario.class.getSimpleName())) {
            return new SimpleQueueScenario(params);
        }

        throw new ClassNotFoundException();
    }

    public static Scenario createScenario(String scenarioClassName, String params) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        String className = SimpleScenario.class.getName();
        Class<?> c = Class.forName(className);
        return (Scenario) c.getConstructor(String.class).newInstance(params);
    }
}
