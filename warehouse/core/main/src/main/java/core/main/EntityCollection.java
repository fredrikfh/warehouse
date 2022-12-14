package core.main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Class for containing a collection of entities, f.eks. Items or Users.
 */
public class EntityCollection<T extends Entity<T>> {
  private final Map<String, T> entities;
  private final Collection<EntityCollectionListener<T>> listeners;
  private final Map<String, EntityListener<T>> entityListeners;

  public EntityCollection() {
    entities = new HashMap<>();
    listeners = new ArrayList<>();
    entityListeners = new HashMap<>();
  }

  private void removeEntityListener(T entity) {
    entity.removeListener(entityListeners.get(entity.getId()));
    entityListeners.remove(entity.getId());
  }

  private void addEntityListener(T entity) {
    EntityListener<T> listener = this::notifyUpdated;
    entity.addListener(listener);
    entityListeners.put(entity.getId(), listener);
  }

  public boolean contains(String id) {
    return entities.containsKey(id);
  }

  public boolean contains(Predicate<T> predicate) {
    return entities.values().stream().anyMatch(predicate);
  }

  public Optional<T> find(Predicate<T> predicate) {
    return entities.values().stream().filter(predicate).findAny();
  }

  public void add(T entity) {
    if (contains(entity.getId())) {
      throw new IllegalArgumentException("Entity with this id is already in the collection");
    }

    entities.put(entity.getId(), entity);
    addEntityListener(entity);

    notifyAdded(entity);
  }

  public void addAll(Collection<T> entities) {
    for (T entity : entities) {
      add(entity);
    }
  }

  /**
   * Adds the entity or replaces it if it already exists.
   *
   * @return true if it was added, false if it replaced
   */
  public boolean put(T entity) {
    if (contains(entity.getId())) {
      T old = get(entity.getId());
      if (!old.equals(entity)) {
        removeEntityListener(old);
        entities.put(entity.getId(), entity);
        addEntityListener(entity);
        notifyUpdated(entity);
      }
      return false;
    } else {
      add(entity);
      return true;
    }
  }

  public T remove(String id) {
    T entity = get(id);

    removeEntityListener(entity);
    entities.remove(entity.getId());

    notifyRemoved(entity);

    return entity;
  }

  public T get(String id) {
    if (!contains(id)) {
      throw new IllegalArgumentException("Entity does not exist in collection");
    }
    return entities.get(id);
  }

  public Collection<T> getAll() {
    return entities.values();
  }

  public Collection<T> getAllFiltered(Predicate<T> predicate) {
    return getAll()
        .stream()
        .filter(predicate)
        .collect(Collectors.toCollection(ArrayList::new));
  }

  public List<T> getAllSorted(Comparator<T> comparator) {
    return getAll()
        .stream()
        .sorted(comparator)
        .collect(Collectors.toList());
  }

  private void notifyAdded(T entity) {
    for (EntityCollectionListener<T> listener : listeners) {
      listener.entityAdded(entity);
    }
  }

  private void notifyUpdated(T entity) {
    for (EntityCollectionListener<T> listener : listeners) {
      listener.entityUpdated(entity);
    }
  }

  private void notifyRemoved(T entity) {
    for (EntityCollectionListener<T> listener : listeners) {
      listener.entityRemoved(entity);
    }
  }
  
  public void addListener(EntityCollectionListener<T> listener) {
    this.listeners.add(listener);
  }

  public void removeListener(EntityCollectionListener<T> listener) {
    this.listeners.remove(listener);
  }
}
