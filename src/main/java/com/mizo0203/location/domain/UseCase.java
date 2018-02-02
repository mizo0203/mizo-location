package com.mizo0203.location.domain;

import com.mizo0203.location.repo.Repository;
import twitter4j.Twitter;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.Closeable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class UseCase implements Closeable {
  private static final Logger LOG = Logger.getLogger(UseCase.class.getName());
  private final Repository mRepository;

  public UseCase() {
    mRepository = new Repository();
  }

  @Override
  public void close() {
    mRepository.destroy();
  }

  public void onReceiveMizoLocationMail(String name, MimeMessage message)
      throws MessagingException {
    switch (name) {
      case "enter":
        onEnter(message);
        break;
      case "exit":
        onExit(message);
        break;
      default:
        break;
    }
  }

  private void onEnter(MimeMessage message) throws MessagingException {
    String subject = message.getSubject();
    long etaMillis = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(4);
    mRepository.enqueueReminderTask("", etaMillis, subject);
  }

  private void onExit(MimeMessage message) {
    mRepository.deleteReminderTask();
  }

  public void pushReminderMessage(String source_id, String message) {
    Twitter mizo0203 = mRepository.createTwitterInstance();
    try {
      mizo0203.updateStatus("帰宅ったー");
    } catch (Exception e) {
      LOG.warning(e.getMessage());
    } finally {
      mRepository.deleteReminderTask();
    }
  }
}
