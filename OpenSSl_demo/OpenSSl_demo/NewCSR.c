#include <openssl/x509.h>
#include <openssl/pem.h>



X509_REQ * genCSR(char *keyfile, char *reqfile, EVP_MD *md)
{
    X509_REQ *req = X509_REQ_new();
    
    // stdout
    BIO *out = BIO_new_fp(stdout, BIO_NOCLOSE);

    // PublicKeyInfo
    BIO* bio_key = BIO_new(BIO_s_file());
    BIO_read_filename(bio_key, keyfile);
    EVP_PKEY *priv_key = PEM_read_bio_PrivateKey(bio_key, NULL, NULL, NULL);
    if (1)
        BIO_printf(out, "read key from %s\n", keyfile);

    // req - subject
    X509_NAME *subject = X509_NAME_new();
    X509_NAME_add_entry_by_txt(subject, "C", MBSTRING_ASC, (const unsigned char *)"TW", -1, -1, 0);
    X509_NAME_add_entry_by_txt(subject, "CN", MBSTRING_ASC, (const unsigned char *)"BILL.Test.SSL", -1, -1, 0);
    X509_REQ_set_subject_name(req, subject);
    if (1)
        BIO_printf(out, "set Subject\n");

    // req - pk info
    X509_REQ_set_pubkey(req, priv_key);
    
    // sign
    X509_REQ_sign(req, priv_key, md);
    if (1)
        BIO_printf(out, "signing...\n");

    // print csr
    if (1)
        X509_REQ_print(out, req);

    // write to file
    BIO *outfile = BIO_new_file(reqfile, "w");
    PEM_write_bio_X509_REQ(outfile, req);
    if (1)
        BIO_printf(out, "save csr to %s\n", reqfile);

    // clean up
    BIO_free(outfile);
    EVP_PKEY_free(priv_key);
    X509_NAME_free(subject);
    BIO_free(out);
    
    return req;
}
