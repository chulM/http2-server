package com.chulm.http2.server;

import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http2.Http2SecurityUtil;
import io.netty.handler.ssl.*;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CertificateException;


public class SslContextProvider {
    private SocketChannel sc;
    private static SslContext sslCtx;
    private static String certPath;
    private static String certPassword;



    public static SslContext getSelfSignedSslContext() throws SSLException, CertificateException {

        SslContextBuilder builder = SslContextBuilder.forClient()
                                                     .trustManager(InsecureTrustManagerFactory.INSTANCE);
        SslProvider provider = OpenSsl.isAlpnSupported() ? SslProvider.OPENSSL : SslProvider.JDK;

        SslContext sslCtx = builder
                .sslProvider(provider)
                .ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
                .applicationProtocolConfig(new ApplicationProtocolConfig(
                        ApplicationProtocolConfig.Protocol.ALPN,
                        ApplicationProtocolConfig.SelectorFailureBehavior.FATAL_ALERT,
                        ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT,
                        ApplicationProtocolNames.HTTP_2,
                        ApplicationProtocolNames.HTTP_1_1))
                .build();

        return sslCtx;
    }

    /**
     * Only JKS File
     */

    public static SslContext createSslContext() {

        try {
            //SelfSignedCertificate
//            SelfSignedCertificate ssc = new SelfSignedCertificate();

            SslProvider provider = OpenSsl.isAlpnSupported() ? SslProvider.OPENSSL : SslProvider.JDK;
            System.out.println("openSSl support:" + OpenSsl.isAlpnSupported());

            String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
            if (algorithm == null) {
                algorithm = "SunX509";
            }
            KeyStore ks = KeyStore.getInstance("JKS");

            ks.load(new FileInputStream(certPath),certPassword.toCharArray());
            final KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
            kmf.init(ks, certPassword.toCharArray());

            sslCtx = SslContextBuilder.forServer(kmf)
                    .sslProvider(provider)
                    .ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
                    .applicationProtocolConfig(new ApplicationProtocolConfig(
                            ApplicationProtocolConfig.Protocol.ALPN, ApplicationProtocolConfig.SelectorFailureBehavior.NO_ADVERTISE,
                            ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT,
                            ApplicationProtocolNames.HTTP_2,
                            ApplicationProtocolNames.HTTP_1_1))
                    .build();

        } catch (Exception e) {
            sslCtx = null;
            e.printStackTrace();
        }
        return sslCtx;
    }

    public static void setCertPath(String certPath) {
        SslContextProvider.certPath = certPath;
    }

    public static void setCertPassword(String certPassword) {
        SslContextProvider.certPassword = certPassword;
    }
}
