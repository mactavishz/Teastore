/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tools.descartes.teastore.imagegenerator.setup;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.descartes.teastore.entities.Category;
import tools.descartes.teastore.entities.ImageSizePreset;
import tools.descartes.teastore.entities.Product;
import tools.descartes.teastore.imagegenerator.ImageDB;
import tools.descartes.teastore.imagegenerator.StoreImage;

/**
 * Image provider setup class. Connects to the persistence service to collect all available products and generates
 * images from the received products and their category. Searches for existing images to be used in the web interface
 * and adds them to the storage / cache.
 * @author Norbert Schmitt
 */
public enum SetupController {

  /**
   * Instance of the setup controller.
   */
  SETUP;

  /**
   * Constants used during image provider setup.
   * @author Norbert Schmitt
   */
  private interface SetupControllerConstants {

	/**
	 * Standard working directory in which the images are stored.
	 */
    public static final Path STD_WORKING_DIR = Paths.get("images_static");


    /**
     * Number of available logical cpus for image creation.
     */
    public static final int CREATION_THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();

  }

  private Path workingDir = SetupControllerConstants.STD_WORKING_DIR;
  private long nrOfImagesToGenerate = 0;
  private final AtomicLong nrOfImagesGenerated = new AtomicLong();
  private final HashMap<String, BufferedImage> categoryImages = new HashMap<>();
  private final ImageDB imgDB = new ImageDB();
  private final ScheduledThreadPoolExecutor imgCreationPool = new ScheduledThreadPoolExecutor(
      SetupControllerConstants.CREATION_THREAD_POOL_SIZE);
  private final Logger log = LoggerFactory.getLogger(SetupController.class);

  private SetupController() {

  }


  private void fetchProductsForCategoryStatic(String url, Category category, HashMap<Category, List<Long>> products) {

    Connection connection = null;
    ResultSet resultSet = null;
    Statement statement = null;
    List<Long> productsList = new ArrayList<>();
    try{
      connection = DriverManager.getConnection(url);
      statement = connection.createStatement();
      log.info(String.format("Executing QUERY: SELECT * FROM PERSISTENCEPRODUCT WHERE CATEGORY_ID = %d", category.getId()));
      resultSet = statement.executeQuery(String.format("SELECT * FROM PERSISTENCEPRODUCT WHERE CATEGORY_ID = %d", category.getId()));
      while (resultSet.next()) {
        Product newProd = new Product();
        long id = resultSet.getLong("ID");
        productsList.add(id);

      }
      log.info(String.format("Fetched Products for category id %d", category.getId()));
      products.put(category, productsList);
    }
    catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        resultSet.close();
        statement.close();
        connection.close();
      } catch (Exception e) {
        e.printStackTrace();
      }

    }


  }


  private List<Category> fetchCategoriesStatic(String url) {

    List<Category> categories = new ArrayList<>();
    Connection connection = null;
    ResultSet resultSet = null;
    Statement statement = null;
    try{
      System.out.println(url);
      connection = DriverManager.getConnection(url);
      statement = connection.createStatement();
      log.info("Executing QUERY:  SELECT * FROM PERSISTENCECATEGORY");
      resultSet = statement.executeQuery("SELECT * FROM PERSISTENCECATEGORY");
      while (resultSet.next()) {
        Category newCat = new Category();
        long id = resultSet.getLong("ID");
        String desc = resultSet.getString("DESCRIPTION");
        String name = resultSet.getString("NAME");
        newCat.setId(id);
        newCat.setDescription(desc);
        newCat.setName(name);
        categories.add(newCat);

      }
    }
    catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        resultSet.close();
        statement.close();
        connection.close();
      } catch (Exception e) {
        e.printStackTrace();
      }

    }
    log.info("Fetched Categories");
    return categories;

  }


  private HashMap<Category, BufferedImage> matchCategoriesToImage(List<Category> categories) {
    HashMap<Category, BufferedImage> result = new HashMap<>();

    List<String> imageNames = categoryImages.entrySet().stream().map(e -> e.getKey())
        .collect(Collectors.toList());
    for (String name : imageNames) {
      for (Category category : categories) {
        String[] tmp = category.getName().split(",");
        if (tmp[0].toLowerCase().replace(" ", "-").equals(name)) {
          log.info("Found matching category {} ({}) for image {}.", category.getName(),
              category.getId(), name + "." + StoreImage.STORE_IMAGE_FORMAT);
          result.put(category, categoryImages.get(name));
        }
      }
    }
    return result;
  }



  public void generateImagesStatic(String url) {
    List<Category> categories = fetchCategoriesStatic(url);
    HashMap<Category, List<Long>> products = new HashMap<>();
    categories.forEach(cat -> fetchProductsForCategoryStatic(url, cat, products));

    generateImages(products, matchCategoriesToImage(categories));
  }



  /**
   * Generates images for the given product IDs and categories.
   * @param products Map of categories and the corresponding products.
   * @param categoryImages Category image representing a specific category.
   */
  public void generateImages(Map<Category, List<Long>> products,
      Map<Category, BufferedImage> categoryImages) {
    nrOfImagesToGenerate = products.entrySet().stream().flatMap(e -> e.getValue().stream()).count();

    CreatorFactory factory = new CreatorFactory(ImageCreator.STD_NR_OF_SHAPES_PER_IMAGE, imgDB,
        ImageSizePreset.STD_IMAGE_SIZE, workingDir, products, categoryImages, nrOfImagesGenerated);

    // Schedule all image creation tasks
    for (long i = 0; i < nrOfImagesToGenerate; i++) {
      imgCreationPool.execute(factory.newRunnable());
    }

    log.info("Image creator thread started. {} {} sized images to generate using {} threads.",
        nrOfImagesToGenerate, ImageSizePreset.STD_IMAGE_SIZE.toString(),
        SetupControllerConstants.CREATION_THREAD_POOL_SIZE);

    imgCreationPool.shutdown();
    try {
      if (!imgCreationPool.awaitTermination(60, TimeUnit.SECONDS)) {
        imgCreationPool.shutdownNow();
      }
      log.info("All image creation tasks have completed.");
    } catch (InterruptedException ie) {
      imgCreationPool.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Create the working directory in which all generated images are stored if it is not existing.
   */
  public void createWorkingDir() {
    if (!workingDir.toFile().exists()) {
      if (!workingDir.toFile().mkdir()) {
        log.error("Standard working directory \"" + workingDir.toAbsolutePath()
            + "\" could not be created.");
        throw new IllegalArgumentException("Standard working directory \""
            + workingDir.toAbsolutePath() + "\" could not be created.");
      } else {
        log.info("Working directory {} created.", workingDir.toAbsolutePath().toString());
      }
    } else {
      log.info("Working directory {} already existed.", workingDir.toAbsolutePath().toString());
    }
  }



  public static boolean deleteDirectory(File directory) {
    if (directory == null || !directory.exists()) {
      return false;
    }

    if (directory.isDirectory()) {
      File[] files = directory.listFiles();
      if (files != null) {
        for (File file : files) {
          if (file.isDirectory()) {
            deleteDirectory(file);
          } else {
            file.delete();
          }
        }
      }
    }
    return directory.delete();
  }

  /**
   * Deletes the current working directory.
   */
  public void deleteWorkingDir() {
    File currentDir = workingDir.toFile();
    boolean isDeleted = false;

    if (currentDir.exists() && currentDir.isDirectory()) {
      isDeleted = deleteDirectory(currentDir);
    }

    if (isDeleted) {
      log.info("Deleted working directory {}.", workingDir.toAbsolutePath().toString());
    } else {
      log.info("Working directory {} not deleted.", workingDir.toAbsolutePath().toString());
    }
  }

  public Path getWorkingDir() {
    return workingDir;
  }


}
