package sp;

import java.awt.*;
import java.awt.geom.Point2D;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SecondPartTasks {

    private SecondPartTasks() {}

    // Найти строки из переданных файлов, в которых встречается указанная подстрока.
    public static List<String> findQuotes(List<String> paths, CharSequence sequence) {
        return paths
                .stream()
                .map(Paths::get)
                .flatMap(path -> {
                    try { //must handle exception
                        return Files.lines(path);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    return null;
                })
                .filter(line -> line.contains(sequence))
                .collect(Collectors.toList());
    }

    // В квадрат с длиной стороны 1 вписана мишень.
    // Стрелок атакует мишень и каждый раз попадает в произвольную точку квадрата.
    // Надо промоделировать этот процесс с помощью класса java.util.Random и посчитать, какова вероятность попасть в мишень.
    public static double piDividedBy4()
    {
        final int ITERATIONS = 200000000; //20M
        Random rnd = new Random();
        final double SIDE_HALF = 1 / 2.;

        return Stream.generate(() -> new Point2D.Double(rnd.nextDouble(), rnd.nextDouble()))
                .limit(ITERATIONS)
                .filter(pnt -> pnt.distance(SIDE_HALF, SIDE_HALF) <= SIDE_HALF)
                .count() * 1./ ITERATIONS;
    }

    // Дано отображение из имени автора в список с содержанием его произведений.
    // Надо вычислить, чья общая длина произведений наибольшая.
    public static String findPrinter(Map<String, List<String>> compositions) {
        return compositions
                .entrySet()
                .stream()
                .max(Comparator.comparingInt(entry -> entry
                                                        .getValue()
                                                        .stream()
                                                        .collect(Collectors.summingInt(value -> value.length()))))
                .map(Map.Entry::getKey)
                .orElse("[Warning] no compositions provided");

    }

    // Вы крупный поставщик продуктов. Каждая торговая сеть делает вам заказ в виде Map<Товар, Количество>.
    // Необходимо вычислить, какой товар и в каком количестве надо поставить.

    public static Map<String, Integer> calculateGlobalOrder(List<Map<String, Integer>> orders) {
        return orders
                .stream()
                .flatMap(order -> order
                                    .entrySet()
                                    .stream())
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                         Collectors.summingInt(Map.Entry::getValue)));
    }
}
