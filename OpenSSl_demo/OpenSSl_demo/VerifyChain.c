#include <openssl/x509.h>
#include <openssl/pem.h>
#include <openssl/x509_vfy.h> 

int verify_callback(int ok, X509_STORE_CTX *stor)
{
    if (!ok)
        fprintf(stderr, "Error: %s\n",
        X509_verify_cert_error_string(stor->error));
    return ok;
}

char *vfyCRTinP7b(X509 *cert, STACK_OF(X509) *certs)
{
    /* create the cert store and set the verify callback */
    X509_STORE *store = X509_STORE_new();
    X509_STORE_set_verify_cb_func(store, verify_callback);

    // find self-signed in certs..
    int i;
    for (i=0; i<sk_X509_num(certs); i++)
    {
        X509 *root= sk_X509_value(certs,i);
        int j = X509_NAME_cmp(X509_get_issuer_name(root),
                          X509_get_subject_name(root));
                          
        if (j==0)
        {
            // save to temp file...
            BIO *tmp = BIO_new_file("temp.crt", "w");
            PEM_write_bio_X509(tmp, sk_X509_value(certs,i));
            BIO_free(tmp);
            BIO *out = BIO_new_fp(stdout,BIO_NOCLOSE);
                          printf("%d\n",j);
X509_NAME_print(out, X509_get_issuer_name(sk_X509_value(certs,i)), 0);
BIO_printf(out,"\n");
X509_NAME_print(out, X509_get_subject_name(sk_X509_value(certs,i)), 0);
            break;
        }
    }
    /* load the CA certificates and CRLs */
    X509_STORE_load_locations(store, "temp.crt", ".");
    X509_STORE_set_default_paths(store);

    /* create a verification context and initialize it */
    X509_STORE_CTX *verify_ctx = X509_STORE_CTX_new();
    X509_STORE_CTX_init(verify_ctx, store,sk_X509_value(certs,1) , NULL);
    /* verify the certificate */
    if (X509_verify_cert(verify_ctx) != 1)
        return "fail";
    else
        return "ok";
}





