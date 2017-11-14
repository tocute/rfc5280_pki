#include <openssl/pem.h>
#include <openssl/x509v3.h>

X509 *crt_load(char* crtfile) {
    BIO *in = BIO_new(BIO_s_file());
    BIO_read_filename(in, crtfile);
    X509 *cert = PEM_read_bio_X509(in, NULL, NULL, NULL);
    BIO_free(in);
    return cert;
}

void crt_printVersion(X509 *cert) {
    BIO *out = BIO_new_fp(stdout, BIO_NOCLOSE);

    BIO_printf(out, "version:\tv%ld\n", X509_get_version(cert));
    BIO_free(out);
}
void crt_printSN(X509 *cert) {
    BIO *out = BIO_new_fp(stdout, BIO_NOCLOSE);
    BIO_printf(out, "sn:\t%02lx\n",
            ASN1_INTEGER_get(X509_get_serialNumber(cert)));
    BIO_free(out);
}
void crt_printAlg(X509 *cert) {
    BIO *out = BIO_new_fp(stdout, BIO_NOCLOSE);
    BIO_printf(out, "algo:\t");
    i2a_ASN1_OBJECT(out, cert->cert_info->signature->algorithm);
    BIO_printf(out, "\n");
    BIO_free(out);
}
void crt_printIssuer(X509 *cert) {
    BIO *out = BIO_new_fp(stdout, BIO_NOCLOSE);
    BIO_printf(out, "issuer:\t");
    X509_NAME_print(out, X509_get_issuer_name(cert), 0);
    BIO_printf(out, "\n");
    BIO_free(out);
}
void crt_printSubject(X509 *cert) {
    BIO *out = BIO_new_fp(stdout, BIO_NOCLOSE);
    BIO_printf(out, "subject:\t");
    X509_NAME_print(out, X509_get_subject_name(cert), 0);
    BIO_printf(out, "\n");
    BIO_free(out);
}
void crt_printNotBefore(X509 *cert) {
    BIO *out = BIO_new_fp(stdout, BIO_NOCLOSE);
    BIO_printf(out, "notBefore\t");
    ASN1_TIME_print(out, X509_get_notBefore(cert));
    BIO_printf(out, "\n");
    BIO_free(out);
}
void crt_printNotAfter(X509 *cert) {
    BIO *out = BIO_new_fp(stdout, BIO_NOCLOSE);
    BIO_printf(out, "notAfter\t");
    ASN1_TIME_print(out, X509_get_notAfter(cert));
    BIO_printf(out, "\n");
    BIO_free(out);
}
void crt_printValidity(X509 *cert) {
    crt_printNotBefore(cert);
    crt_printNotAfter(cert);
}

void crt_printPKInfo(X509 *cert) {
    BIO *out = BIO_new_fp(stdout, BIO_NOCLOSE);
    BIO_printf(out, "pkInfo:\t");
    i2a_ASN1_OBJECT(out, X509_get_X509_PUBKEY(cert)->algor->algorithm);
    BIO_printf(out, "\n");
    RSA_print(out, X509_get_pubkey(cert)->pkey.rsa, 8);
    BIO_printf(out, "\n");
    BIO_free(out);
}

void crt_printBasic(X509 *cert) {
    BIO *out = BIO_new_fp(stdout,BIO_NOCLOSE);
    BIO_printf(out, "-- Basic Fields Start--\n");
    crt_printVersion(cert);
    crt_printSN(cert);
    crt_printAlg(cert);
    crt_printIssuer(cert);
    crt_printValidity(cert);
    crt_printSubject(cert);
    crt_printPKInfo(cert);
    BIO_printf(out, "-- Basic Fields End --\n");
    BIO_free(out);
}

void crt_printExt(X509 *cert) {
    int count = X509_get_ext_count(cert);
    if (count > 0) {
        BIO *out = BIO_new_fp(stdout, BIO_NOCLOSE);
        BIO_printf(out, "-- Extensions Start--\n");
        int i;
        for (i = 0; i < count; i++) {
            X509_EXTENSION *ex = X509_get_ext(cert, i);
            //X509_EXTENSION_get_object(ex);
            // oid
            i2a_ASN1_OBJECT(out, X509_EXTENSION_get_object(ex));
            // critical
            BIO_printf(out, ": %s\n",X509_EXTENSION_get_critical(ex) ? "critical" : "");
            // value
            X509V3_EXT_print(out, ex, 0, 4);
            // dump HEX
            //ASN1_STRING_print(out, ex->value);
            {
                int i;
                char *p = ex->value->data;
                BIO_printf(out, "\n    ");
                for (i = 0; i < ex->value->length; i++)
                    BIO_printf(out, "%02X", p[i]&255);
                BIO_printf(out, "\n");
            }
            BIO_printf(out, "\n");
        }
        BIO_printf(out, "-- Extensions End --\n");
        BIO_free(out);
    }
}

int main_crt(int argc, char* argv[]) {
    char *crt = "client.crt";

    X509 *cert2 = crt_load(crt);
    crt_printBasic(cert2);
    crt_printExt(cert2);
    X509_free(cert2);
    return 0;
}

