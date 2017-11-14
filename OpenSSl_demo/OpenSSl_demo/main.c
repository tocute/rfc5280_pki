#include <stdio.h>
#include <stdlib.h>
#include <openssl/bio.h>
#include <openssl/x509.h>
#include <openssl/pem.h>
#include <openssl/evp.h>


int main(int argc, char *argv[])
{
    OpenSSL_add_all_algorithms();
    RSA *kp = genRSA(1024);
    
    //RSA_free(kp);
    
//    int choose=0;
//    char *filename="C:\\TestCerts\\pem.cer";
//    PKCS7 *chain =NULL;
//    char *filename2="C:\\TestCerts\\EE2.p7b";
//    int keylength=1024;
//    char *keyfile="C:\\TestCerts\\tkey.pem";
    X509_REQ *req = genCSR("private.key", "root.csr", EVP_sha1());
    
//    
//    BIO *EEin=BIO_new(BIO_s_file());
//    BIO_read_filename(EEin, "C:\\TestCerts\\XMLEE.cer");
//    X509 *TrustAnchor = PEM_read_bio_X509(EEin, NULL, NULL, NULL);
//    BIO *Chainin = BIO_new(BIO_s_file());
//    //BIO_read_filename(Chainin,"C:\\TestCerts\\XMLchain.p7b" );
//    BIO_read_filename(Chainin,"C:\\TestCerts\\EE2.p7b" );
//    PKCS7 *p7 = d2i_PKCS7_bio(Chainin, NULL);
//    STACK_OF(X509) *certs = p7->d.sign->cert;
//    if(argc>1){
//       if (strcmp(argv[1],"parse")==0){
//           choose=1;                            
//           if(argc>2){                            
//              filename=argv[2];
//           }   
//       }
//       if (strcmp(argv[1],"parsep7")==0){
//           choose=2;
//           if(argc>2){                            
//              filename2=argv[2];
//           }            
//       }
//       if (strcmp(argv[1],"genrsa")==0)
//           choose=3;
//           if(argc>2){
//              sscanf(argv[2], "%d",&keylength);
//           }
//           if(argc>3){
//              keyfile=argv[3];
//           }
//       if (strcmp(argv[1],"gencsr")==0)
//           choose=4; 
//           if(argc>2){
//              keyfile=argv[2];
//           }
//           if(argc>3){
//              reqfile=argv[3];
//           } 
//       if (strcmp(argv[1],"verify")==0)
//           choose=5;
//    }
//    switch(choose)
//    {
//     case 1:
// 
//         ParceCert(filename);
//     break;
//     case 2:
//   
//         parseP7B(chain,filename2,1); 
//     break;

//     case 4:
//    // char *keyfile="C:\\TestCerts\\tkey.pem";
//    // X509_REQ *req=X509_REQ_new();
//    // char *reqfile="C:\\TestCerts\\tcsr.csr";
//         genCSR(req, keyfile, reqfile, EVP_sha1(), 1);
//     break;
//     case 5:
//          printf("%s",vfyCRTinP7b(TrustAnchor, certs)) ;
//          
//     break;
//     default:
//         printf("Wrong Command!!!\n");
//         printf("Valid Command:\n");
//         printf("parse keyfile\n");
//         printf("parsep7 chainfile\n");
//         printf("genrsa keylength keyfile\n");
//         printf("gencsr keyfile reqfile\n");
//         printf("verify");
//     }
     
    
}
