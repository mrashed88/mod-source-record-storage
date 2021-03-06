package org.folio.services;

import java.util.Collection;
import java.util.Optional;

import org.folio.dao.SnapshotDao;
import org.folio.rest.jaxrs.model.Snapshot;
import org.folio.rest.jaxrs.model.SnapshotCollection;
import org.jooq.Condition;
import org.jooq.OrderField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.vertx.core.Future;

@Service
public class SnapshotServiceImpl implements SnapshotService {

  private final SnapshotDao snapshotDao;

  @Autowired
  public SnapshotServiceImpl(final SnapshotDao snapshotDao) {
    this.snapshotDao = snapshotDao;
  }

  @Override
  public Future<SnapshotCollection> getSnapshots(Condition condition, Collection<OrderField<?>> orderFields,
      int offset, int limit, String tenantId) {
    return snapshotDao.getSnapshots(condition, orderFields, offset, limit, tenantId);
  }

  @Override
  public Future<Optional<Snapshot>> getSnapshotById(String id, String tenantId) {
    return snapshotDao.getSnapshotById(id, tenantId);
  }

  @Override
  public Future<Snapshot> saveSnapshot(Snapshot snapshot, String tenantId) {
    return snapshotDao.saveSnapshot(snapshot, tenantId);
  }

  @Override
  public Future<Snapshot> updateSnapshot(Snapshot snapshot, String tenantId) {
    return snapshotDao.updateSnapshot(snapshot, tenantId);
  }

  @Override
  public Future<Boolean> deleteSnapshot(String id, String tenantId) {
    return snapshotDao.deleteSnapshot(id, tenantId);
  }

}