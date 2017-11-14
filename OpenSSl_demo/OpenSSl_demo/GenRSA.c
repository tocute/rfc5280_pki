#include <openssl/rsa.h>
#include <openssl/pem.h>

RSA * genRSA(int bits)
{
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
    
    // 2. save public key
    BIO *bp_public = BIO_new_file("public.key", "w+");
    ret = PEM_write_bio_RSAPublicKey(bp_public, rsa);
    if(ret != 1){
        printf("Generate PublicKey Fail !!");
    }
    BIO_free(bp_public);
    
    // 3. save private key
    BIO *bp_private = BIO_new_file("private.key", "w+");
    ret = PEM_write_bio_RSAPrivateKey(bp_private, rsa, NULL, NULL, 0, NULL, NULL);
    BIO_free(bp_private);

    

    // 4. free
    BN_free(bne);
    return rsa;
}
