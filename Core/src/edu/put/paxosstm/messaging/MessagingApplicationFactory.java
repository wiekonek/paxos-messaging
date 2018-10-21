package edu.put.paxosstm.messaging;

class MessagingApplicationFactory {
    static MessagingApp createApplication(String applicationClassName) throws
            IllegalAccessException,
            InstantiationException,
            ClassNotFoundException
    {
        Class<?> c = Class.forName(applicationClassName);
        return (MessagingApp) c.newInstance();
    }
}
