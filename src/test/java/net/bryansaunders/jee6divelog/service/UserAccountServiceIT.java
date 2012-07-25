/**
 * 
 */
package net.bryansaunders.jee6divelog.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import javax.inject.Inject;

import net.bryansaunders.jee6divelog.DefaultDeployment;
import net.bryansaunders.jee6divelog.model.UserAccount;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Bryan Saunders <btsaunde@gmail.com>
 *
 */
@RunWith(Arquillian.class)
public class UserAccountServiceIT {
    
    /**
     * User Service.
     */
    @Inject
    private UserAccountService userService;
    
    /**
     * Creates Arquillian Deployment Container.
     * 
     * @return deployment container
     */
    @Deployment
    public static WebArchive createDeployment() {
        return DefaultDeployment.getDefaultDeployment();
    }
    
    /**
     * Tests the Producer was properly injected.
     */
    @Test
    public void testInjection() {
        assertNotNull(this.userService);
    }

    /**
     * Test method for createUser().
     */
    @Test
    public void testCreateUser() {
        // given
        final UserAccount validUser = new UserAccount();
        validUser.setFirstName("Bryan");
        validUser.setLastName("Saunders");
        validUser.setEmail("btsaunde@gmail.com");
        validUser.setPassword("pass123");
        
        // when
        UserAccount savedUser = this.userService.createUser(validUser);
        
        // then
        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals(savedUser, validUser);
    }
    
    /**
     * Test for findByUsername().
     */
    @Test
    public void ifFoundByUsernameThenRetrieve() {
        // given
        final String email = "test@test.com";
        final UserAccount validUser = new UserAccount();
        validUser.setFirstName("Bryan");
        validUser.setLastName("Saunders");
        validUser.setEmail(email);
        validUser.setPassword("pass123");
        final UserAccount savedUser = this.userService.createUser(validUser);

        // when
        final UserAccount retrievedUser = this.userService.findByUserEmail(email);

        // then
        assertNotNull(retrievedUser);
        assertEquals(email, retrievedUser.getEmail());
        assertEquals(savedUser.getId(), retrievedUser.getId());
    }

    /**
     * Test for findByUsername().
     */
    @Test(expected = Exception.class)
    public void ifNotFoundByUsernameThenError() {
        // given

        // when
        this.userService.findByUserEmail("RandomUser");

        // then
        fail("Should have thrown EntityNotFoundException");
    }

}
