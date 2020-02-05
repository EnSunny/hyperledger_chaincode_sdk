package kr.hecate.hyperledger.chaincode;

import kr.hecate.hyperledger.client.CAClient;
import kr.hecate.hyperledger.client.ChannelClient;
import kr.hecate.hyperledger.client.FabricClient;
import kr.hecate.hyperledger.config.ConfigConstants;
import kr.hecate.hyperledger.user.UserContext;
import kr.hecate.hyperledger.util.Util;
import org.hyperledger.fabric.sdk.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import static java.nio.charset.StandardCharsets.UTF_8;

public class InvokeQueryChaincode {

    private static final byte[] EXPECTED_EVENT_DATA = "!".getBytes(UTF_8);
    private static final String EXPECTED_EVENT_NAME = "event";

    public static void main(String args[]) {
        try {
            Util.cleanUp();
            String caUrl = ConfigConstants.CA_ORG1_URL;
            CAClient caClient = new CAClient(caUrl, null);
            // Enroll Admin to Org1MSP
            UserContext adminUserContext = new UserContext();
            adminUserContext.setName(ConfigConstants.ADMIN);
            adminUserContext.setAffiliation(ConfigConstants.ORG1);
            adminUserContext.setMspId(ConfigConstants.ORG1_MSP);
            caClient.setAdminUserContext(adminUserContext);
            adminUserContext = caClient.enrollAdminUser(ConfigConstants.ADMIN, ConfigConstants.ADMIN_PASSWORD);

            FabricClient fabClient = new FabricClient(adminUserContext);

            ChannelClient channelClient = fabClient.createChannelClient(ConfigConstants.CHANNEL_NAME);
            Channel channel = channelClient.getChannel();
            Peer peer = fabClient.getInstance().newPeer(ConfigConstants.ORG1_PEER_0, ConfigConstants.ORG1_PEER_0_URL);
            EventHub eventHub = fabClient.getInstance().newEventHub("eventhub01", "grpc://localhost:7053");
            Orderer orderer = fabClient.getInstance().newOrderer(ConfigConstants.ORDERER_NAME, ConfigConstants.ORDERER_URL);
            channel.addPeer(peer);
            channel.addEventHub(eventHub);
            channel.addOrderer(orderer);
            channel.initialize();

            TransactionProposalRequest request = fabClient.getInstance().newTransactionProposalRequest();
            ChaincodeID ccid = ChaincodeID.newBuilder().setName(ConfigConstants.CHAINCODE_1_NAME).build();
            request.setChaincodeID(ccid);
            request.setFcn("createCar");
            String[] arguments = { "CAR1", "Chevy", "Volt", "Red", "Nick" };
            request.setArgs(arguments);
            request.setProposalWaitTime(1000);

            Map<String, byte[]> tm2 = new HashMap<>();
            tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8)); // Just some extra junk
            // in transient map
            tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8)); // ditto
            tm2.put("result", ":)".getBytes(UTF_8)); // This should be returned see chaincode why.
            tm2.put(EXPECTED_EVENT_NAME, EXPECTED_EVENT_DATA); // This should trigger an event see chaincode why.
            request.setTransientMap(tm2);
            Collection<ProposalResponse> responses = channelClient.sendTransactionProposal(request);

            Thread.sleep(10000);

            Collection<ProposalResponse>  responsesQuery = channelClient.queryByChainCode("fabcar", "queryAllCars", null);
            for (ProposalResponse pres : responsesQuery) {
                String stringResponse = new String(pres.getChaincodeActionResponsePayload());
                System.out.println(stringResponse);
            }

            Thread.sleep(10000);
            String[] args1 = {"CAR1"};
            Collection<ProposalResponse>  responses1Query = channelClient.queryByChainCode("fabcar", "queryCar", args1);
            for (ProposalResponse pres : responses1Query) {
                String stringResponse = new String(pres.getChaincodeActionResponsePayload());
                System.out.println(stringResponse);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
