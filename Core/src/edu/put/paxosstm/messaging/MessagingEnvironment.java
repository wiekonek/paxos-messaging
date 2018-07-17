package edu.put.paxosstm.messaging;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import edu.put.paxosstm.messaging.core.ApplicationRunner;
import edu.put.paxosstm.messaging.core.MessagingApp;
import soa.paxosstm.tools.Tools;

import java.util.stream.Stream;

/**
 * Paxos messaging environment
 */
public class MessagingEnvironment {

    /**
     * This method load PaxosMessaging system and start your {@link MessagingApp} with specified params.
     *
     * @param config Messaging config (specifying nodes and Paxos parameters).
     * @param app Your application class implementing {@link MessagingApp}.
     * @param params Params for your application (passed to {@link MessagingApp#application(String[])}).
     * @param <T> Your application type.
     */
    public static <T extends MessagingApp> void startEnvironment(MessagingConfig config, Class<T> app, String... params) throws MessagingException {
        if (!config.isInitializedProperly()) throw new MessagingException("Config not initialized properly!");

        try {

            String[] paxosParams = new String[]{
                    ApplicationRunner.class.getName(),
                    Integer.toString(config.nodeId),
                    Tools.encode(config.getConfString()),
                    app.getName()
            };
            soa.paxosstm.dstm.Main.main(
                    params.length == 0 ?
                            paxosParams :
                            Stream.of(paxosParams, params).flatMap(Stream::of).toArray(String[]::new)
            );
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
