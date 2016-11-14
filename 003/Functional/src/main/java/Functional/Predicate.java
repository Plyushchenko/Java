package Functional;

public abstract class Predicate<X> extends Function1<X, Boolean> { //Predicate: X -> Boolean
    public abstract Boolean apply(X x);

    public Predicate<X> or(Predicate<? super X> p) {
        return new Predicate<X>() {
            @Override
            public Boolean apply(X x) {
                return Predicate.this.apply(x) || p.apply(x);
            }
        };
    }

    public Predicate<X> and(Predicate<? super X> p) {
        return new Predicate<X>() {
            @Override
            public Boolean apply(X x) {
                return Predicate.this.apply(x) && p.apply(x);
            }
        };
    }

    public Predicate<X> not() {
        return new Predicate<X>() {
            @Override
            public Boolean apply(X x) {
                return !Predicate.this.apply(x);
            }
        };
    }

    public static final Predicate ALWAYS_TRUE = new Predicate() {
        @Override
        public Boolean apply(Object o) {
            return true;
        }
    };
    public static final Predicate ALWAYS_FALSE = ALWAYS_TRUE.not();
}