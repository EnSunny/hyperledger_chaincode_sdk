package kr.hecate.hyperledger.connection;

import kr.hecate.hyperledger.client.CAClient;
import kr.hecate.hyperledger.client.ChannelClient;
import kr.hecate.hyperledger.client.FabricClient;
import kr.hecate.hyperledger.config.ConfigureConstants;
import kr.hecate.hyperledger.user.UserContext;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.EventHub;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;

public class FabricConnManager {

    public FabricConnManager() {}



    public void initConnection() throws Exception {

        String caUrl = ConfigureConstants.CA_ORG1_URL;
        CAClient caClient = new CAClient(caUrl, null);
        // Enroll Admin to Org1MSP
        UserContext adminUserContext = new UserContext();
        adminUserContext.setName(ConfigureConstants.ADMIN);
        adminUserContext.setAffiliation(ConfigureConstants.ORG1);
        adminUserContext.setMspId(ConfigureConstants.ORG1_MSP);
        caClient.setAdminUserContext(adminUserContext);
        adminUserContext = caClient.enrollAdminUser(ConfigureConstants.ADMIN, ConfigureConstants.ADMIN_PASSWORD);

        FabricClient fabClient = new FabricClient(adminUserContext);

        ChannelClient channelClient = fabClient.createChannelClient(ConfigureConstants.CHANNEL_NAME);
        Channel channel = channelClient.getChannel();
        Peer peer = fabClient.getInstance().newPeer(ConfigureConstants.ORG1_PEER_0, ConfigureConstants.ORG1_PEER_0_URL);
        EventHub eventHub = fabClient.getInstance().newEventHub("eventhub01", "grpc://localhost:7053");

        Orderer orderer = fabClient.getInstance().newOrderer(ConfigureConstants.ORDERER_NAME, ConfigureConstants.ORDERER_URL);
        channel.addPeer(peer);
        channel.addEventHub(eventHub);
        channel.addOrderer(orderer);
        channel.initialize();
    }
}
