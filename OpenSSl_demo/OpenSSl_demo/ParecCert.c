#include <stdio.h>
#include <stdlib.h>
#include <openssl/bio.h>
#include <openssl/x509.h>
#include <openssl/pem.h>


void ParceCert(char* Infile){
     BIO *in = BIO_new(BIO_s_file());
     BIO_read_filename(in, Infile);
     X509 *x = PEM_read_bio_X509(in, NULL, NULL, NULL);
     if(x){
     BIO *out = BIO_new_fp(stdout, BIO_NOCLOSE);
     BIO_printf(out," -------- default method --------\n");
     X509_print(out,x);	
     BIO_printf(out," -------- default method --------\n");
     X509_NAME_print(out, X509_get_issuer_name(x),0);
     printf("%ld",X509_get_serialNumber(x));
     }
     else
     printf("No file");
     }
