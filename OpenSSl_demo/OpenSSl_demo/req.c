#include <openssl/x509.h>
#include <openssl/pem.h>

EVP_PKEY *req_get_private_key(char *keyfile) {
	BIO* bio_key = BIO_new(BIO_s_file());
	BIO_read_filename(bio_key, keyfile);
	EVP_PKEY *pk = PEM_read_bio_PrivateKey(bio_key, NULL, NULL, NULL);
	return pk;
}

void req_setSubject(X509_REQ *req) {
	X509_NAME *subject = req->req_info->subject;
	X509_NAME_add_entry_by_txt(subject, "C", MBSTRING_ASC, (const unsigned char *)"TW", -1, -1, 0);
    X509_NAME_add_entry_by_txt(subject, "CN", MBSTRING_ASC, (const unsigned char *)"Bill.TestCode.SSL", -1, -1, 0);
}

void req_sign(X509_REQ *req, EVP_PKEY *pk) {
	X509_REQ_set_pubkey(req, pk);
	X509_REQ_sign(req, pk, EVP_sha1());
    printf("Sign OK");
}

void req_print(X509_REQ *req) {
	BIO *out = BIO_new_fp(stdout, BIO_NOCLOSE);
	X509_REQ_print(out, req);
	BIO_free(out);
}

void req_save(X509_REQ *req, char *reqfile) {
	BIO *out = BIO_new_file(reqfile, "w");
	PEM_write_bio_X509_REQ(out, req);
	BIO_free(out);
}

int main_req(int argc, char* argv[]) {
    char *keyfile = "private.key";
    char *csrfile = "client.csr";

    X509_REQ *req = X509_REQ_new();
    EVP_PKEY *key = req_get_private_key(keyfile);
    req_setSubject(req);
    req_sign(req, key);
    req_print(req);
    req_save(req, csrfile);

    EVP_PKEY_free(key);
    X509_REQ_free(req);
    return 0;
}

