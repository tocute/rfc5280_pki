package pkcs;


import java.io.*;
import java.security.PublicKey;
import java.util.*;
import java.security.cert.*;
import java.security.cert.CertificateFactory;


/**
 *
 * @author tas151
 */
public class CertChain {
    
    /** Creates a new instance of Main */
    public CertChain() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("==> CertChain");
        
        try{
        FileInputStream fis = new FileInputStream("root_client.p7b");
        FileInputStream tafis=new FileInputStream("root_service.cer"); 
      
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        CertPath cp = cf.generateCertPath(fis,"PKCS7");
        
        X509Certificate trust_cert = (X509Certificate)cf.generateCertificate(tafis);    
        
        Collection c = cp.getCertificates();
        Iterator i = c.iterator();
        while (i.hasNext()) {
            X509Certificate cert = (X509Certificate)i.next();
            //System.out.println("=== CertChain  " + cert.getSubjectDN());
        }
        i = c.iterator();
        X509Certificate root_cert = (X509Certificate)i.next();
        X509Certificate service_cert =(X509Certificate)i.next();

        service_cert.verify(root_cert.getPublicKey());
        System.out.println("=== CertChain Verify:OK");
            
        CertPathValidator validator = CertPathValidator.getInstance("PKIX");
       // String realm = null;

       // 下面看不懂
        TrustAnchor ta = new TrustAnchor(trust_cert,null);
        Set<TrustAnchor> tas = new HashSet<TrustAnchor>();
        tas.add(ta);
        java.security.cert.PKIXParameters param = new java.security.cert.PKIXParameters(tas);
        param.setRevocationEnabled(false);
        param.setPolicyQualifiersRejected(false);
        System.out.println("== CertChain Verify Start");
        validator.validate(cp,param);
        System.out.println("Verify:OK");
//        
        System.out.println("<== CertChain ");
        }
        catch(Exception e){
            System.out.println("<== CertChain Error: " + e.toString());
        }
    }
    
}

