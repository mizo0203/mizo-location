package com.mizo0203.lilywhite.domain;

import com.mizo0203.lilywhite.repo.Repository;
import twitter4j.Twitter;

import javax.mail.internet.MimeMessage;
import java.io.Closeable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class UseCase implements Closeable {
  private static final Logger LOG = Logger.getLogger(UseCase.class.getName());
  private final Repository mRepository;
  private final Translator mTranslator;

  public UseCase() {
    mRepository = new Repository();
    mTranslator = new Translator();
  }

  @Override
  public void close() {
    mRepository.destroy();
  }

  public void onReceiveMizoLocationMail(String name, MimeMessage message) {
    Twitter mizo0203 = mRepository.createTwitterInstance();
    try {
      String subject = message.getSubject();

      String loc = mizo0203.showUser(mizo0203.getId()).getLocation();
      LOG.info("loc: " + loc);

      long etaMillis = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5);
      mRepository.enqueueReminderTask(loc, etaMillis, subject);

      //      if (subject.indexOf("自宅") != -1) {
      //        if (subject.indexOf("到着") != -1) {
      //          mizo0203.updateStatus("【みぞろけ】帰宅なう");
      //          mizo0203.updateProfile(null, null, "自宅", null);
      //        } else if (subject.indexOf("出発") != -1) {
      //          mizo0203.updateStatus("【みぞろけ】家を出たなう");
      //          mizo0203.updateProfile(null, null, "移動中", null);
      //        }
      //
      //      } else if (subject.indexOf("職場") != -1) {
      //        if (subject.indexOf("到着") != -1) {
      //          if (!loc.equalsIgnoreCase("会社")) {
      //            mizo0203.updateStatus("【みぞろけ】出社なう");
      //            mizo0203.updateProfile(null, null, "会社", null);
      //          }
      //        }
      //      } else if (subject.indexOf("門真市駅") != -1) {
      //        if (subject.indexOf("到着") != -1) {
      //          if (loc.equalsIgnoreCase("会社")) {
      //            mizo0203.updateStatus("【みぞろけ】退社なう");
      //            mizo0203.updateProfile(null, null, "移動中", null);
      //          }
      //        }
      //      }

    } catch (Exception e) {
      LOG.warning(e.getMessage());
    }
  }
}
