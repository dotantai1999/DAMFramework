package Repository;

public interface IJpaRepository<T> {
	Long insert(Object object);
	void update(Object object);
	void delete(Object object);
}
