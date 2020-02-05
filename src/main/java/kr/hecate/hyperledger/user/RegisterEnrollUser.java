package kr.hecate.hyperledger.user;

import kr.hecate.hyperledger.client.CAClient;
import kr.hecate.hyperledger.config.ConfigureConstants;
import kr.hecate.hyperledger.util.Util;

public class RegisterEnrollUser {
    public static void main(String args[]) {
        try {
            Util.cleanUp();
            String caUrl = ConfigureConstants.CA_ORG1_URL;
            CAClient caClient = new CAClient(caUrl, null);
            // Enroll Admin to Org1MSP
            UserContext adminUserContext = new UserContext();
            adminUserContext.setName(ConfigureConstants.ADMIN);
            adminUserContext.setAffiliation(ConfigureConstants.ORG1);
            adminUserContext.setMspId(ConfigureConstants.ORG1_MSP);
            caClient.setAdminUserContext(adminUserContext);
            adminUserContext = caClient.enrollAdminUser(ConfigureConstants.ADMIN, ConfigureConstants.ADMIN_PASSWORD);

            // Register and Enroll user to Org1MSP
            UserContext userContext = new UserContext();
            String name = "user"+System.currentTimeMillis();
            userContext.setName(name);
            userContext.setAffiliation(ConfigureConstants.ORG1);
            userContext.setMspId(ConfigureConstants.ORG1_MSP);

            String eSecret = caClient.registerUser(name, ConfigureConstants.ORG1);

            userContext = caClient.enrollUser(userContext, eSecret);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
