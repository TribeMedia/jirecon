/*
 * Jirecon, the Jitsi recorder container.
 * 
 * Distributable under LGPL license. See terms of license at gnu.org.
 */
package org.jitsi.jirecon.xmppcomponent;

import org.dom4j.*;
import org.xmpp.packet.*;

/**
 * Static tool for operating Recording IQ extension.
 * <p>
 * JireconComponent extends the IQ with its own extension called
 * "recording extension", like this:
 * <p>
 * 
 * <pre>
 * +----------------+
 * | iq             |
 * +----------------+
 * | +-----------+  |
 * | | recording |  |
 * | | packet    |  |
 * | | extension |  |
 * | +-----------+  |
 * +----------------+
 * </pre>
 * <p>
 * 
 * There are five attribute in recording packet extension: action, status,
 * mucjid, dst and rid:
 * <ol>
 * <li>
 * 1. action. Possible values are: 'start', 'stop', 'info'. 'start'/'stop' means
 * to start/stop a recording, 'info' means to notify recording information.</li>
 * <li>
 * 2. status. Possible values are: 'initiating', 'recording', 'stopping',
 * 'stopped'. It MUST be set in packet sent from component. Status of specified
 * recording session.</li>
 * <li>
 * 3. mucjid. JID of specified recorded Jitsi-meeting. It is ONLY set in
 * starting command.</li>
 * <li>
 * 4. dst. It indicates where does the recording session output so client can
 * get recorded video files. The specified definition hasn't been decided yet,
 * maybe it's an URI or something else. It is ONLY set in "Recording Info"
 * packet.</li>
 * <li>
 * 5. rid. Identifier of specified recording session. It is generated by Jirecon
 * component and MUST be set in IQ packet during post-interaction.</li>
 * </ol>
 * <p>
 * 
 * <strong>Warning:</strong> Here we use xmpp.packet.IQ instead of
 * smack.packet.IQ, because both "whack" and "tinder" libary use xmpp.packet.IQ.
 * 
 * @author lishunyang
 * 
 */
public class RecordingIqUtils
{
    /**
     * Name space of recording packet extension.
     */
    public static final String NAMESPACE = "http://jitsi.org/protocol/jirecon";

    /**
     * Stanza of recording packet extension.
     */
    public static final String ELEMENT_NAME = "recording";

    /**
     * Attribute name of "action".
     */
    public static final String ACTION_NAME = "action";

    /**
     * Attribute name of "status".
     */
    public static final String STATUS_NAME = "status";

    /**
     * Attribute name of "mucjid".
     */
    public static final String MUCJID_NAME = "mucjid";
    
    /**
     * Attribute name of "dst".
     */
    public static final String OUTPUT_NAME = "dst";

    /**
     * Attribute name of "rid".
     */
    public static final String RID_NAME = "rid";
    
    /**
     * Document factory, it's used for creating xmpp.packet.IQ.
     */
    private static final DocumentFactory docFactory = DocumentFactory
        .getInstance();

    /**
     * Create resultIQ for a specified setIQ with recording extension.
     * 
     * @param relatedIq The related setIQ.
     * @return resultIQ.
     */
    public static IQ createIqResult(IQ relatedIq)
    {
        final IQ result = IQ.createResultIQ(relatedIq);
        final Element record =
            docFactory.createElement(ELEMENT_NAME, NAMESPACE);

        result.setChildElement(record);

        return result;
    }

    /**
     * Create setIQ with recording extension.
     * 
     * @param from Local peer's jid.
     * @param to Remote peer's jid.
     * @return setIQ.
     */
    public static IQ createIqSet(String from, String to)
    {
        final IQ set = new IQ(IQ.Type.set);
        final Element record =
            docFactory.createElement(ELEMENT_NAME, NAMESPACE);

        set.setFrom(from);
        set.setTo(to);
        set.setChildElement(record);

        return set;
    }

    /**
     * Add attribute to a specified IQ.
     * 
     * @param iq The IQ to be added attribute.
     * @param attrName Name of attribute.
     * @param attrValue Value of attribute.
     */
    public static void addAttribute(IQ iq, String attrName, String attrValue)
    {
        final Element record = iq.getChildElement();

        record.add(docFactory.createAttribute(record, attrName, attrValue));
    }

    /**
     * Get attribute of a specified IQ.
     * 
     * @param iq The IQ to get attribute from.
     * @param attrName Name of attribute.
     * @return Value of attribute. It could be null if it's not found.
     */
    public static String getAttribute(IQ iq, String attrName)
    {
        final Element element = iq.getChildElement();

        return element.attribute(attrName).getValue();
    }

    /**
     * Enumerative value of attribute "action" in recording extension.
     * 
     * @author lishunyang
     * 
     */
    public enum Action
    {
        /**
         * It can only be set in packet sent from client to component, in order
         * to let Jirecon component start a new recording session.
         */
        START("start"),

        /**
         * It can only be set in packet sent from client to component, in order
         * to let Jirecon component stop an specified recording session.
         */
        STOP("stop"),

        /**
         * It can be set both in packet sent from client to component or packet
         * sent from component to client, in order to notify the opposite with
         * some information, such as recording session status.
         */
        INFO("info");

        private String name;

        private Action(String name)
        {
            this.name = name;
        }

        @Override
        public String toString()
        {
            return name;
        }
    }

    /**
     * Enumerative value of attribute "status" in recording extension.
     * 
     * @author lishunyang
     * 
     */
    public enum Status
    {
        /**
         * It can only be set in packet sent from component to client, notify
         * the opposite that "start" command has been received and recording
         * session is starting.
         */
        INITIATING("initiating"),

        /**
         * It can only be set in packet sent from component to client, notify
         * the opposite that recording session has been started successfully.
         */
        STARTED("started"),

        /**
         * It can only be set in packet sent from component to client, notify
         * the opposite that "stop" command has been received and recording
         * session is stopping.
         */
        STOPPING("stopping"),

        /**
         * It can only be set in packet sent from component to client, notify
         * the opposite that recording session has been stopped successfully.
         */
        STOPPED("stopped"),

        /**
         * It can only be set in packet sent from component to client, notify
         * the opposite that recording session has been aborted.
         */
        ABORTED("aborted");

        private String name;

        private Status(String name)
        {
            this.name = name;
        }

        @Override
        public String toString()
        {
            return name;
        }
    }
}