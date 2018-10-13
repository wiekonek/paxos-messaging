package edu.put.paxosstm.messaging.benchmark;

import edu.put.paxosstm.messaging.benchmark.core.ArgumentParsingException;
import edu.put.paxosstm.messaging.benchmark.core.AvailableScenarios;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;


class BenchmarkArgumentParserTest {

    private final static ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final static ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final static PrintStream originalOut = System.out;
    private final static PrintStream originalErr = System.err;

    @BeforeAll
    static void setup() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterAll
    static void restore() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    // Exceptions
    @Test
    void constructor_throwException_ifNullParam() {
        assertThrows(ArgumentParsingException.class, () -> new BenchmarkArgumentParser(null));
    }

    @Test
    void constructor_throwException_ifEmptyParams() throws IOException {
        assertThrows(ArgumentParsingException.class, () -> new BenchmarkArgumentParser(new String[] {}));
    }

    @ParameterizedTest(name="[{index}] args: {arguments}")
    @ValueSource(strings = {"--help", "-h"})
    void constructor_throwException_ifHelp(String args) throws IOException {
        assertBenchmarkThrowsParsingException(args);
    }

    @ParameterizedTest(name="[{index}] args: {arguments}")
    @ValueSource(strings = {
            "",
            "-s SimpleQueueScenario",
            "--scenario 0 --ssh --ssh-hosts=asd"
    })
    void constructor_throwException_ifMissingAnyRequiredArgs(String args) throws IOException {
        assertBenchmarkThrowsParsingException(args);
    }

    @Test
    void constructor_throwException_ifSshWithoutHosts() throws IOException {
        assertBenchmarkThrowsParsingException("--nodes=3 --ssh");
    }

    @ParameterizedTest(name="[{index}] args: {arguments}")
    @ValueSource(strings = {
            "--node=3 --ssh --ssh-hosts=host1",
            "--node=3 --ssh --ssh-hosts=host1,host2",
    })
    void constructor_throwException_ifNotEnoughHosts(String args) throws IOException {
        assertBenchmarkThrowsParsingException(args);
    }


    // Returning values
    @ParameterizedTest(name="[{index}] for args \"{0}\" should be \"{1}\"")
    @CsvSource({
            "--node 3, 3",
            "--node=1, 1",
            "-n 5, 5",
            "-n5, 5",
            "-n=8, 8",
    })
    void getNodesNo_returnNodesNumber_ifNodesParamInMultipleFormats(String args, int expectedValue) throws IOException, ArgumentParsingException {
        BenchmarkArgumentParser parser = new BenchmarkArgumentParser(toArgs(args));
        assertEquals(expectedValue, parser.getNodesNo());
    }

    @ParameterizedTest(name="[{index}] for args \"{0}\" should be \"{1}\"")
    @CsvSource({
            "-n3 --rounds 1, 1",
            "-n3 --rounds=100, 100",
            "-n3 -r 999, 999",
            "-n3 -r 8, 8",
    })
    void getRoundsNo_returnRoundsNumber_ifRoundsParamInMultipleFormats(String args, int expectedValue) throws IOException, ArgumentParsingException {
        BenchmarkArgumentParser parser = new BenchmarkArgumentParser(toArgs(args));
        assertEquals(expectedValue, parser.getRoundsNo());
    }

    @ParameterizedTest(name="[{index}] for args \"{0}\" should be \"{1}\"")
    @CsvSource({
            "-n3 --scenario SimpleQueueScenario, SimpleQueueScenario",
            "-n3 --scenario=SimpleQueueScenario, SimpleQueueScenario",
            "-n3 -s SimpleQueueScenario, SimpleQueueScenario",
            "-n3 -s=SimpleQueueScenario, SimpleQueueScenario",
    })
    void getScenarioType_returnScenarioType_ifScenarioTypeInMultipleFormats(String args, AvailableScenarios expectedScenarioType) throws IOException, ArgumentParsingException {
        BenchmarkArgumentParser parser = new BenchmarkArgumentParser(toArgs(args));
        Assertions.assertEquals(expectedScenarioType, parser.getScenarioType());
    }

    @ParameterizedTest
    @EnumSource(AvailableScenarios.class)
    void getScenarioType_returnScenarioType_forAllAvailableScenarioTypes(AvailableScenarios type) throws IOException, ArgumentParsingException {
        BenchmarkArgumentParser parser = new BenchmarkArgumentParser(toArgs(String.format("-n 3 --scenario %s", type.name())));
        Assertions.assertEquals(type, parser.getScenarioType());
    }

    @ParameterizedTest(name="[{index}] for args \"{0}\" should be \"{1}\"")
    @CsvSource({
            "-n3 -- --producersNo 2 --consumersNo 2, --producersNo 2 --consumersNo 2",
            "-n3 -- -a -b 2 -c 3 -n 1, -a -b 2 -c 3 -n 1",
            "-n3, ' '",
    })
    void scenarioArgs_returnScenarioArgs(String args, String scenarioArgs) throws IOException, ArgumentParsingException {
        BenchmarkArgumentParser parser = new BenchmarkArgumentParser(toArgs(args));
        assertArrayEquals(toArgs(scenarioArgs), parser.getScenarioArgs());
    }

    @ParameterizedTest(name="[{index}] for args \"{0}\" should be \"{1}\"")
    @CsvSource({
            "-n3 -- --producersNo 2 --consumersNo 2, --producersNo 2 --consumersNo 2",
            "-n3 -- -a -b 2 -c 3 -n 1, -a -b 2 -c 3 -n 1",
            "-n3, ' '",
    })
    void scenarioArgs_returnScenarioType_ifScenarioTypeInMultipleFormats(String args, String scenarioArgs) throws IOException, ArgumentParsingException {
        BenchmarkArgumentParser parser = new BenchmarkArgumentParser(toArgs(args));
        assertArrayEquals(toArgs(scenarioArgs), parser.getScenarioArgs());
    }

    @ParameterizedTest(name="[{index}] for args \"{0}\" should be \"{1}\"")
    @CsvSource(value = {
            "-n3 --ssh --ssh-hosts=a,b,c; true",
            "-n1 --ssh --ssh-hosts=a; true",
            "-n1 --ssh --ssh-hosts=a,b,c,d; true",
            "-n3; false",
    }, delimiter = ';')
    void isSsh_returnIsSshBenchmarking(String args, boolean expectedValue) throws IOException, ArgumentParsingException {
        BenchmarkArgumentParser parser = new BenchmarkArgumentParser(toArgs(args));
        assertEquals(expectedValue, parser.isSsh());
    }

    @ParameterizedTest(name="[{index}] with hosts \"{arguments}\"")
    @ValueSource(strings = {
            "user1@host1 user2@host2 user3@host3",
            "user1@host1 user2@host2 user3@host3 user4@host4 user5@host5",
    })
    void sshHosts_returnHostsArray_ifSshoHostsOptionProvided(String hosts) throws IOException, ArgumentParsingException {
        String[] hostsArray = hosts.split(" ");
        String hostsParam = String.join(",", hostsArray);
        BenchmarkArgumentParser parser = new BenchmarkArgumentParser(toArgs(String.format("-n1 --ssh --ssh-hosts=%s", hostsParam)));
        assertArrayEquals(hostsArray, parser.sshHosts());
    }

    @ParameterizedTest(name="[{index}] for args \"{arguments}\"")
    @ValueSource(strings = {
            "-n1",
            "-n 1",
            "-n 1 -s SimpleTopic --rounds 5 -- --producersNo 2 --consumersNo 2",
    })
    void getArgs_returnPassedArgs(String argsStr) throws IOException, ArgumentParsingException {
        String[] args = toArgs(argsStr);
        BenchmarkArgumentParser parser = new BenchmarkArgumentParser(args);
        assertArrayEquals(args, parser.getArgs());
    }







    private String[] toArgs(String argsLine) {
        return argsLine.split(" ");
    }

    private void assertBenchmarkThrowsParsingException(String args) {
        assertThrows(ArgumentParsingException.class, () -> new BenchmarkArgumentParser(toArgs(args)));
    }
}