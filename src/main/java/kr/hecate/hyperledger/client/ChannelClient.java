package kr.hecate.hyperledger.client;

import org.hyperledger.fabric.protos.peer.Chaincode;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.TransactionRequest.Type;
import org.hyperledger.fabric.sdk.exception.ChaincodeEndorsementPolicyParseException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.BlockEvent.TransactionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static java.nio.charset.StandardCharsets.UTF_8;


public class ChannelClient {
    static final Logger logger = LoggerFactory.getLogger(ChannelClient.class);

    private String name;
    private Channel channel;
    private FabricClient fabClient;

    public String getName() { return name; }
    public Channel getChannel() { return channel; }
    public FabricClient getFabClient() { return fabClient; }

    /**
     * Constructor
     *
     * @param name
     * @param channel
     * @param fabricClient
     */
    public ChannelClient(String name, Channel channel, FabricClient fabricClient) {
        this.name = name;
        this.channel = channel;
        this.fabClient = fabricClient;
    }

    /**
     * Query By chaincode
     *
     * @param chaincodeName
     * @param functionName
     * @param args
     * @return
     * @throws InvalidArgumentException
     * @throws ProposalException
     */
    public Collection<ProposalResponse> queryByChainCode(String chaincodeName, String functionName, String[] args) {
        logger.info("Querying " + functionName + " on channel " + channel.getName());

        Collection<ProposalResponse> response = null;
        try{
            QueryByChaincodeRequest request = fabClient.getInstance().newQueryProposalRequest();
            ChaincodeID ccid = ChaincodeID.newBuilder().setName(chaincodeName).build();
            request.setChaincodeID(ccid);
            request.setFcn(functionName);
            if(args != null){
                request.setArgs(args);
            }
             response = channel.queryByChaincode(request);

        } catch (InvalidArgumentException ie) {

        } catch (ProposalException pe) {

        }

        return response;
    }

    /***
     * Send Transaction proposal.
     *
     * @param request
     * @return
     * @throws ProposalException
     * @throws InvalidArgumentException
     */
    public Collection<ProposalResponse> sendTransactionProposal(TransactionProposalRequest request) throws
            ProposalException, InvalidArgumentException{
        logger.info("Sending transaction proposal on channel " + channel.getName());

        Collection<ProposalResponse> response = channel.sendTransactionProposal(request, channel.getPeers());

        for(ProposalResponse pres : response) {
            String stringResponse = new String(pres.getChaincodeActionResponsePayload());
            logger.info("Transaction proposal on channel " + channel.getName() + " " + pres.getMessage() + " "
                        + pres.getStatus() + " with transaction id:" + pres.getTransactionID() );
            logger.info(stringResponse);
        }

        CompletableFuture<TransactionEvent> cf = channel.sendTransaction(response);
//        logger.info(cf.toString());
//        if(cf.isDone()){
//            logger.info(cf.toString());
//        }
        return response;
    }

    /**
     *
     * @param chaincodeName
     * @param version
     * @param chaincodePath
     * @param language
     * @param functionName
     * @param functionArgs
     * @param policyPath
     * @return
     * @throws InvalidArgumentException
     * @throws ProposalException
     * @throws ChaincodeEndorsementPolicyParseException
     * @throws IOException
     */

    public Collection<ProposalResponse> instantiateChainCode(String chaincodeName, String version, String chaincodePath,
                                                             String language, String functionName, String[] functionArgs, String policyPath)
        throws InvalidArgumentException, ProposalException, ChaincodeEndorsementPolicyParseException, IOException {

        logger.info("Instantiate proposal request " + chaincodeName + " on channel " + channel.getName()
                + " with Fabric client " + fabClient.getInstance().getUserContext().getMspId() + " "
                + fabClient.getInstance().getUserContext().getName());

        InstantiateProposalRequest instantiateProposalRequest = fabClient.getInstance().newInstantiationProposalRequest();

        instantiateProposalRequest.setProposalWaitTime(100000);
        ChaincodeID.Builder chaincodeIDBuilder = ChaincodeID.newBuilder().setName(chaincodeName)
                                                                         .setVersion(version)
                                                                         .setPath(chaincodePath);
        ChaincodeID ccid = chaincodeIDBuilder.build();

        logger.info("Instantiating Chaincdoe ID " + chaincodeName + "on channel " + channel.getName());

        instantiateProposalRequest.setChaincodeID(ccid);
        if(language.equals(Type.GO_LANG.toString())){
            instantiateProposalRequest.setChaincodeLanguage(Type.GO_LANG);
        } else {
            instantiateProposalRequest.setChaincodeLanguage(Type.JAVA);
        }

        instantiateProposalRequest.setFcn(functionName);
        instantiateProposalRequest.setArgs(functionArgs);
        Map<String, byte[]> tm = new HashMap<>();
        tm.put("HyperledgerFabric", "InstantiateProposalRequest:JavaSDK".getBytes(UTF_8));
        tm.put("method", "InstantiateProposalRequest:JavaSDK".getBytes(UTF_8));
        instantiateProposalRequest.setTransientMap(tm);

        if(policyPath != null){
            ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
            chaincodeEndorsementPolicy.fromYamlFile(new File(policyPath));
            instantiateProposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);
        }

        Collection<ProposalResponse> responses = channel.sendInstantiationProposal(instantiateProposalRequest);
        CompletableFuture<TransactionEvent> cf = channel.sendTransaction(responses);

        logger.info("Chaincode " + chaincodeName + "on channel " + channel.getName() + " instantiation " + cf);

        return responses;
    }

    /***
     * Query a transaction by id.
     *
     *
     * @param txnId
     * @return
     * @throws ProposalException
     * @throws InvalidArgumentException
     */
    public TransactionInfo queryByTransaction(String txnId) throws ProposalException, InvalidArgumentException {
        logger.info("");

        Collection<Peer> peers = channel.getPeers();

        for(Peer peer : peers){
            TransactionInfo info = channel.queryTransactionByID(peer, txnId);
            return info;
        }
        return null;
    }

}
