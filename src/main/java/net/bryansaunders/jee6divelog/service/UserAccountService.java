/**
 * 
 */
package net.bryansaunders.jee6divelog.service;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;

import net.bryansaunders.jee6divelog.dao.user.UserAccountDao;
import net.bryansaunders.jee6divelog.model.UserAccount;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User Service for Working with User.
 * 
 * @author Bryan Saunders <btsaunde@gmail.com>
 * 
 */
@Stateless
public class UserAccountService {
    
    /**
     * Logger.
     */
    private Logger logger = LoggerFactory.getLogger(UserAccountService.class);

    /**
     * User DAO.
     */
    @Inject
    private UserAccountDao userDao;

    /**
     * EJB Context for Transaction Rollback.
     */
    @Resource
    private EJBContext context;

    /**
     * Creates a new User.
     * 
     * @param user
     *            User to Create.
     * @return Saved user object, or null if an error occured.
     */
    public UserAccount createUser(final UserAccount user) {
        UserAccount savedUser = null;

        try {
            savedUser = this.userDao.save(user);
        } catch (ConstraintViolationException e) {
            this.context.setRollbackOnly();
        }

        return savedUser;
    }

    /**
     * Find a User by their Username. NoResultException thrown if the user is not found.
     * 
     * @param username
     *            Username to search for
     * @return User if found, null if not
     */
    public UserAccount findByUserEmail(final String username) {
        final EntityManager entityManager = this.userDao.getEntityManager();
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        final CriteriaQuery<UserAccount> criteriaQuery = criteriaBuilder.createQuery(UserAccount.class);
        final Root<UserAccount> root = criteriaQuery.from(UserAccount.class);
        criteriaQuery.select(root);

        final ParameterExpression<String> usernameParam = criteriaBuilder.parameter(String.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("email"), usernameParam));

        final TypedQuery<UserAccount> query = entityManager.createQuery(criteriaQuery);
        query.setParameter(usernameParam, username);

        UserAccount foundAccount = null;
        try {
            foundAccount = query.getSingleResult();
        } catch (NoResultException e) {
            this.logger.info("Could not Find UserAccount for Username: " + username);
        }
        
        return foundAccount;
    }

}