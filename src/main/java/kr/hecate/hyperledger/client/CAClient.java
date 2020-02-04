package kr.hecate.hyperledger.client;

import kr.hecate.hyperledger.user.UserContext;
import kr.hecate.hyperledger.util.Util;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.Properties;

public class CAClient {
    static final Logger logger = LoggerFactory.getLogger(CAClient.class);

    String caUrl;
    Properties caProperties;
    HFCAClient instance;

    UserContext adminContext;

    public UserContext getAdminUserContext () { return adminContext; }

    /**
     * Set the admin user Context for registering and enroilling user.
     *
     * @param userContext
     */
    public void setAdminUserContext(UserContext userContext) { this.adminContext = userContext; }

    /***
     * Constructor
     *
     * @param caUrl
     * @param caProperties
     * @throws MalformedURLException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     * @throws CryptoException
     * @throws InvalidArgumentException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    public CAClient(String caUrl, Properties caProperties) throws MalformedURLException, IllegalAccessException, InstantiationException,
            ClassNotFoundException, CryptoException, InvalidArgumentException, NoSuchMethodException, InvocationTargetException {
        this.caUrl = caUrl;
        this.caProperties = caProperties;
        init();
    }

    public void init() throws MalformedURLException, IllegalAccessException, InvocationTargetException, InvalidArgumentException,
            InstantiationException, NoSuchMethodException, CryptoException, ClassNotFoundException {
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        instance = HFCAClient.createNewInstance(caUrl, caProperties);
        instance.setCryptoSuite(cryptoSuite);
    }

    public HFCAClient getInstance() { return instance; }

    /**
     * Enroll admin user.
     *
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    public UserContext enrollAdminUser(String username, String password) throws Exception{
        UserContext userContext = Util.readUserContext(adminContext.getAffiliation(), username);
        if(userContext != null) {
            logger.warn("CA -" + caUrl + " admin is already enrolled.");
            return userContext;
        }
        Enrollment adminEnrollment = instance.enroll(username, password);
        adminContext.setEnrollment(adminEnrollment);
        logger.info("CA - " + caUrl + " Enrolled Admin");
        Util.wirteUserContext(adminContext);
        return adminContext;
    }

    /**
     * Register user.
     *
     * @param username
     * @param organization
     * @return
     * @throws Exception
     */
    public String registerUser(String username, String organization) throws Exception{
        UserContext userContext = Util.readUserContext(adminContext.getAffiliation(), username);
        if(userContext != null){
            logger.warn("CA -" + caUrl + " User " + username + " is alread registered");
            return null;
        }
        RegistrationRequest rr = new RegistrationRequest(username, organization);
        String enrollmentSecret = instance.register(rr, adminContext);
        logger.info("CA -" + caUrl + " Registered User - " + username);
        return enrollmentSecret;
    }

    /**
     * Enroll user.
     *
     * @param user
     * @param secret
     * @return
     * @throws Exception
     */
    public UserContext enrollUser(UserContext user, String secret) throws Exception{
        UserContext userContext = Util.readUserContext(adminContext.getAffiliation(), user.getName());
        if(userContext != null){
            logger.warn("CA -" + caUrl + " User " + user.getName() + " is already enrolled");
            return userContext;
        }
        Enrollment enrollment = instance.enroll(user.getName(), secret);
        user.setEnrollment(enrollment);
        Util.wirteUserContext(user);
        logger.info("CA -" +caUrl +" Enrolled User - " + user.getName());
        return user;
    }
}