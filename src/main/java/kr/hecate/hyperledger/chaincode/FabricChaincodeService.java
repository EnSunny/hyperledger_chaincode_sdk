package kr.hecate.hyperledger.chaincode;

import kr.hecate.hyperledger.client.ChannelClient;
import kr.hecate.hyperledger.client.FabricClient;
import kr.hecate.hyperledger.config.ConfigureConstants;
import kr.hecate.hyperledger.connection.FabricConnManager;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.ChaincodeResponse;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;


import static java.nio.charset.StandardCharsets.UTF_8;
import static kr.hecate.hyperledger.config.ConfigureConstants.EXPECTED_EVENT_DATA;
import static kr.hecate.hyperledger.config.ConfigureConstants.EXPECTED_EVENT_NAME;

public class FabricChaincodeService {

    static final Logger logger = LoggerFactory.getLogger(ChannelClient.class);

    private FabricConnManager connManager = null;
    private ChannelClient channelClient = null;
    private FabricClient fabClient = null;

    public FabricChaincodeService (){
        connManager = FabricConnManager.getInstance();
        channelClient = connManager.getChannelClient();
        fabClient = connManager.getFabricClient();
    }

    public String queryChaincode(String funcName, String[] arguments){
        String stringResponse = null;

        try {
            Collection<ProposalResponse>  responses1Query = channelClient.queryByChainCode(ConfigureConstants.CHAINCODE_1_NAME, funcName, arguments);
            for (ProposalResponse pres : responses1Query) {

                stringResponse = new String(pres.getChaincodeActionResponsePayload());
                logger.info(stringResponse);
//                java.util.logging.Logger.getLogger(QueryChaincode.class.getName()).log(Level.INFO, stringResponse);
            }
        } catch (InvalidArgumentException e) {
            logger.error(e.toString());
        }
        return stringResponse;
    }

    public String InvokeChaincode(String funcName, String[] arguments){
        try {
            TransactionProposalRequest request = fabClient.getInstance().newTransactionProposalRequest();
            ChaincodeID ccid = ChaincodeID.newBuilder().setName(ConfigureConstants.CHAINCODE_1_NAME).build();
            request.setChaincodeID(ccid);
            request.setFcn(funcName);
            //String[] arguments = { "CAR1", "Chevy", "Volt", "Red", "Nick" };
            request.setArgs(arguments);
            request.setProposalWaitTime(1000);

            Map<String, byte[]> tm2 = new HashMap<>();
            tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
            tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8));
            tm2.put("result", ":)".getBytes(UTF_8));
            tm2.put(EXPECTED_EVENT_NAME, EXPECTED_EVENT_DATA);

            request.setTransientMap(tm2);
            Collection<ProposalResponse> responses = channelClient.sendTransactionProposal(request);
            for (ProposalResponse res: responses) {
                ChaincodeResponse.Status status = res.getStatus();
                logger.info("Invoke Success.");
                logger.info(status.toString());
//                logger.info(responses.);
               // Logger.getLogger(InvokeChaincode.class.getName()).log(Level.INFO,"Invoked createCar on "+ConfigureConstants.CHAINCODE_1_NAME + ". Status - " + status);
            }


        } catch (InvalidArgumentException | ProposalException e) {
            logger.error(e.toString());
        }
        return "";
    }
}
