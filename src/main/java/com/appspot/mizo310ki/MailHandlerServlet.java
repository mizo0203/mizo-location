package com.appspot.mizo310ki;

import com.mizo0203.lilywhite.domain.UseCase;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class MailHandlerServlet extends HttpServlet {
  private static final Logger LOG = Logger.getLogger(MailHandlerServlet.class.getName());

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    try {
      String name = req.getRequestURI();
      name = name.substring(name.lastIndexOf('/') + 1, name.lastIndexOf('@'));
      LOG.info("name:" + name);
      Properties props = new Properties();
      Session session = Session.getDefaultInstance(props, null);
      MimeMessage message = new MimeMessage(session, req.getInputStream());
      // log.info("ContentType:" + message.getContentType());
      Address addresses[] = message.getFrom();
      for (Address address : addresses) {
        LOG.info("from: " + address.toString());
      }

      LOG.info("Subject: " + message.getSubject());

      try (UseCase useCase = new UseCase()) {
        //        if (addresses[0].toString().indexOf("noreply@insideicloud.icloud.com") != -1) {
        useCase.onReceiveMizoLocationMail(name, message);
        //        }
      }

    } catch (MessagingException e) {
      LOG.warning(e.getMessage());
    }
  }
}
