package kr.hecate.hyperledger.network;

import kr.hecate.hyperledger.client.ChannelClient;
import kr.hecate.hyperledger.client.FabricClient;
import kr.hecate.hyperledger.config.ConfigConstants;
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
            File pkFolder1 = new File(ConfigConstants.ORG1_USR_ADMIN_PK);
            File[] pkFiles1 = pkFolder1.listFiles();
            File certFolder = new File(ConfigConstants.ORG1_USR_ADMIN_CERT);
            File[] certFiles = certFolder.listFiles();
            Enrollment enrollOrg1Admin = Util.getEnrollment(ConfigConstants.ORG1_USR_ADMIN_PK, pkFiles1[0].getName(),
                    ConfigConstants.ORG1_USR_ADMIN_CERT, certFiles[0].getName());
            org1Admin.setEnrollment(enrollOrg1Admin);
            org1Admin.setMspId("Org1MSP");
            org1Admin.setName("admin");

//            UserContext org2Admin = new UserContext();
//            File pkFolder2 = new File(ConfigConstants.ORG2_USR_ADMIN_PK);
//            File[] pkFiles2 = pkFolder2.listFiles();
//            File certFolder2 = new File(ConfigConstants.ORG2_USR_ADMIN_CERT);
//            File[] certFiles2 = certFolder2.listFiles();
//            Enrollment enrollOrg2Admin = Util.getEnrollment(ConfigConstants.ORG2_USR_ADMIN_PK, pkFiles2[0].getName(),
//                    ConfigConstants.ORG2_USR_ADMIN_CERT, certFiles2[0].getName());
//            org2Admin.setEnrollment(enrollOrg2Admin);
//            org2Admin.setMspId(ConfigConstants.ORG2_MSP);
//            org2Admin.setName(ConfigConstants.ADMIN);

            FabricClient fabClient = new FabricClient(org1Admin);

            Channel mychannel = fabClient.getInstance().newChannel(ConfigConstants.CHANNEL_NAME);
            Orderer orderer = fabClient.getInstance().newOrderer(ConfigConstants.ORDERER_NAME, ConfigConstants.ORDERER_URL);
            Peer peer0_org1 = fabClient.getInstance().newPeer(ConfigConstants.ORG1_PEER_0, ConfigConstants.ORG1_PEER_0_URL);
//            Peer peer1_org1 = fabClient.getInstance().newPeer(ConfigConstants.ORG1_PEER_1, ConfigConstants.ORG1_PEER_1_URL);
//            Peer peer0_org2 = fabClient.getInstance().newPeer(ConfigConstants.ORG2_PEER_0, ConfigConstants.ORG2_PEER_0_URL);
//            Peer peer1_org2 = fabClient.getInstance().newPeer(ConfigConstants.ORG2_PEER_1, ConfigConstants.ORG2_PEER_1_URL);
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

            Collection<ProposalResponse> response = fabClient.deployChainCode(ConfigConstants.CHAINCODE_1_NAME,
                    ConfigConstants.CHAINCODE_1_PATH, ConfigConstants.CHAINCODE_ROOT_DIR, TransactionRequest.Type.GO_LANG.toString(),
                    ConfigConstants.CHAINCODE_1_VERSION, org1Peers);


            for (ProposalResponse res : response) {
                Logger.getLogger(DeployInstantiateChaincode.class.getName()).log(Level.INFO,
                        ConfigConstants.CHAINCODE_1_NAME + "- Chain code deployment " + res.getStatus());
            }

//            fabClient.getInstance().setUserContext(org2Admin);

//            response = fabClient.deployChainCode(ConfigConstants.CHAINCODE_1_NAME,
//                    ConfigConstants.CHAINCODE_1_PATH, ConfigConstants.CHAINCODE_ROOT_DIR, TransactionRequest.Type.GO_LANG.toString(),
//                    ConfigConstants.CHAINCODE_1_VERSION, org2Peers);

//            for (ProposalResponse res : response) {
//                Logger.getLogger(DeployInstantiateChaincode.class.getName()).log(Level.INFO,
//                        ConfigConstants.CHAINCODE_1_NAME + "- Chain code deployment " + res.getStatus());
//            }

            ChannelClient channelClient = new ChannelClient(mychannel.getName(), mychannel, fabClient);

            String[] arguments = { "" };
            response = channelClient.instantiateChainCode(ConfigConstants.CHAINCODE_1_NAME, ConfigConstants.CHAINCODE_1_VERSION,
                    ConfigConstants.CHAINCODE_1_PATH, TransactionRequest.Type.GO_LANG.toString(), "init", arguments, null);

            for (ProposalResponse res : response) {
                Logger.getLogger(DeployInstantiateChaincode.class.getName()).log(Level.INFO,
                        ConfigConstants.CHAINCODE_1_NAME + "- Chain code instantiation " + res.getStatus());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
