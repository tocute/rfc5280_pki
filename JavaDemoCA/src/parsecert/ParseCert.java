package parsecert;

import java.security.*;
import java.io.*;
import java.util.*;
import java.security.cert.*;
import java.security.cert.CertificateFactory;


/**
 *
 * @author tas151
 */
public class ParseCert {
    
    /** Creates a new instance of Main */
    public ParseCert() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{

            FileInputStream in=new FileInputStream("client.cer"); 
            FileInputStream incrl=new FileInputStream("revoke_2011.crl"); 
            
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate)cf.generateCertificate(in);    
            X509CRL crl=(X509CRL)cf.generateCRL(incrl);
          
            PublicKey pbk = cert.getPublicKey();
            //crl.verify(pbk);
            cert.verify(pbk);
            System.out.println("Verify:OK\n");
            
            System.out.println("CRL:");
            System.out.println("Version: " + crl.getVersion());
            System.out.println("Issuer: " + crl.getIssuerDN());

            Set revocations = crl.getRevokedCertificates();
            if (revocations != null)
            {
                Iterator it = revocations.iterator();
                while (it.hasNext())
                {
                    X509CRLEntry entry = (X509CRLEntry)it.next();
                    System.out.println("SerialNumber: " + entry.getSerialNumber());
                }
            }
        }  
            
            
        
        
        catch(Exception e){
            System.out.println(e.toString());
        }
        
    }
    
}
