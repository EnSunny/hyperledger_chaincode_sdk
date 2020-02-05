package kr.hecate.hyperledger.util;

import kr.hecate.hyperledger.config.ConfigConstants;
import kr.hecate.hyperledger.user.CAEnrollment;
import kr.hecate.hyperledger.user.UserContext;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.xml.bind.DatatypeConverter;

public class Util {

    static final org.slf4j.Logger logger = LoggerFactory.getLogger(Util.class);

    /**
     * Serialize user
     *
     * @param userContext
     * @throws Exception
     */

    public static void wirteUserContext(UserContext userContext) throws Exception{
        String directoryPath = ConfigConstants.CRYPTO_PATH + File.separator + "users/" + userContext.getAffiliation();
        String filePath = directoryPath + "/" + userContext.getName() + ".ser";
        File directory = new File(directoryPath);

        if(!directory.exists()){
            directory.mkdirs();
        }

        FileOutputStream file = new FileOutputStream(filePath);
        ObjectOutputStream out  = new ObjectOutputStream(file);

        out.writeObject(userContext);

        out.close();
        file.close();
    }

    /**
     * Deserialize user
     *
     * @param affiliation
     * @param username
     * @return
     * @throws Exception
     */
    public static UserContext readUserContext(String affiliation, String username) throws Exception {
        String filePath = ConfigConstants.CRYPTO_PATH + File.separator + "users/" + affiliation + "/" + username + ".ser";
        File file = new File(filePath);
        if (file.exists()) {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            ObjectInputStream in = new ObjectInputStream(fileInputStream);

            UserContext userContext = (UserContext) in.readObject();

            in.close();
            fileInputStream.close();
            return userContext;
        }

        return null;
    }

    /***
     * Create enrollment from key and certificate files.
     *
     * @param keyFolderPath
     * @param keyFileName
     * @param certFolderPath
     * @param certFileName
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws CryptoException
     */

    public static CAEnrollment getEnrollment(String keyFolderPath, String keyFileName, String certFolderPath, String certFileName)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, CryptoException {
        PrivateKey key = null;
        String certificate = null;
        InputStream isKey = null;
        BufferedReader brKey = null;

        try{
            isKey = new FileInputStream(keyFolderPath + File.separator+keyFileName);
            brKey = new BufferedReader(new InputStreamReader(isKey));
            StringBuilder keyBudilder = new StringBuilder();

            for (String line = brKey.readLine() ; line != null; line = brKey.readLine()){
                if(line.indexOf("PRIVATE") == -1){
                    keyBudilder.append(line);
                }
            }

            certificate = new String(Files.readAllBytes(Paths.get(certFolderPath, certFileName)));

            byte[] encoded = DatatypeConverter.parseBase64Binary(keyBudilder.toString());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            KeyFactory kf = KeyFactory.getInstance("EC");
            key = kf.generatePrivate(keySpec);

        } finally {
            isKey.close();
            brKey.close();
        }
        CAEnrollment enrollment = new CAEnrollment(key, certificate);

        return enrollment;
    }

    public static void cleanUp() {
        String directoryPath = "users";
        File directory = new File(directoryPath);
        deleteDirectory(directory);
    }

    public static boolean deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDirectory(children[i]);
                if (!success) {
                    return false;
                }
            }
        }
        // either file or an empty directory
        logger.info("Deleting - " + dir.getName());
        return dir.delete();
    }
}
