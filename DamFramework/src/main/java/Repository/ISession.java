package Repository;

import helper.QueryCreator;
import service.Insertor;
import service.Selector;

public abstract class ISession<T> {
	protected QueryCreator query = new QueryCreator();
	protected Insertor insertor;

	public ISession() {
	}

	public QueryCreator getQuery() {
		return query;
	}

	public void setQuery(QueryCreator query) {
		this.query = query;
	}

	public abstract Object insert(Object object);
	public abstract Object update(Object object);
	public abstract void delete(Object object);
	public abstract Object get(Class zClass, Object id);
	public abstract Object select(Class zClass, Object id);
}
