package infoborden;

import javax.jms.*;

import bussimulator.Bericht;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public  class ListenerStarter implements Runnable, ExceptionListener {
    private String selector = "";
	private Infobord infobord;
	private Berichten berichten;

	// Name of the queue we will receive messages from
	private static String subject = "BUSLISTENER";
	
	public ListenerStarter() {
	}
	
	public ListenerStarter(String selector, Infobord infobord, Berichten berichten) {
		this.selector=selector;
		this.infobord=infobord;
		this.berichten=berichten;
	}

	public void run() {
        try {
            ActiveMQConnectionFactory connectionFactory = 
            		new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
//			TODO maak de connection aan
          Connection connection = connectionFactory.createConnection();
          connection.start();
          connection.setExceptionListener(this);
//			TODO maak de session aan
          Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//			TODO maak de destination aan
          Destination destination = session.createTopic(subject);
//			TODO maak de consumer aan
          MessageConsumer consumer = session.createConsumer(destination, selector);
            System.out.println("Produce, wait, consume"+ selector);
			MessageListener ml = new QueueListener(selector, infobord, berichten);
            consumer.receive();
            consumer.setMessageListener(ml);
        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }

    public synchronized void onException(JMSException ex) {
        System.out.println("JMS Exception occured.  Shutting down client.");
    }
}