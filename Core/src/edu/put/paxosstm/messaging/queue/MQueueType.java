package edu.put.paxosstm.messaging.queue;

/**
 * Available types of queues
 */
public enum MQueueType {
    Simple {
        @Override
        public String toString() {
            return "simple";
        }
    },
    Multi {
        @Override
        public String toString() {return  "multi"; }
    }
}
