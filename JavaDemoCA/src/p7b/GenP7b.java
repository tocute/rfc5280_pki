/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package p7b;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSSignedDataParser;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.RC2Engine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import static org.bouncycastle.crypto.tls.HandshakeType.certificate;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.pkcs.PKCS12PfxPdu;
import org.bouncycastle.pkcs.PKCS12PfxPduBuilder;
import org.bouncycastle.pkcs.PKCS12SafeBag;
import org.bouncycastle.pkcs.PKCS12SafeBagBuilder;
import org.bouncycastle.pkcs.PKCS12SafeBagFactory;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.bc.BcPKCS12MacCalculatorBuilder;
import org.bouncycastle.pkcs.bc.BcPKCS12PBEOutputEncryptorBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS12SafeBagBuilder;
import org.bouncycastle.pkcs.jcajce.JcePKCSPBEInputDecryptorProviderBuilder;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.encoders.Base64;
import static pfx.GenPfx.KEY_PASSWD;

/**
 *
 * @author bill.chang
 */
public class GenP7b {
    public static void main(String[] args) {
        System.out.println("==> GenP7b = client.cer + private.key");
        try{
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
                
            // Read pfx
            PKCS12PfxPdu pfx;
            try (FileInputStream fis = new FileInputStream("root_client.pfx")) {
                 long len = fis.getChannel().size();
                 byte[] loadedBytes = new byte[(int) len];
                 fis.read(loadedBytes);
                 fis.close();
                 pfx = new PKCS12PfxPdu(loadedBytes);
             }


            // Parse PFX/P12
            ContentInfo[] infos = pfx.getContentInfos();

            Map certMap = new HashMap();
            Map privKeyMap = new HashMap();

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
                    }
                }
            }

//            System.out.println("########## PFX Dump");
//            for (Iterator it = privKeyMap.keySet().iterator(); it.hasNext();)
//            {
//                String alias = (String)it.next();
//                Object key = privKeyMap.get(alias);
//                System.out.println("Key Entry: " + alias + ", Key: " + key );
//                //if( privateKey.equals(key) )
//                //    System.out.println("Checked");
//            }
//
//            for (Iterator it = certMap.keySet().iterator(); it.hasNext();)
//            {
//                String alias = (String)it.next();
// 
//                X509Certificate cert2 = (X509Certificate)certMap.get(alias);
//                System.out.println("Certificate Entry: " + alias + ", Subject: " + cert2.getSubjectDN() );
//            }
            
            Iterator it1 = privKeyMap.keySet().iterator();
            String alias1 = (String)it1.next();
            PrivateKey privKey = (PrivateKey) privKeyMap.get(alias1);
            System.out.println("PrivateKey: " + privKey );

            Set certificates = new HashSet();
            for (Iterator it = certMap.keySet().iterator(); it.hasNext();)
            {
                String alias = (String)it.next();
 
                X509Certificate cert = (X509Certificate)certMap.get(alias);
                certificates.add(cert);
            }
            
            
            Iterator it = certificates.iterator();
            X509Certificate root_cert = (X509Certificate)it.next();
            System.out.println("Certificate Entry: Subject: " + root_cert.getSubjectDN() );
            
            X509Certificate ee_cert = (X509Certificate)it.next();
            System.out.println("Certificate Entry: Subject: " + ee_cert.getSubjectDN() );


            CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
            ContentSigner sha256Signer = new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").build(privKey);
            generator.addSignerInfoGenerator(
                    new JcaSignerInfoGeneratorBuilder(
                            new JcaDigestCalculatorProviderBuilder().setProvider("BC").build())
                            .build(sha256Signer, ee_cert));
            generator.addCertificates(new JcaCertStore(certificates));
            //CMSTypedData content = new CMSProcessableByteArray(ee_cert.getEncoded());
            CMSTypedData content = new CMSProcessableByteArray("Hello World!".getBytes());
            CMSSignedData signedData = generator.generate(content, true);
            
            FileOutputStream fout = new FileOutputStream("root_client.p7b");

            fout.write(signedData.getEncoded());
            fout.close();

            System.out.println("<== GenP7b  " + Base64.toBase64String(signedData.getEncoded()) );
            System.out.println("<== GenP7b");
        }  
        catch(Exception e){
            System.out.println("<== Error " + e.toString());
        }
    }
}
