package com.mizo0203.lilywhite.repo;

import com.mizo0203.lilywhite.repo.objectify.entity.KeyEntity;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import java.util.logging.Logger;

public class Repository {

  private static final Logger LOG = Logger.getLogger(Repository.class.getName());
  private final OfyRepository mOfyRepository;
  private final PushQueueRepository mPushQueueRepository;

  public Repository() {
    mOfyRepository = new OfyRepository();
    // FIXME: getChannelSecret(), getChannelAccessToken() をクラスメソッドに変更
    mPushQueueRepository = new PushQueueRepository();
  }

  public void destroy() {
    mOfyRepository.destroy();
    mPushQueueRepository.destroy();
  }

  private void deleteReminderTask() {
    String taskName = getKey("taskName");
    if (taskName == null || taskName.isEmpty()) {
      return;
    }
    mPushQueueRepository.deleteReminderTask(taskName);
    deleteKey("taskName");
  }

  public void enqueueReminderTask(String sourceId, long etaMillis, String message) {
    String taskName = mPushQueueRepository.enqueueReminderTask(sourceId, etaMillis, message);
    LOG.info("enqueueReminderTask taskName: " + taskName);
    setKey("taskName", taskName);
  }

  public Twitter createTwitterInstance() {
    Twitter mizo0203 = new TwitterFactory().getInstance();
    String consumerKey = getKey("consumerKey");
    String consumerSecret = getKey("consumerSecret");
    String token = getKey("token");
    String tokenSecret = getKey("tokenSecret");
    mizo0203.setOAuthConsumer(consumerKey, consumerSecret);
    mizo0203.setOAuthAccessToken(new AccessToken(token, tokenSecret));
    return mizo0203;
  }

  private void setKey(String key, String value) {
    KeyEntity keyEntity = mOfyRepository.loadKeyEntity(key);
    if (keyEntity == null) {
      keyEntity = new KeyEntity();
      keyEntity.key = key;
    }
    keyEntity.value = value;
    mOfyRepository.saveKeyEntity(keyEntity);
  }

  private String getKey(String key) {
    KeyEntity keyEntity = mOfyRepository.loadKeyEntity(key);
    if (keyEntity == null) {
      keyEntity = new KeyEntity();
      keyEntity.key = key;
      keyEntity.value = "";
      mOfyRepository.saveKeyEntity(keyEntity);
    }
    if (keyEntity.value.isEmpty()) {
      LOG.severe(key + " isEmpty");
    }
    return keyEntity.value;
  }

  private void deleteKey(String key) {
    mOfyRepository.deleteKeyEntity(key);
  }
}
