package gencert;
import org.bouncycastle.x509.*;
import java.security.*;
import java.io.*;
import java.util.*;
import org.bouncycastle.asn1.x509.*;
import java.math.*;
import java.security.cert.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class GenCert {

	    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	try{

            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

            KeyFactory kfac = KeyFactory.getInstance("RSA");
            PublicKey service_publicKey = null;
            PublicKey root_publicKey = null;
            PrivateKey root_privateKey = null;

            // Read public key from file.
            try (FileInputStream fis = new FileInputStream("service_public.key")) {
                long len = fis.getChannel().size();
                byte[] loadedBytes = new byte[(int) len];
                fis.read(loadedBytes);
                fis.close();
                
                X509EncodedKeySpec spec = new X509EncodedKeySpec(loadedBytes);
                service_publicKey = kfac.generatePublic(spec);
                System.out.println("public. " + service_publicKey.toString());
            }
   
            // Read public key from file.
            try (FileInputStream fis = new FileInputStream("root_public.key")) {
                long len = fis.getChannel().size();
                byte[] loadedBytes = new byte[(int) len];
                fis.read(loadedBytes);
                fis.close();
                
                X509EncodedKeySpec spec = new X509EncodedKeySpec(loadedBytes);
                root_publicKey = kfac.generatePublic(spec);
                System.out.println("public. " + root_publicKey.toString());
            }
            
            // Read private key from file.
            try (FileInputStream fis = new FileInputStream("root_private.key")) {
              long len = fis.getChannel().size();
              byte[] loadedBytes = new byte[(int) len];
              fis.read(loadedBytes);
              fis.close();

              PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(loadedBytes);
              root_privateKey = kfac.generatePrivate(privateKeySpec);
              System.out.println("private. " + root_privateKey.toString());
            } 
            
//            // Read CSR from file.
//            try (FileInputStream fis = new FileInputStream("test2.csr")) {
//                long len = fis.getChannel().size();
//                byte[] loadedBytes = new byte[(int) len];
//                fis.read(loadedBytes);
//                fis.close();
//
//                PKCS10CertificationRequest d = new PKCS10CertificationRequest(loadedBytes);
//                X500Name subject1 = d.getCertificationRequestInfo().getSubject();
//                System.out.println("X500Name. " + subject1.toString());
//            } 
                
            // output cer file
            // yesterday
            Date validityBeginDate = new Date(System.currentTimeMillis());
            // in years 
            Date validityEndDate = new Date(validityBeginDate.getTime() + 365 * 24 * 60 * 60 * 1000);
            BigInteger bi = BigInteger.valueOf(System.currentTimeMillis());


            // GENERATE THE X509 CERTIFICATE method 1
//            X509V1CertificateGenerator certGen = new X509V1CertificateGenerator();
//            X500Principal dnName = new X500Principal("CN=John Doe");
//            certGen.setSubjectDN(dnName);
//            certGen.setIssuerDN(dnName); // use the same

            // GENERATE THE X509 CERTIFICATE method 2
            X509V3CertificateGenerator certGen = new X509V3CertificateGenerator(); 
            X509Name subject = new X509Name("C=TW,O=TWCA,ST=Taiwan,L=Taipei,CN=Bill.Test.Root");

            certGen.setIssuerDN(subject);
            certGen.setSubjectDN(subject);
            //certGen.addExtension(X509Extensions.SubjectKeyIdentifier, false, createAuthorityKeyId(kp.getPublic()));
            //certGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false, createAuthorityKeyId(kp.getPublic()));
            
            certGen.setNotBefore(validityBeginDate);
            certGen.setNotAfter(validityEndDate);
            certGen.setSerialNumber(bi);
            certGen.setSignatureAlgorithm("sha1withRSA");
            certGen.setPublicKey(root_publicKey);
	   
            X509Certificate cert = certGen.generate(root_privateKey);
            FileOutputStream fout = new FileOutputStream("root_service.cer");
            fout.write(cert.getEncoded());
            fout.close();
	   
            create_cer_service2client(service_publicKey, root_privateKey);
            // output crl file
//            X509V2CRLGenerator   crlGen = new X509V2CRLGenerator(); 
//            Date now = new Date();
//            BigInteger crlNumber = BigInteger.valueOf(1);
//            crlGen.setIssuerDN(cert.getIssuerX500Principal()); 
//            crlGen.setThisUpdate( now );
//            crlGen.setNextUpdate( now );
//            crlGen.setSignatureAlgorithm(cert.getSigAlgName());
//            crlGen.addCRLEntry(BigInteger.ONE, now, CRLReason.privilegeWithdrawn);
//            
//            crlGen.addExtension(X509Extensions.AuthorityKeyIdentifier,
//                  false, new AuthorityKeyIdentifierStructure(cert));
//            crlGen.addExtension(X509Extensions.CRLNumber,
//                  false, new CRLNumber(crlNumber));
// 
//            X509CRL    crl = crlGen.generateX509CRL(privateKey, "BC");
//            FileOutputStream fout2 = new FileOutputStream("client.crl");
//            fout2.write(crl.getEncoded());
//            fout2.close();
            }
            catch(Exception e){
		System.out.println(e.toString());
            }
		
	}
        

        public static void create_cer_service2client(PublicKey _ServicePublicKey, PrivateKey _RootPrivateKey) 
        {
            try{
                // output cer file
                // yesterday
                Date validityBeginDate = new Date(System.currentTimeMillis());
                // in years 
                Date validityEndDate = new Date(validityBeginDate.getTime() + 365 * 24 * 60 * 60 * 1000);
                BigInteger bi = BigInteger.valueOf(System.currentTimeMillis());


                // GENERATE THE X509 CERTIFICATE method 2
                X509V3CertificateGenerator certGen = new X509V3CertificateGenerator(); 
                X509Name issuer = new X509Name("C=TW,O=TWCA,ST=Taiwan,L=Taipei,CN=Bill.Test.Root");
                X509Name subject = new X509Name("C=TW,O=TWCA,ST=Taiwan,L=Taipei,CN=Bill.Test.Service");
                certGen.setIssuerDN(issuer);
                certGen.setSubjectDN(subject);

                certGen.setNotBefore(validityBeginDate);
                certGen.setNotAfter(validityEndDate);
                certGen.setSerialNumber(bi);
                certGen.setSignatureAlgorithm("sha1withRSA");
                certGen.setPublicKey(_ServicePublicKey);

                X509Certificate cert = certGen.generate(_RootPrivateKey);
                FileOutputStream fout = new FileOutputStream("service_client.cer");
                fout.write(cert.getEncoded());
                fout.close();
            }
            catch(Exception e){
		System.out.println(e.toString());
            }
        }
}
