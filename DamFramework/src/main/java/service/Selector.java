package service;

public interface Selector {
    Object select(Class entityClass, Object id);
}
