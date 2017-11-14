package csr;

import java.security.*;
import java.io.*;
import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.x509.X509V1CertificateGenerator;

/**
 *
 * @author TAS151
 */
public class GenCSR {
    
    /** Creates a new instance of csrmain */
    public GenCSR() {
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        try{
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

            KeyFactory kfac = KeyFactory.getInstance("RSA");
            PublicKey publicKey = null;
            PrivateKey privateKey = null;

            // Read public key from file.
            try (FileInputStream fis = new FileInputStream("service_public.key")) {
                long len = fis.getChannel().size();
                byte[] loadedBytes = new byte[(int) len];
                fis.read(loadedBytes);
                fis.close();
                
                X509EncodedKeySpec spec = new X509EncodedKeySpec(loadedBytes);
                publicKey = kfac.generatePublic(spec);
                System.out.println("public. " + publicKey.toString());
            }
   
            // Read private key from file.
            try (FileInputStream fis = new FileInputStream("service_private.key")) {
              long len = fis.getChannel().size();
              byte[] loadedBytes = new byte[(int) len];
              fis.read(loadedBytes);
              fis.close();

              PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(loadedBytes);
              privateKey = kfac.generatePrivate(privateKeySpec);
              System.out.println("private. " + privateKey.toString());
            } 
            
            // output csr file
//            X500NameBuilder x500NameBld = new X500NameBuilder(BCStyle.INSTANCE);
//
//            x500NameBld.addRDN(BCStyle.C, "TW");
//            x500NameBld.addRDN(BCStyle.ST,"Taiwan");
//            x500NameBld.addRDN(BCStyle.L, "Taipei");
//            x500NameBld.addRDN(BCStyle.O, "TWCA");
//            x500NameBld.addRDN(BCStyle.CN, "Bill.Test");
//
//            X500Name subject = x500NameBld.build();
//
//            PKCS10CertificationRequestBuilder requestBuilder = new JcaPKCS10CertificationRequestBuilder(subject, publicKey);
//            //ExtensionsGenerator extGen = new ExtensionsGenerator();
//            //extGen.addExtension(Extension.subjectAlternativeName, false, new GeneralNames(new GeneralName(GeneralName.rfc822Name, "feedback-crypto@bouncycastle.org")));
//            //requestBuilder.addAttribute(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest, extGen.generate());
//
//            String sigName = "SHA1withRSA";
//            org.bouncycastle.pkcs.PKCS10CertificationRequest req1 = requestBuilder.build(new JcaContentSignerBuilder(sigName).setProvider("BC").build(privateKey));
//            FileOutputStream fout = new FileOutputStream("client.csr");
//            fout.write(req1.getEncoded());
//            fout.close();
//            
//            if (req1.isSignatureValid(new JcaContentVerifierProviderBuilder().setProvider("BC").build(publicKey)))
//            {
//                System.out.println(sigName + ": PKCS#10 request verified.");
//            }
//            else
//            {
//                System.out.println(sigName + ": Failed verify check.");
//            }
        
        
        // Old method
            X509Principal subject= new X509Principal("C=TW,O=TWCA,CN=Bill.Test.Service");
            PKCS10CertificationRequest req1 = new PKCS10CertificationRequest(
                                                        "SHA1withRSA",
                                                        subject,
                                                        publicKey,
                                                        null,
                                                        privateKey);
            byte[]  bytes = req1.getEncoded();
            byte[] bytes2 = Base64.encode(bytes);
            String reqstart = "-----BEGIN CERTIFICATE REQUEST-----\n";
            String reqend   = "-----END CERTIFICATE REQUEST-----\n";
            
            FileOutputStream fout = new FileOutputStream("service.csr");
            fout.write(reqstart.getBytes());
            int count=bytes2.length;
            int off=0;
            while(count>=64){
                fout.write(bytes2,off,64);
                fout.write('\n');
                count=count-64;
                off=off+64;
            }
            if (count>0){
              fout.write(bytes2,off,count);
              fout.write('\n');
            }
            fout.write(reqend.getBytes());
            fout.close();
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
        // TODO code application logic here
    }
    
}

