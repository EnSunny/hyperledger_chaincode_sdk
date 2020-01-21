package main.java.kr.hecate.hyperledger.client;

import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public class FabricClient {

    static final Logger logger = LoggerFactory.getLogger(FabricClient.class);

    private HFClient instance;

    /**
     * Return an instance of HFClinet
     */
    public HFClient getInstance() { return instance; }

    /**
     * Construcor
     *
     * @param context
     *
     * @throws CryptoException
     * @throws InvalidArgumentException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public FabricClient(User context) throws CryptoException,
                    InvalidArgumentException, InvocationTargetException,
                    NoSuchMethodException, ClassNotFoundException,
                    InstantiationException, IllegalAccessException{
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();

        instance = HFClient.createNewInstance();
        instance.setCryptoSuite(cryptoSuite);
        instance.setUserContext(context);
    }

    /**
     *
     * @param name
     * @return
     * @throws InvalidArgumentException
     */
    public ChannelClient createChannelClient(String name) throws InvalidArgumentException{
       Channel channel = instance.newChannel(name);

       ChannelClient client = new ChannelClient(name, channel, this);
       return client;
    }

    /**
     *
     * @param chainCodeName
     * @param chaincodePath
     * @param codepath
     * @param language
     * @param version
     * @param peers
     * @return
     * @throws InvalidArgumentException
     * @throws IOException
     * @throws ProposalException
     */

    public Collection<ProposalResponse> deployChaincode(String chainCodeName, String chaincodePath, String codepath
    , String language, String version, Collection<Peer> peers) throws InvalidArgumentException, IOException, ProposalException {

        InstallProposalRequest request = instance.newInstallProposalRequest();
        ChaincodeID.Builder chaincodeIDBuilder = ChaincodeID.
                                                        newBuilder()
                                                        .setName(chainCodeName)
                                                        .setVersion(version)
                                                        .setPath(chaincodePath);
        ChaincodeID chaincodeID = chaincodeIDBuilder.build();
        logger.info("Deploying chaincode " + chainCodeName + " using Fabric client " + instance.getUserContext().getMspId()
                    + " " + instance.getUserContext().getName());
        request.setChaincodeID(chaincodeID);
        request.setUserContext(instance.getUserContext());
        request.setChaincodeSourceLocation(new File(codepath));
        request.setChaincodeVersion(version);
        Collection<ProposalResponse> responses = instance.sendInstallProposal(request, peers);

        return responses;
    }

}
