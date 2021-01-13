package serviceImpl;

import service.Selector;

public abstract class SelectDecorator implements Selector {
    protected Selector wrappeeSelector;

    public SelectDecorator(Selector wrappeeSelector) {
        this.wrappeeSelector = wrappeeSelector;
    }

    public abstract Object additionalSelect(Object entity, Object id);
}
