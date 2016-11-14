package Functional;

public abstract class Function1<X, Y> { //Function1: X -> Y
    abstract public Y apply(X x);

    public <Z> Function1<X, Z> compose(Function1<? super Y, ? extends Z> g) {
        return new Function1<X, Z>() {
            @Override
            public Z apply(X x) {
                return g.apply(Function1.this.apply(x));
            }
        };
    }
}
