package XUnit.UI;

import XUnit.Tester;

public class Main {
    
    public static void main(String[] args) {
        try {
            Tester tester = new Tester(args);
            tester.testAll();
            tester.getTestReports().forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
