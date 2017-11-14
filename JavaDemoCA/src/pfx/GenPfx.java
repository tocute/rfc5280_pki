/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pfx;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyFactory;
import java.security.KeyStore.Entry.Attribute;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;

import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.RC2Engine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.bc.BcDefaultDigestProvider;
import org.bouncycastle.pkcs.PKCS12PfxPdu;
import org.bouncycastle.pkcs.PKCS12PfxPduBuilder;
import org.bouncycastle.pkcs.PKCS12SafeBag;
import org.bouncycastle.pkcs.PKCS12SafeBagBuilder;
import org.bouncycastle.pkcs.PKCS12SafeBagFactory;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.bc.BcPKCS12MacCalculatorBuilder;
import org.bouncycastle.pkcs.bc.BcPKCS12MacCalculatorBuilderProvider;
import org.bouncycastle.pkcs.bc.BcPKCS12PBEOutputEncryptorBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS12SafeBagBuilder;
import org.bouncycastle.pkcs.jcajce.JcePKCSPBEInputDecryptorProviderBuilder;


/**
 *
 * @author bill.chang
 */
public class GenPfx {
    public static char[] KEY_PASSWD = "1234".toCharArray();
    public static void main(String[] args) {
        System.out.println("==> GenPfx = client.cer + private.key");
        try{
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            
            FileInputStream fis1 = new FileInputStream("root_service.cer"); 
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate root_cert = (X509Certificate)cf.generateCertificate(fis1);
            fis1.close();

            FileInputStream fis2 = new FileInputStream("service_client.cer"); 
            X509Certificate service_cert = (X509Certificate)cf.generateCertificate(fis2);
            fis1.close();
            
            KeyFactory kfac = KeyFactory.getInstance("RSA");
            //PublicKey publicKey = null;
            PrivateKey privateKey = null;
            
//            // Read public key from file.
//            try (FileInputStream fis = new FileInputStream("root_public.key")) {
//                long len = fis.getChannel().size();
//                byte[] loadedBytes = new byte[(int) len];
//                fis.read(loadedBytes);
//                fis.close();
//                
//                X509EncodedKeySpec spec = new X509EncodedKeySpec(loadedBytes);
//                publicKey = kfac.generatePublic(spec);
//                System.out.println("public. " + publicKey.toString());
//            }
            
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
           
            
            //JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();
            PKCS12SafeBagBuilder rootCertBagBuilder = new JcaPKCS12SafeBagBuilder(root_cert);
            rootCertBagBuilder.addBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName, new DERBMPString("Bouncy Primary Certificate"));
            
            PKCS12SafeBagBuilder serviceCertBagBuilder = new JcaPKCS12SafeBagBuilder(service_cert);
            serviceCertBagBuilder.addBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName, new DERBMPString("Bill Test Key"));
            //eeCertBagBuilder.addBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_localKeyId, extUtils.createSubjectKeyIdentifier(publicKey));

            PKCS12SafeBagBuilder keyBagBuilder = new JcaPKCS12SafeBagBuilder(privateKey, new BcPKCS12PBEOutputEncryptorBuilder(PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, new CBCBlockCipher(new DESedeEngine())).build(KEY_PASSWD));
            keyBagBuilder.addBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName, new DERBMPString("Bill Test Key"));
            //keyBagBuilder.addBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_localKeyId, extUtils.createSubjectKeyIdentifier(publicKey));

            //
            // construct the actual key store
            //
            PKCS12PfxPduBuilder pfxPduBuilder = new PKCS12PfxPduBuilder();
            PKCS12SafeBag[] certs = new PKCS12SafeBag[2];
            certs[0] = serviceCertBagBuilder.build();
            certs[1] = rootCertBagBuilder.build();

            pfxPduBuilder.addEncryptedData(new BcPKCS12PBEOutputEncryptorBuilder(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC, new CBCBlockCipher(new RC2Engine())).build(KEY_PASSWD), certs);
            pfxPduBuilder.addData(keyBagBuilder.build());

            PKCS12PfxPdu pfx = pfxPduBuilder.build(new BcPKCS12MacCalculatorBuilder(), KEY_PASSWD);
            FileOutputStream fout = new FileOutputStream("root_client.pfx");
            fout.write(pfx.getEncoded());
            fout.close();

            
            
            // Parse PFX/P12
            ContentInfo[] infos = pfx.getContentInfos();

            Map certMap = new HashMap();
            Map certKeyIds = new HashMap();
            Map privKeyMap = new HashMap();
            Map privKeyIds = new HashMap();

            InputDecryptorProvider inputDecryptorProvider = new JcePKCSPBEInputDecryptorProviderBuilder()
                                                                .setProvider("BC").build(KEY_PASSWD);

            JcaX509CertificateConverter  jcaConverter = new JcaX509CertificateConverter().setProvider("BC");

            
            for (int i = 0; i != infos.length; i++)
            {
                if (infos[i].getContentType().equals(PKCSObjectIdentifiers.encryptedData)) //CER file
                {
                    PKCS12SafeBagFactory dataFact = new PKCS12SafeBagFactory(infos[i], inputDecryptorProvider);
                    PKCS12SafeBag[] bags = dataFact.getSafeBags();

                    for (int b = 0; b != bags.length; b++)
                    {
                        PKCS12SafeBag bag = bags[b];

                        X509CertificateHolder certHldr = (X509CertificateHolder)bag.getBagValue();
                        X509Certificate       cert2 = jcaConverter.getCertificate(certHldr);
                        //System.out.println("X509Certificate: " + cert2);
                        org.bouncycastle.asn1.pkcs.Attribute[] attributes = bag.getAttributes();
                        for (int a = 0; a != attributes.length; a++)
                        {
                            org.bouncycastle.asn1.pkcs.Attribute attr = attributes[a];
                            if (attr.getAttrType().equals(PKCS12SafeBag.friendlyNameAttribute))
                            {
                                certMap.put(((DERBMPString)attr.getAttributeValues()[0]).getString(), cert2);
                            }
                            else if (attr.getAttrType().equals(PKCS12SafeBag.localKeyIdAttribute))
                            {
                                certKeyIds.put(attr.getAttributeValues()[0], cert2);
                            }
                        }
                    }
                }
                else //private key file
                {
                    PKCS12SafeBagFactory dataFact = new PKCS12SafeBagFactory(infos[i]);
                    PKCS12SafeBag[] bags = dataFact.getSafeBags();

                    //PKCS8EncryptedPrivateKeyInfo tempInfo = (PKCS8EncryptedPrivateKeyInfo)bags[0].getBagValue();
                    PKCS8EncryptedPrivateKeyInfo encInfo = (PKCS8EncryptedPrivateKeyInfo)bags[0].getBagValue();
                    PrivateKeyInfo info = encInfo.decryptPrivateKeyInfo(inputDecryptorProvider);

                    KeyFactory keyFact = KeyFactory.getInstance(info.getPrivateKeyAlgorithm().getAlgorithm().getId(), "BC");
                    PrivateKey privKey = keyFact.generatePrivate(new PKCS8EncodedKeySpec(info.getEncoded()));

                    org.bouncycastle.asn1.pkcs.Attribute[] attributes = bags[0].getAttributes();
                    for (int a = 0; a != attributes.length; a++)
                    {
                        org.bouncycastle.asn1.pkcs.Attribute attr = attributes[a];
                        if (attr.getAttrType().equals(PKCS12SafeBag.friendlyNameAttribute))
                        {
                            privKeyMap.put(((DERBMPString)attr.getAttributeValues()[0]).getString(), privKey);
                        }
                        else if (attr.getAttrType().equals(PKCS12SafeBag.localKeyIdAttribute))
                        {
                            //privKeyIds.put(attr.getAttributeValues()[0], privKey);
                            privKeyIds.put(privKey, attr.getAttributeValues()[0]);
                        }                            
                    }
                }
            }

            System.out.println("########## PFX Dump");
            for (Iterator it = privKeyMap.keySet().iterator(); it.hasNext();)
            {
                String alias = (String)it.next();
                Object key = privKeyMap.get(alias);
                Object privKey_id = privKeyIds.get(key);

                X509Certificate cert2 = (X509Certificate)certKeyIds.get(privKey_id);  
                System.out.println("Key Entry: " + alias + ", Key: " + key );
                if( privateKey.equals(key) )
                    System.out.println("Checked");
            }

            for (Iterator it = certMap.keySet().iterator(); it.hasNext();)
            {
                String alias = (String)it.next();
 
                X509Certificate cert2 = (X509Certificate)certMap.get(alias);
                System.out.println("Certificate Entry: " + alias + ", Subject: " + cert2.getSubjectDN() );
            }
        
            System.out.println("<== GenPfx ");
        }  
        catch(Exception e){
            System.out.println("<== Error " + e.toString());
        }
    }
}
