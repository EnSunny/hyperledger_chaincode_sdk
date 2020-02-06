package kr.hecate.hyperledger.config;

import kr.hecate.hyperledger.client.ChannelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ConfigureConstants {

    static final Logger logger = LoggerFactory.getLogger(ConfigureConstants.class);

    public static final byte[] EXPECTED_EVENT_DATA = "!".getBytes(UTF_8);
    public static final String EXPECTED_EVENT_NAME = "event";

    public static String PROPERTIES_FILE_NAME = "./properties/server.properties";
    public static String ORG1_MSP = "Org1MSP";
    public static String ORG1 = "org1";
//    public static String ORG2_MSP = "Org2MSP";
//    public static String ORG2 = "org2";
    public static String ADMIN = "admin";
    public static String ADMIN_PASSWORD = "adminpw";
    public static String CRYPTO_PATH = "/Users/yuwonseon/Documents/hyperledgerls";
    public static String CHANNEL_CONFIG_PATH = "config/channel.tx";
    public static String ORG1_USR_BASE_PATH = CRYPTO_PATH + File.separator + "crypto-config" + File.separator + "peerOrganizations" + File.separator
            + "org1.example.com" + File.separator + "users" + File.separator + "Admin@org1.example.com"
            + File.separator + "msp";
//    public static final String ORG2_USR_BASE_PATH = CRYPTO_PATH + File.separator + "crypto-config" + File.separator + "peerOrganizations" + File.separator
//            + "org2.example.com" + File.separator + "users" + File.separator + "Admin@org2.example.com"
//            + File.separator + "msp";
    public static String ORG1_USR_ADMIN_PK = ORG1_USR_BASE_PATH + File.separator + "keystore";
    public static String ORG1_USR_ADMIN_CERT = ORG1_USR_BASE_PATH + File.separator + "admincerts";
//    public static String ORG2_USR_ADMIN_PK = ORG2_USR_BASE_PATH + File.separator + "keystore";
//    public static String ORG2_USR_ADMIN_CERT = ORG2_USR_BASE_PATH + File.separator + "admincerts";
    public static String CA_ORG1_URL = "http://localhost:7054";
//    public static String CA_ORG2_URL = "http://localhost:8054";
    public static String ORDERER_URL = "grpc://localhost:7050";
    public static String ORDERER_NAME = "orderer.example.com";
    public static String CHANNEL_NAME = "mychannel";
    public static String ORG1_PEER_0 = "peer0.org1.example.com";
    public static String ORG1_PEER_0_URL = "grpc://localhost:7051";
/*
    public static String ORG1_PEER_1 = "peer1.org1.example.com";
    public static String ORG1_PEER_1_URL = "grpc://localhost:7056";
    public static String ORG2_PEER_0 = "peer0.org2.example.com";
    public static String ORG2_PEER_0_URL = "grpc://localhost:8051";
    public static String ORG2_PEER_1 = "peer1.org2.example.com";
    public static String ORG2_PEER_1_URL = "grpc://localhost:8056";
*/
    public static String CHAINCODE_ROOT_DIR = "chaincode";
    public static String CHAINCODE_1_NAME = "fabcar";
    public static String CHAINCODE_1_PATH = "github.com/fabcar";
    public static String CHAINCODE_1_VERSION = "1";
    public static int PROPOSAL_WAIT_TIME = 100000;

    private static ConfigureConstants m_constants;

    private ConfigureConstants () {
        synchronized (this) {
            init();
        }
    }

    public static ConfigureConstants getInstance() {
        if(m_constants == null){
            m_constants = new ConfigureConstants();
        }
        return m_constants;
    }

    private void init() {
        try {
            loadProperties();
        } catch (Exception e) {
            logger.error("!--------------Configure init error -------------------!");
        }
    }

    private void loadProperties() throws Exception{
        Properties properties = new Properties();

        try {
            FileInputStream fis = new FileInputStream(PROPERTIES_FILE_NAME);
            properties.load(fis);
        } catch (Exception e){
            logger.error("properties file을 찾을수 없습니다.\n'{}' 파일을 CLASSPATH안에 만들어 주세요.", PROPERTIES_FILE_NAME, e);
            throw new Exception("properties file을 찾을수 없습니다.\n'" + PROPERTIES_FILE_NAME + "'파일을 CLASSPATH안에 만들어 주세요.");
        }

        ORG1_MSP = properties.getProperty("ORG1_MSP", ORG1_MSP);
        ORG1 = properties.getProperty("ORG1", ORG1);
        ADMIN = properties.getProperty("ADMIN", ADMIN);
        ADMIN_PASSWORD = properties.getProperty("ADMIN_PASSWORD", ADMIN_PASSWORD);
        CRYPTO_PATH = properties.getProperty("CRYPTO_PATH", CRYPTO_PATH);
        CHANNEL_CONFIG_PATH = properties.getProperty("CHANNEL_CONFIG_PATH", CHANNEL_CONFIG_PATH);
        ORG1_USR_BASE_PATH = CRYPTO_PATH + File.separator + "crypto-config" + File.separator + "peerOrganizations" + File.separator
                + "org1.example.com" + File.separator + "users" + File.separator + "Admin@org1.example.com"
                + File.separator + "msp";
        ORG1_USR_ADMIN_PK = ORG1_USR_BASE_PATH + File.separator + "keystore";
        ORG1_USR_ADMIN_CERT = ORG1_USR_BASE_PATH + File.separator + "admincerts";
        CA_ORG1_URL = properties.getProperty("CA_ORG1_URL", CA_ORG1_URL);
        ORDERER_URL = properties.getProperty("ORDERER_URL", ORDERER_URL);
        ORDERER_NAME = properties.getProperty("ORDERER_NAME", ORDERER_NAME);
        CHANNEL_NAME = properties.getProperty("CHANNEL_NAME", CHANNEL_NAME);
        ORG1_PEER_0 = properties.getProperty("ORG1_PEER_0", ORG1_PEER_0);
        ORG1_PEER_0_URL = properties.getProperty("ORG1_PEER_0_URL", ORG1_PEER_0_URL);

        CHAINCODE_ROOT_DIR = properties.getProperty("CHAINCODE_ROOT_DIR", CHAINCODE_ROOT_DIR);
        CHAINCODE_1_NAME = properties.getProperty("CHAINCODE_1_NAME", CHAINCODE_1_NAME);
        CHAINCODE_1_PATH = properties.getProperty("CHAINCODE_1_PATH", CHAINCODE_1_PATH);
        CHAINCODE_1_VERSION = properties.getProperty("CHAINCODE_1_VERSION", CHAINCODE_1_VERSION);
        PROPOSAL_WAIT_TIME = Integer.parseInt(properties.getProperty("PROPOSAL_WAIT_TIME", String.valueOf(PROPOSAL_WAIT_TIME)));

        logger.info("!--------------- Init Configure Constants ---------------!");
        logger.info("ORG1_MSP : {}", ORG1_MSP);
        logger.info("ORG1 : {}", ORG1);
        logger.info("ADMIN : {}", ADMIN);
        logger.info("ADMIN_PASSWORD : {} ",ADMIN_PASSWORD);
        logger.info("CRYPTO_PATH : {}", CRYPTO_PATH);
        logger.info("CHANNEL_CONFIG_PATH : {}", CHANNEL_CONFIG_PATH);
        logger.info("ORG1_USR_BASE_PATH : {}", ORG1_USR_BASE_PATH);
        logger.info("ORG1_USR_ADMIN_PK : {} ",ORG1_USR_ADMIN_PK);
        logger.info("ORG1_USR_ADMIN_CERT : {}", ORG1_USR_ADMIN_CERT);
        logger.info("CA_ORG1_URL : {}", CA_ORG1_URL);
        logger.info("ORDERER_URL : {}", ORDERER_URL);
        logger.info("ORDERER_NAME : {} ",ORDERER_NAME);
        logger.info("CHANNEL_NAME : {}", CHANNEL_NAME);
        logger.info("ORG1_PEER_0 : {}", ORG1_PEER_0);
        logger.info("ORG1_PEER_0_URL : {}", ORG1_PEER_0_URL);
        logger.info("CHAINCODE_ROOT_DIR : {} ",CHAINCODE_ROOT_DIR);
        logger.info("CHAINCODE_1_NAME : {}", CHAINCODE_1_NAME);
        logger.info("CHAINCODE_1_PATH : {}", CHAINCODE_1_PATH);
        logger.info("CHAINCODE_1_VERSION : {}", CHAINCODE_1_VERSION);
        logger.info("PROPOSAL_WAIT_TIME : {}", PROPOSAL_WAIT_TIME);
    }
}
