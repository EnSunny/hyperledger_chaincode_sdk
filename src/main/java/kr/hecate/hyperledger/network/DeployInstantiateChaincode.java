package kr.hecate.hyperledger.network;

import kr.hecate.hyperledger.client.ChannelClient;
import kr.hecate.hyperledger.client.FabricClient;
import kr.hecate.hyperledger.config.ConfigureConstants;
import kr.hecate.hyperledger.user.UserContext;
import kr.hecate.hyperledger.util.Util;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeployInstantiateChaincode {

    public static void main(String[] args) {
        try {
            CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();

            UserContext org1Admin = new UserContext();
            File pkFolder1 = new File(ConfigureConstants.ORG1_USR_ADMIN_PK);
            File[] pkFiles1 = pkFolder1.listFiles();
            File certFolder = new File(ConfigureConstants.ORG1_USR_ADMIN_CERT);
            File[] certFiles = certFolder.listFiles();
            Enrollment enrollOrg1Admin = Util.getEnrollment(ConfigureConstants.ORG1_USR_ADMIN_PK, pkFiles1[0].getName(),
                    ConfigureConstants.ORG1_USR_ADMIN_CERT, certFiles[0].getName());
            org1Admin.setEnrollment(enrollOrg1Admin);
            org1Admin.setMspId("Org1MSP");
            org1Admin.setName("admin");

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

            Channel mychannel = fabClient.getInstance().newChannel(ConfigureConstants.CHANNEL_NAME);
            Orderer orderer = fabClient.getInstance().newOrderer(ConfigureConstants.ORDERER_NAME, ConfigureConstants.ORDERER_URL);
            Peer peer0_org1 = fabClient.getInstance().newPeer(ConfigureConstants.ORG1_PEER_0, ConfigureConstants.ORG1_PEER_0_URL);
//            Peer peer1_org1 = fabClient.getInstance().newPeer(ConfigureConstants.ORG1_PEER_1, ConfigureConstants.ORG1_PEER_1_URL);
//            Peer peer0_org2 = fabClient.getInstance().newPeer(ConfigureConstants.ORG2_PEER_0, ConfigureConstants.ORG2_PEER_0_URL);
//            Peer peer1_org2 = fabClient.getInstance().newPeer(ConfigureConstants.ORG2_PEER_1, ConfigureConstants.ORG2_PEER_1_URL);
            mychannel.addOrderer(orderer);
            mychannel.addPeer(peer0_org1);
//            mychannel.addPeer(peer1_org1);
//            mychannel.addPeer(peer0_org2);
//            mychannel.addPeer(peer1_org2);
            mychannel.initialize();

            List<Peer> org1Peers = new ArrayList<Peer>();
            org1Peers.add(peer0_org1);
//            org1Peers.add(peer1_org1);

//            List<Peer> org2Peers = new ArrayList<Peer>();
//            org2Peers.add(peer0_org2);
//            org2Peers.add(peer1_org2);

            Collection<ProposalResponse> response = fabClient.deployChainCode(ConfigureConstants.CHAINCODE_1_NAME,
                    ConfigureConstants.CHAINCODE_1_PATH, ConfigureConstants.CHAINCODE_ROOT_DIR, TransactionRequest.Type.GO_LANG.toString(),
                    ConfigureConstants.CHAINCODE_1_VERSION, org1Peers);


            for (ProposalResponse res : response) {
                Logger.getLogger(DeployInstantiateChaincode.class.getName()).log(Level.INFO,
                        ConfigureConstants.CHAINCODE_1_NAME + "- Chain code deployment " + res.getStatus());
            }

//            fabClient.getInstance().setUserContext(org2Admin);

//            response = fabClient.deployChainCode(ConfigureConstants.CHAINCODE_1_NAME,
//                    ConfigureConstants.CHAINCODE_1_PATH, ConfigureConstants.CHAINCODE_ROOT_DIR, TransactionRequest.Type.GO_LANG.toString(),
//                    ConfigureConstants.CHAINCODE_1_VERSION, org2Peers);

//            for (ProposalResponse res : response) {
//                Logger.getLogger(DeployInstantiateChaincode.class.getName()).log(Level.INFO,
//                        ConfigureConstants.CHAINCODE_1_NAME + "- Chain code deployment " + res.getStatus());
//            }

            ChannelClient channelClient = new ChannelClient(mychannel.getName(), mychannel, fabClient);

            String[] arguments = { "" };
            response = channelClient.instantiateChainCode(ConfigureConstants.CHAINCODE_1_NAME, ConfigureConstants.CHAINCODE_1_VERSION,
                    ConfigureConstants.CHAINCODE_1_PATH, TransactionRequest.Type.GO_LANG.toString(), "init", arguments, null);

            for (ProposalResponse res : response) {
                Logger.getLogger(DeployInstantiateChaincode.class.getName()).log(Level.INFO,
                        ConfigureConstants.CHAINCODE_1_NAME + "- Chain code instantiation " + res.getStatus());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
