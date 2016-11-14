package Functional;
import java.util.*;

public class Collections{
    public static <X, Y> List<Y> map(Function1<? super X, ? extends Y> f, Iterable<X> a) {
        final List<Y> res = new ArrayList<>();
        for (X x : a) {
            res.add(f.apply(x));
        }
        return res;
    }
    public static <X> List<X> filter(Predicate<? super X> p, Iterable<X> a) {
        final List<X> res = new ArrayList<>();
        for (X x : a) {
            if (p.apply(x)){
                res.add(x);
            }
        }
        return res;
    }

    public static <X> List<X> takeWhile(Predicate<? super X> p, Iterable<X> a) {
        List<X> res = new ArrayList<>();
        for (X x : a) {
            if (p.apply(x)){
                res.add(x);
            }
            else{
                break;
            }
        }
        return res;
    }

    public static <X> List<X> takeUnless(Predicate<? super X> p, Iterable<X> a) {
        return takeWhile(p.not(), a);
    }

    public static <X, Y> X foldl(Function2<? super X, ? super Y, ? extends X> f, X result, Iterable <Y> a){
        for (Y y : a){
            result = f.apply(result, y);
        }
        return result;
    }

    public static <X, Y> Y foldr(Function2<? super X, ? super Y, ? extends Y> f, Y init, Iterable<X> a) {
        return foldrImplementation(f, init, a.iterator());
    }

    private static <Y, X> Y foldrImplementation(Function2<? super X, ? super Y, ? extends Y> f, Y init, Iterator<X> it) {
        if (!it.hasNext())
            return init;
        return f.apply(it.next(), foldrImplementation(f, init, it));
    }
}