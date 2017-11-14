#include <openssl/pem.h>

void crt_printName(X509 *cert) {
    BIO *out = BIO_new_fp(stdout, BIO_NOCLOSE);
    BIO_printf(out, "issuer:\t");
    X509_NAME_print(out, X509_get_issuer_name(cert), 0);
    BIO_printf(out, "\nsubject:\t");
    X509_NAME_print(out, X509_get_subject_name(cert), 0);
    BIO_printf(out, "\n");
    BIO_free(out);
}

int verify_callback(int ok, X509_STORE_CTX *stor) {
    if (!ok)
        fprintf(stderr, "\nError: %s\n", X509_verify_cert_error_string(stor->error));
    return ok;
}

int crt_vfy(X509 *cert, STACK_OF(X509) *chain, char *cafile, char *cadir)
{
    /* create the cert store and set the verify callback */
    X509_STORE *store = X509_STORE_new();
    X509_STORE_set_verify_cb_func(store, verify_callback);
    
    /* load the CA certificates and CRLs */
    X509_STORE_load_locations(store, cafile, cadir);
    X509_STORE_set_default_paths(store);
    
    /* create a verification context and initialize it */
    X509_STORE_CTX *verify_ctx = X509_STORE_CTX_new();
    X509_STORE_CTX_init(verify_ctx, store, cert, chain);
    
    /* verify the certificate */
    if (X509_verify_cert(verify_ctx) != 1)
        return 0;
    else
        return 1;
}

int p7b_verify(PKCS7 *p7, char *cafile, char *cadir)
{
    STACK_OF(X509) *chain = p7->d.sign->cert;
    for (int i = 0; i < sk_X509_num(chain); i++)
    {
        X509 *x = sk_X509_value(chain, i);
        int j = X509_NAME_cmp(X509_get_issuer_name(x),
                              X509_get_subject_name(x));
//        if (j==0)
//        {
//            // save to temp file...
//            BIO *tmp = BIO_new_file("temp.crt", "w");
//            PEM_write_bio_X509(tmp, sk_X509_value(chain,i));
//            BIO_free(tmp);
//            BIO *out = BIO_new_fp(stdout,BIO_NOCLOSE);
//            printf("%d\n",j);
//            X509_NAME_print(out, X509_get_issuer_name(sk_X509_value(chain,i)), 0);
//            BIO_printf(out,"\n");
//            X509_NAME_print(out, X509_get_subject_name(sk_X509_value(chain,i)), 0);
//            break;
//        }
        BIO *out = BIO_new_fp(stdout,BIO_NOCLOSE);
        BIO_printf(out, "\ncert[%d]\n", i);
        X509_NAME_print(out, X509_get_issuer_name(x), 0);;

        if (!crt_vfy(x, chain, cafile, cadir))
            return 0;
    }
    return 1;
}


void p7b_print(PKCS7 *p7) {
    BIO *out = BIO_new_fp(stdout, BIO_NOCLOSE);
    STACK_OF(X509) *chain = p7->d.sign->cert;

    for (int i = 0; i < sk_X509_num(chain); i++) {
        X509 *x= sk_X509_value(chain, i);
        BIO_printf(out, "\ncert[%d]\n", i);
        crt_printName(x);
    }
    BIO_free(out);
}

PKCS7 *p7b_load(char *p7bfile , int format_type)
{   // informat: 1=der, 3=pem
    BIO *in = BIO_new(BIO_s_file());
    BIO_read_filename(in, p7bfile);
    PKCS7 *p7 = NULL;
    if(format_type == 3) // PEM
        p7 = PEM_read_bio_PKCS7(in, NULL, NULL, NULL);
    else if(format_type == 1) // DER
        p7 = d2i_PKCS7_bio(in, NULL);
    BIO_free(in);
    return p7;
}

PKCS7 * genP7b(PKCS7 *p7b_chain, X509 *cert, EVP_PKEY *pkey, char *msg)
{
    PKCS7* p7 = PKCS7_new();

    PKCS7_set_type(p7, NID_pkcs7_signed);
    PKCS7_content_new(p7, NID_pkcs7_data);
    PKCS7_set_detached(p7, 0);

    //添加签名者信息，
    //x509：签名证书，pkey：签名者私钥。EVP_sha1()签名者摘要算法。
    PKCS7_SIGNER_INFO* info = PKCS7_add_signature(p7, cert, pkey, EVP_sha1());

    //添加签名者证书
    PKCS7_add_certificate(p7, cert);

    //添加签名者的CA证书链
    STACK_OF(X509) *chain = p7b_chain->d.sign->cert;
    for (int i=0; i<sk_X509_num(chain); i++)
    {
        PKCS7_add_certificate(p7, sk_X509_value(chain, i));
    }
    
    if(msg != NULL)
    {
        BIO* p7bio = PKCS7_dataInit(p7, NULL);
        BIO_write(p7bio, msg, strlen(msg));//加入原始数据，
        PKCS7_dataFinal(p7, p7bio); //处理数据。
        BIO_free(p7bio);
    }

    BIO *out = BIO_new_file("temp.p7b", "w+");
    PEM_write_bio_PKCS7(out, p7);
    BIO_free(out);
    printf("\n Generate P7b OK\n");
    //转换为der编码输出
    //i2d_PKCS7(p7,&dertmp);

    //PKCS7_free(p7);
    return p7;
}

void parseP7b(PKCS7 *p7b)
{
    //解析P7签名的代码：
    //der编码转换为PKCS7结构体
    //PKCS7 * p7 = d2i_PKCS7(NULL,dertmp,derLen)
    
    //解析出原始数据
    BIO *p7bio= PKCS7_dataDecode(p7b, NULL, NULL, NULL);
    
    //从BIO中读取原始数据,将得到"How are you!"
    char src[1024];
    int srcLen = BIO_read(p7bio,src,1024);
    printf("msg %s\n",src);
    
    BIO *out = BIO_new_fp(stdout, BIO_NOCLOSE);
    
    //获得签名者信息stack
    STACK_OF(PKCS7_SIGNER_INFO) *sk = PKCS7_get_signer_info(p7b);
    
    //获得签名者个数(本例只有1个)
    int signCount = sk_PKCS7_SIGNER_INFO_num(sk);
    
    for(int i=0; i<signCount; i++)
    {
        BIO_printf(out, "\ncert[%d]\n", i);
        //获得签名者信息
        PKCS7_SIGNER_INFO *signInfo = sk_PKCS7_SIGNER_INFO_value(sk,i);
        
        //获得签名者证书
        X509 *cert= PKCS7_cert_from_signer_info(p7b, signInfo);
        X509_NAME_print(out, X509_get_subject_name(cert), 0);
        
        //验证签名
        if(PKCS7_signatureVerify(p7bio, p7b, signInfo, cert) != 1)
        {
            printf("\nsignatureVerify Err\n");
        }
        else
        {
            printf("\nsignatureVerify OK\n");
        }
    }
    
    BIO_free(out);
}

int main(int argc, char* argv[]) {
    char* p7b_fn = "root_client.p7b";
    OpenSSL_add_all_algorithms();
    PKCS7 *p7 = p7b_load(p7b_fn, 1);
    
    BIO* bio_key = BIO_new(BIO_s_file());
    BIO_read_filename(bio_key, "private.key");
    EVP_PKEY *pk = PEM_read_bio_PrivateKey(bio_key, NULL, NULL, NULL);
    BIO_free(bio_key);
    
    BIO *bio_cert = BIO_new(BIO_s_file());
    BIO_read_filename(bio_cert, "service_client.cer");
    //X509 *cert = PEM_read_bio_X509(bio_cert, NULL, NULL, NULL);
    X509 *cert = d2i_X509_bio(bio_cert,NULL);//(bio_cert, NULL, NULL, NULL);

    PKCS7 *p7b_test = genP7b(p7, cert, pk, "Yes123!");
    
//    char* p7b_fn2 = "temp.p7b";
//    PKCS7 *p7b_test = p7b_load(p7b_fn2, 3);
    parseP7b(p7b_test);
    
    //    p7b_print(p7);
//
//    if (p7b_verify(p7, "root_service.cer", NULL))
//        printf("\nverify ok\n");
//    else
//        printf("\nverify fail\n");

    PKCS7_free(p7);
    return 0;
}

