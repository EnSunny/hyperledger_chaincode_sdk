package kr.hecate.hyperledger.chaincode;

import kr.hecate.hyperledger.client.CAClient;
import kr.hecate.hyperledger.client.ChannelClient;
import kr.hecate.hyperledger.client.FabricClient;
import kr.hecate.hyperledger.config.ConfigureConstants;
import kr.hecate.hyperledger.connection.FabricConnManager;
import kr.hecate.hyperledger.user.UserContext;
import kr.hecate.hyperledger.util.Util;
import org.hyperledger.fabric.sdk.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.logging.Level;

import static java.nio.charset.StandardCharsets.UTF_8;

public class QueryChaincode {

    static final Logger logger = LoggerFactory.getLogger(QueryChaincode.class);


    public static void main(String args[]) {
        try {

            //Util.cleanUp();
//            String caUrl = ConfigureConstants.CA_ORG1_URL;
//            CAClient caClient = new CAClient(caUrl, null);
//            // Enroll Admin to Org1MSP
//            UserContext adminUserContext = new UserContext();
//            adminUserContext.setName(ConfigureConstants.ADMIN);
//            adminUserContext.setAffiliation(ConfigureConstants.ORG1);
//            adminUserContext.setMspId(ConfigureConstants.ORG1_MSP);
//            caClient.setAdminUserContext(adminUserContext);
//            adminUserContext = caClient.enrollAdminUser(ConfigureConstants.ADMIN, ConfigureConstants.ADMIN_PASSWORD);
//
//            FabricClient fabClient = new FabricClient(adminUserContext);
//
//            ChannelClient channelClient = fabClient.createChannelClient(ConfigureConstants.CHANNEL_NAME);
//            Channel channel = channelClient.getChannel();
//            Peer peer = fabClient.getInstance().newPeer(ConfigureConstants.ORG1_PEER_0, ConfigureConstants.ORG1_PEER_0_URL);
//            EventHub eventHub = fabClient.getInstance().newEventHub("eventhub01", "grpc://localhost:7053");
//            Orderer orderer = fabClient.getInstance().newOrderer(ConfigureConstants.ORDERER_NAME, ConfigureConstants.ORDERER_URL);
//            channel.addPeer(peer);
//            channel.addEventHub(eventHub);
//            channel.addOrderer(orderer);
//            channel.initialize();

//            FabricConnManager manager = FabricConnManager.getInstance();
//            ChannelClient channelClient = manager.getChannelClient();

            FabricChaincodeService fabChaincodeService = new FabricChaincodeService();
//
//            String[] arguments1 = { "CAR12", "A", "B", "C", "D" };
////
//            String resultInvoke = fabChaincodeService.InvokeChaincode("createCar",arguments1);

//            logger.info(resultInvoke);
//            Thread.sleep(1500);
            String reusltQuery1 = fabChaincodeService.queryChaincode("queryAllCars", null);

            logger.info(reusltQuery1);

            String[] arguments2 = { "CAR12"};
            String reusltQuery2 = fabChaincodeService.queryChaincode("queryCar", arguments2);

            logger.info(reusltQuery2);


//            Logger.getLogger(QueryChaincode.class.getName()).log(Level.INFO, "Querying for all cars ...");
//
//            Collection<ProposalResponse> responsesQuery = channelClient.queryByChainCode("fabcar", "queryAllCars", null);
//            for (ProposalResponse pres : responsesQuery) {
//                String stringResponse = new String(pres.getChaincodeActionResponsePayload());
//                Logger.getLogger(QueryChaincode.class.getName()).log(Level.INFO, stringResponse);
//            }
//
//            Thread.sleep(10000);
//            String[] args1 = {"CAR1"};
//            Logger.getLogger(QueryChaincode.class.getName()).log(Level.INFO, "Querying for a car - " + args1[0]);
//
//            Collection<ProposalResponse>  responses1Query = channelClient.queryByChainCode("fabcar", "queryCar", args1);
//            for (ProposalResponse pres : responses1Query) {
//                String stringResponse = new String(pres.getChaincodeActionResponsePayload());
//                Logger.getLogger(QueryChaincode.class.getName()).log(Level.INFO, stringResponse);
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
