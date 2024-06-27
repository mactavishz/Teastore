package tools.descartes.teastore.auth.security;

import org.junit.Assert;
import org.junit.Test;

import tools.descartes.teastore.entities.message.SessionBlob;

/**
 * Test for the SHASecurityProviuder.
 * 
 * @author Simon
 *
 */
public class ShaSecurityProviderTest {

  /**
   * checks security token behavior.
   */
  @Test
  public void test() {
    ShaSecurityProvider provider = new ShaSecurityProvider();
    SessionBlob blob = new SessionBlob();
    blob.setSid("1234");
    blob.setUid(123456L);
    Assert.assertTrue(provider.validate(blob) == null);
    provider.secure(blob);
    Assert.assertTrue(provider.validate(blob) != null);
    blob.setUid(13L);
    Assert.assertTrue(provider.validate(blob) == null);
    provider.secure(blob);
    Assert.assertTrue(provider.validate(blob) != null);
  }

}
