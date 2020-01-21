package main.java.kr.hecate.hyperledger.user;

import org.hyperledger.fabric.sdk.Enrollment;

import java.io.Serializable;
import java.security.PrivateKey;

public class CAEnrollment implements Enrollment, Serializable {
    private static final long serialVersionUID = 1L;
    private PrivateKey key;
    private String cert;

    public CAEnrollment(PrivateKey pKey, String signedPem){
        this.key = pKey;
        this.cert = signedPem;
    }

    @Override
    public PrivateKey getKey() { return key; }

    @Override
    public String getCert() { return cert; }
}
