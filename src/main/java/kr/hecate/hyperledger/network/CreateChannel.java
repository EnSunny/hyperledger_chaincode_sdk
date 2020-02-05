package kr.hecate.hyperledger.network;

import kr.hecate.hyperledger.client.FabricClient;
import kr.hecate.hyperledger.config.ConfigureConstants;
import kr.hecate.hyperledger.user.UserContext;
import kr.hecate.hyperledger.util.Util;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateChannel {
    public static void main(String[] args) {
        try {
            CryptoSuite.Factory.getCryptoSuite();
            Util.cleanUp();
            // Construct Channel
            UserContext org1Admin = new UserContext();
            File pkFolder1 = new File(ConfigureConstants.ORG1_USR_ADMIN_PK);
            File[] pkFiles1 = pkFolder1.listFiles();
            File certFolder1 = new File(ConfigureConstants.ORG1_USR_ADMIN_CERT);
            File[] certFiles1 = certFolder1.listFiles();
            Enrollment enrollOrg1Admin = Util.getEnrollment(ConfigureConstants.ORG1_USR_ADMIN_PK, pkFiles1[0].getName(),
                    ConfigureConstants.ORG1_USR_ADMIN_CERT, certFiles1[0].getName());
            org1Admin.setEnrollment(enrollOrg1Admin);
            org1Admin.setMspId(ConfigureConstants.ORG1_MSP);
            org1Admin.setName(ConfigureConstants.ADMIN);

//            UserContext org2Admin = new UserContext();
//            File pkFolder2 = new File(ConfigureConstants.ORG2_USR_ADMIN_PK);
//            File[] pkFiles2 = pkFolder2.listFiles();
//            File certFolder2 = new File(ConfigureConstants.ORG2_USR_ADMIN_CERT);
//            File[] certFiles2 = certFolder2.listFiles();
//            Enrollment enrollOrg2Admin = Util.getEnrollment(ConfigureConstants.ORG2_USR_ADMIN_PK, pkFiles2[0].getName(),
//                    ConfigureConstants.ORG2_USR_ADMIN_CERT, certFiles2[0].getName());
//            org2Admin.setEnrollment(enrollOrg2Admin);
//            org2Admin.setMspId(ConfigureConstants.ORG2_MSP);
//            org2Admin.setName(ConfigureConstants.ADMIN);

            FabricClient fabClient = new FabricClient(org1Admin);

            // Create a new channel
            Orderer orderer = fabClient.getInstance().newOrderer(ConfigureConstants.ORDERER_NAME, ConfigureConstants.ORDERER_URL);
            ChannelConfiguration channelConfiguration = new ChannelConfiguration(new File(ConfigureConstants.CHANNEL_CONFIG_PATH));

            byte[] channelConfigurationSignatures = fabClient.getInstance()
                    .getChannelConfigurationSignature(channelConfiguration, org1Admin);

            Channel mychannel = fabClient.getInstance().newChannel(ConfigureConstants.CHANNEL_NAME, orderer, channelConfiguration,
                    channelConfigurationSignatures);

            Peer peer0_org1 = fabClient.getInstance().newPeer(ConfigureConstants.ORG1_PEER_0, ConfigureConstants.ORG1_PEER_0_URL);
//            Peer peer1_org1 = fabClient.getInstance().newPeer(ConfigureConstants.ORG1_PEER_1, ConfigureConstants.ORG1_PEER_1_URL);
//            Peer peer0_org2 = fabClient.getInstance().newPeer(ConfigureConstants.ORG2_PEER_0, ConfigureConstants.ORG2_PEER_0_URL);
//            Peer peer1_org2 = fabClient.getInstance().newPeer(ConfigureConstants.ORG2_PEER_1, ConfigureConstants.ORG2_PEER_1_URL);

            mychannel.joinPeer(peer0_org1);
            //mychannel.joinPeer(peer1_org1);

            mychannel.addOrderer(orderer);

            mychannel.initialize();

//            fabClient.getInstance().setUserContext(org2Admin);
//            mychannel = fabClient.getInstance().getChannel("mychannel");
//            mychannel.joinPeer(peer0_org2);
//            mychannel.joinPeer(peer1_org2);

            Logger.getLogger(CreateChannel.class.getName()).log(Level.INFO, "Channel created " + mychannel.getName());
            Collection peers = mychannel.getPeers();
            Iterator peerIter = peers.iterator();
            while (peerIter.hasNext()) {
                Peer pr = (Peer) peerIter.next();
                Logger.getLogger(CreateChannel.class.getName()).log(Level.INFO, pr.getName() + " at " + pr.getUrl());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
