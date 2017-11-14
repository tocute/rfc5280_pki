#include <openssl/rsa.h>
#include <openssl/pem.h>


RSA *rsa_genkey(int bits)
{
//    RSA *rsa = RSA_generate_key(bit, RSA_F4, callback, stdout);
//
    
    // 1. generate rsa key
    BIGNUM *bne = BN_new();
    int ret = BN_set_word(bne, RSA_F4);
    if(ret != 1){
        printf("Generate BN Fail !!");
    }
    
    RSA *rsa = RSA_new();
    ret = RSA_generate_key_ex(rsa, bits, bne, NULL);
    if(ret != 1){
        printf("Generate RSA Fail !!");
    }
    
    // 4. free
    BN_free(bne);
    return rsa;
}

void rsa_print(RSA *rsa) {
    BIO *out = BIO_new_fp(stdout, BIO_NOCLOSE);
    BIO_printf(out, "---- RSA KeySize = %d bytes ----\n", RSA_size(rsa));
    RSA_print(out, rsa, 0);
    BIO_free(out);
}

void rsa_save(RSA *rsa, char *filename) {
    //BIO *out = BIO_new_fp(stdout, BIO_NOCLOSE);
    //BIO_write_filename(out, filename);
    BIO *out = BIO_new_file("private.key", "w+");
    
    //PEM_write_bio_RSAPublicKey(bp_public, rsa);
    PEM_write_bio_RSAPrivateKey(out, rsa, NULL, NULL, 0, 0, NULL);
    BIO_free(out);
}

int main_key(int argc, char* argv[]) {
    int keylength = 1024;
    char *filename = "private.key";

    RSA *rsa = rsa_genkey(keylength);
    rsa_print(rsa);
    rsa_save(rsa, filename);

    RSA_free(rsa);
    return 0;
}
