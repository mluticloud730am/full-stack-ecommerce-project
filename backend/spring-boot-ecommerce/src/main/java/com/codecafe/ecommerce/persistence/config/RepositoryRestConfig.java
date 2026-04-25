package com.codecafe.ecommerce.persistence.config;

import com.codecafe.ecommerce.persistence.entity.Product;
import com.codecafe.ecommerce.persistence.entity.ProductCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Configuration
public class RepositoryRestConfig implements RepositoryRestConfigurer {

    @Autowired
    private EntityManager entityManager;

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {

        // ✅ CORS fix — allow Angular frontend to call backend
        cors.addMapping("/api/**")
            .allowedOrigins("http://54.152.221.95:84")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*");

        // Disable unsafe methods
        HttpMethod[] unsupportedActions = {
            HttpMethod.PUT, HttpMethod.POST, HttpMethod.DELETE, HttpMethod.PATCH
        };
        disableHttpMethods(Product.class, config, unsupportedActions);
        disableHttpMethods(ProductCategory.class, config, unsupportedActions);

        // Expose IDs
        exposeIds(config);
    }

    private void disableHttpMethods(Class theClass,
                                    RepositoryRestConfiguration config,
                                    HttpMethod[] unsupportedActions) {
        config.getExposureConfiguration()
              .forDomainType(theClass)
              .withItemExposure((meta, httpMethods) -> httpMethods.disable(unsupportedActions))
              .withCollectionExposure((meta, httpMethods) -> httpMethods.disable(unsupportedActions));
    }

    private void exposeIds(RepositoryRestConfiguration config) {
        Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();
        List<Class> entityClasses = new ArrayList<>();
        for (EntityType<?> entity : entities) {
            entityClasses.add(entity.getJavaType());
        }
        config.exposeIdsFor(entityClasses.toArray(new Class[0]));
    }
}
