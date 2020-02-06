package kr.hecate.hyperledger.connection;

import kr.hecate.hyperledger.client.CAClient;
import kr.hecate.hyperledger.client.ChannelClient;
import kr.hecate.hyperledger.client.FabricClient;
import kr.hecate.hyperledger.config.ConfigureConstants;
import kr.hecate.hyperledger.user.UserContext;
import kr.hecate.hyperledger.util.Util;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.EventHub;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

public class FabricConnManager {

    static final Logger logger = LoggerFactory.getLogger(FabricConnManager.class);

    private static FabricConnManager m_fabricConnManager = new FabricConnManager();

    private static ChannelClient m_channelClient;
    private static FabricClient m_fabClient;

    private FabricConnManager() {
        init();
    }

    public static FabricConnManager getInstance(){
        return m_fabricConnManager;
    }

    private void init() {
        setupFabClient();
        setupChannelClient();
    }
    private void setupFabClient()  {

        String caUrl = ConfigureConstants.CA_ORG1_URL;
        CAClient caClient = null;
        Util util;

        try {
            caClient = new CAClient(caUrl, null);

            // Enroll Admin to Org1MSP
            UserContext adminUserContext;
            adminUserContext = Util.readUserContext(ConfigureConstants.ORG1,ConfigureConstants.ADMIN);
            if(adminUserContext == null) {

                adminUserContext = new UserContext();
                adminUserContext.setName(ConfigureConstants.ADMIN);
                adminUserContext.setAffiliation(ConfigureConstants.ORG1);
                adminUserContext.setMspId(ConfigureConstants.ORG1_MSP);
                caClient.setAdminUserContext(adminUserContext);
                adminUserContext = caClient.enrollAdminUser(ConfigureConstants.ADMIN, ConfigureConstants.ADMIN_PASSWORD);
            }

            m_fabClient = new FabricClient(adminUserContext);

        } catch (IllegalAccessException | InstantiationException | CryptoException
                | InvocationTargetException | InvalidArgumentException | MalformedURLException
                | ClassNotFoundException | NoSuchMethodException e  ) {
            e.printStackTrace();
        } catch ( Exception e) {
//            logger.error(e.toString());
            e.printStackTrace();
        }
    }

    private void setupChannelClient() {
        try {
            m_channelClient = m_fabClient.createChannelClient(ConfigureConstants.CHANNEL_NAME);
            Channel channel = m_channelClient.getChannel();
            Peer peer = m_fabClient.getInstance().newPeer(ConfigureConstants.ORG1_PEER_0, ConfigureConstants.ORG1_PEER_0_URL);
            EventHub eventHub = m_fabClient.getInstance().newEventHub("eventhub01", "grpc://localhost:7053");

            Orderer orderer = m_fabClient.getInstance().newOrderer(ConfigureConstants.ORDERER_NAME, ConfigureConstants.ORDERER_URL);
            channel.addPeer(peer);
            channel.addEventHub(eventHub);
            channel.addOrderer(orderer);
            channel.initialize();
        } catch (InvalidArgumentException | TransactionException e) {
            logger.error(e.toString());
        }
    }

    public FabricClient getFabricClient()  {
        if (m_fabClient != null) {
            return m_fabClient;
        } else {
            return null;
        }
    }

    public ChannelClient getChannelClient()  {
        if (m_channelClient != null) {
            return m_channelClient;
        } else {
            return null;
        }
    }
}
