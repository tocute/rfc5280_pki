#include <openssl/x509.h>
#include <openssl/pem.h>
#include <openssl/x509_vfy.h>

void parseP7B(PKCS7 *p7, char *infile, int informat)
{// informat: 1=der, 3=pem
    // init
    BIO *in = BIO_new(BIO_s_file()), *out = BIO_new_fp(stdout, BIO_NOCLOSE);

    // init by informat
    BIO_read_filename(in, infile);
    if (informat == 1) // DER
        p7 = d2i_PKCS7_bio(in, NULL);
    else if (informat == 3) // PEM
        p7 = PEM_read_bio_PKCS7(in, NULL, NULL, NULL);

    // parse
    if (p7 != NULL)
    {
        STACK_OF(X509) *certs = p7->d.sign->cert;        
        int i;
        if (certs != NULL)
        {
            X509 *x;
            for (i = 0; i < sk_X509_num(certs); i++)
            {
                x = sk_X509_value(certs, i);
                // print out
                //X509_print(out, x);
                {
                    BIO_printf(out, "issuer = ");
                    X509_NAME_print(out, X509_get_issuer_name(x), 0);
                    BIO_printf(out, "\nsubject = ");
                    X509_NAME_print(out, X509_get_subject_name(x), 0);
                    BIO_printf(out, "\n\n");
                }
            }
            //X509_free(x);
        }
    }
    
    BIO_free(in);
    BIO_free_all(out);
}
