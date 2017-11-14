package genkey;
import java.io.*;
import java.security.*;
import java.security.spec.*;


public class GenKey {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	// TODO Auto-generated method stub
	try{
	    //Gen 1024bits RSA
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024);
            KeyPair kp = kpg.generateKeyPair();
            PublicKey root_public_key = kp.getPublic(); 
            PrivateKey root_private_key = kp.getPrivate();
            
            // Store Root Private Key.
            System.out.println("private. " + root_private_key.toString());
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(root_private_key.getEncoded());
            try (FileOutputStream fos = new FileOutputStream("root_private.key")) {
              fos.write(pkcs8EncodedKeySpec.getEncoded());
              fos.close();
            }
            
            // Store Root Public Key.
            System.out.println("public. "+root_public_key.toString());
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(root_public_key.getEncoded());
            try (FileOutputStream fos = new FileOutputStream("root_public.key")) {
                fos.write(x509EncodedKeySpec.getEncoded());
                fos.close();
            }
  
            // Read public key from file.
            try (FileInputStream fis = new FileInputStream("root_public.key")) {
                long len = fis.getChannel().size();
                byte[] loadedBytes = new byte[(int) len];
                fis.read(loadedBytes);
                fis.close();
                
                X509EncodedKeySpec spec = new X509EncodedKeySpec(loadedBytes);
                KeyFactory kfac = KeyFactory.getInstance("RSA");
                PublicKey publicKey = kfac.generatePublic(spec);
                System.out.println("public. " + publicKey.toString());
            }
   
            // Read private key from file.
            try (FileInputStream fis = new FileInputStream("root_private.key")) {
              long len = fis.getChannel().size();
              byte[] loadedBytes = new byte[(int) len];
              fis.read(loadedBytes);
              fis.close();

              PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(loadedBytes);
              KeyFactory kfac = KeyFactory.getInstance("RSA");
              PrivateKey privateKey = kfac.generatePrivate(privateKeySpec);
              System.out.println("private. " + privateKey.toString());
            } 
            
            
            KeyPair kp2 = kpg.generateKeyPair();
            PublicKey service_public_key = kp2.getPublic(); 
            PrivateKey service_private_key = kp2.getPrivate();
            
            // Store Service Private Key.
            System.out.println("private. " + service_private_key.toString());
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec1 = new PKCS8EncodedKeySpec(service_private_key.getEncoded());
            try (FileOutputStream fos = new FileOutputStream("service_private.key")) {
              fos.write(pkcs8EncodedKeySpec1.getEncoded());
              fos.close();
            }
            
            // Store Service Public Key.
            System.out.println("public. " + service_public_key.toString());
            X509EncodedKeySpec x509EncodedKeySpec1 = new X509EncodedKeySpec(service_public_key.getEncoded());
            try (FileOutputStream fos = new FileOutputStream("service_public.key")) {
                fos.write(x509EncodedKeySpec1.getEncoded());
                fos.close();
            }
            
            KeyPair kp3 = kpg.generateKeyPair();
            PublicKey client_public_key = kp3.getPublic(); 
            PrivateKey client_private_key = kp3.getPrivate();
            
            // Store Client Private Key.
            System.out.println("private. " + service_private_key.toString());
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec2 = new PKCS8EncodedKeySpec(client_private_key.getEncoded());
            try (FileOutputStream fos = new FileOutputStream("client_private.key")) {
              fos.write(pkcs8EncodedKeySpec2.getEncoded());
              fos.close();
            }
            
            // Store Client Public Key.
            System.out.println("public. " + service_public_key.toString());
            X509EncodedKeySpec x509EncodedKeySpec2 = new X509EncodedKeySpec(client_public_key.getEncoded());
            try (FileOutputStream fos = new FileOutputStream("client_public.key")) {
                fos.write(x509EncodedKeySpec2.getEncoded());
                fos.close();
            }
        }
        catch(IOException | NoSuchAlgorithmException | InvalidKeySpecException e){
            System.out.println("ERROR : "+e.toString());
        }
		
	}

}
