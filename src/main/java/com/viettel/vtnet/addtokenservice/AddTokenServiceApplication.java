package com.viettel.vtnet.addtokenservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> 46a6f09 (brk origin)
import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

<<<<<<< HEAD
@SpringBootApplication
public class AddTokenServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(AddTokenServiceApplication.class, args);
    disableSSLCertificateChecking();
  }


  /**
   * Disables the SSL certificate checking for new instances of {@link HttpsURLConnection} This has been created to
   * aid testing on a local box, not for use on production.
   */
  private static void disableSSLCertificateChecking() {
    TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
      public X509Certificate[] getAcceptedIssuers() {
        return null;
      }

      @Override
      public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        // Not implemented
      }

      @Override
      public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        // Not implemented
      }
    } };

    try {
      SSLContext sc = SSLContext.getInstance("SSL");

      sc.init(null, trustAllCerts, new java.security.SecureRandom());

      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

      // Create all-trusting host name verifier
      HostnameVerifier allHostsValid = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
          return true;
        }
      };

      // Install the all-trusting host verifier
      HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    } catch (KeyManagementException | NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
  }
=======
=======
>>>>>>> 46a6f09 (brk origin)
@SpringBootApplication
public class AddTokenServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(AddTokenServiceApplication.class, args);
    disableSSLCertificateChecking();
  }

<<<<<<< HEAD
>>>>>>> a7d5f61 (Initial commit)
=======

  /**
   * Disables the SSL certificate checking for new instances of {@link HttpsURLConnection} This has been created to
   * aid testing on a local box, not for use on production.
   */
  private static void disableSSLCertificateChecking() {
    TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
      public X509Certificate[] getAcceptedIssuers() {
        return null;
      }

      @Override
      public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        // Not implemented
      }

      @Override
      public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        // Not implemented
      }
    } };

    try {
      SSLContext sc = SSLContext.getInstance("SSL");

      sc.init(null, trustAllCerts, new java.security.SecureRandom());

      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

      // Create all-trusting host name verifier
      HostnameVerifier allHostsValid = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
          return true;
        }
      };

      // Install the all-trusting host verifier
      HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    } catch (KeyManagementException | NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
  }
>>>>>>> 46a6f09 (brk origin)
}
