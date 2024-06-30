package tools.descartes.teastore.model.repository;

import jakarta.persistence.EntityManager;
import tools.descartes.teastore.model.domain.CategoryRepository;
import tools.descartes.teastore.model.domain.UserRepository;

import java.util.List;

public class DataGeneratorUtil {
    /**
	 * Checks if the database is empty.
	 *
	 * @return True if the database is empty.
	 */
	public static boolean isDatabaseEmpty() {
		// every other entity requires a valid category or user
		return (CategoryRepository.REPOSITORY.getAllEntities(-1, 1).size() == 0
				&& UserRepository.REPOSITORY.getAllEntities(-1, 1).size() == 0);
	}

    public static void setGenerationFinishedFlag(boolean flag) {
		EntityManager em = CategoryRepository.REPOSITORY.getEM();
		try {
			em.getTransaction().begin();
			List<DatabaseManagementEntity> entities =
					em.createQuery("SELECT u FROM "
							+ DatabaseManagementEntity.class.getName()
							+ " u", DatabaseManagementEntity.class)
					.getResultList();
			if (entities == null || entities.isEmpty()) {
				DatabaseManagementEntity entity = new DatabaseManagementEntity();
				entity.setFinishedGenerating(flag);
				em.persist(entity);
			} else {
				DatabaseManagementEntity entity = entities.get(0);
				entity.setFinishedGenerating(flag);
			}
			em.getTransaction().commit();
		} finally {
			em.close();
		}
	}

	/**
	 * Returns true if the database has finished generating.
	 * False if it is currently generating.
	 * @return False if the database is generating.
	 */
	public static boolean getGenerationFinishedFlag() {
		boolean finishedGenerating = false;
		EntityManager em = CategoryRepository.REPOSITORY.getEM();
		try {
			List<DatabaseManagementEntity> entities =
					em.createQuery("SELECT u FROM "
							+ DatabaseManagementEntity.class.getName()
							+ " u", DatabaseManagementEntity.class)
					.getResultList();
			if (entities != null && !entities.isEmpty()) {
				finishedGenerating = entities.get(0).isFinishedGenerating();
			}
		} finally {
			em.close();
		}
		return finishedGenerating;
	}
}
