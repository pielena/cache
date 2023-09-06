package ru.otus.crm.service;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.base.AbstractHibernateTest;
import ru.otus.cachehw.MyCache;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DbServiceClientCacheTest extends AbstractHibernateTest {
    private static final Logger log = LoggerFactory.getLogger(DbServiceClientCacheTest.class);

    @Test
    public void shouldBeFasterWithCache() {
        DBServiceClient dbServiceClientBare = new DbServiceClientImpl(transactionManager, clientTemplate, null);
        List<Long> ids = addTestData(dbServiceClientBare);
        long withoutCacheTime = measureTime(dbServiceClientBare, ids);

        DBServiceClient dbServiceClientCached = new DbServiceClientImpl(transactionManager, clientTemplate, new MyCache<>());
        ids = addTestData(dbServiceClientCached);
        long withCacheTime = measureTime(dbServiceClientCached, ids);

        log.info("time without cache: {}", withoutCacheTime);
        log.info("time with cache: {}", withCacheTime);

        assertThat(withoutCacheTime).isGreaterThan(withCacheTime);
    }

    @Test
    public void shouldClearCache() {
        MyCache<String, Client> myCache = new MyCache<>();
        DBServiceClient dbServiceClientWithCache = new DbServiceClientImpl(transactionManager, clientTemplate, myCache);

        int iteration = 0;
        int count = 5000;

        for (int i = 0; i < count; i++) {
            Client client = new Client((long) i, "Client#" + i, new Address(null, "Street#" + i),
                    List.of(new Phone(null, "123-45-6" + i), new Phone(null, "765-43-2" + i)));

            dbServiceClientWithCache.saveClient(client);

            if (myCache.getCurrentSize() == 1) {
                iteration++;
            }
        }

        log.info("Cache was cleared {} times", iteration-1);
        assertThat(iteration).isGreaterThan(1);
    }

    private List<Long> addTestData(DBServiceClient dbServiceClient) {
        List<Long> ids = new ArrayList<>();
        int count = 1000;
        for (int i = 0; i < count; i++) {
            Client client = new Client((long) i, "Client#" + i, new Address(null, "Street#" + i),
                    List.of(new Phone(null, "123-45-6" + i), new Phone(null, "765-43-2" + i)));
            ids.add(dbServiceClient.saveClient(client).getId());
        }
        return ids;
    }

    private long measureTime(DBServiceClient dbServiceClient, List<Long> ids) {
        long start = System.currentTimeMillis();
        for (Long id : ids) {
            dbServiceClient.getClient(id);
        }
        long end = System.currentTimeMillis();
        return end - start;
    }
}