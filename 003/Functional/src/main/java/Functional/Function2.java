package Functional;

public abstract class Function2<X, Y, Z> { //Function2: X -> Y -> Z (same as (X, Y) -> Z)
    abstract public Z apply(X x, Y y);

    public <W> Function2<X, Y, W> compose (Function1<? super Z, ? extends W> g){
        return new Function2<X, Y, W>(){
            @Override
            public W apply(X x, Y y) {
                return g.apply(Function2.this.apply(x, y));
            }
        };
    }

    public Function1<Y, Z> bind1(X x){
        return new Function1<Y, Z>() {
            @Override
            public Z apply(Y y) {
                return Function2.this.apply(x, y);
            }
        };
    }

    public Function1<X, Z> bind2(Y y){
        return new Function1<X, Z>() {
            @Override
            public Z apply(X x) {
                return Function2.this.apply(x, y);
            }
        };
    }

    public Function1<X, Function1<Y, Z>> curry(){
        return new Function1<X, Function1<Y, Z>>() {
            @Override
            public Function1<Y, Z> apply(X x) {
                return Function2.this.bind1(x);
            }
        };
    }
}
