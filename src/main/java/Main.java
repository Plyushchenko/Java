import javafx.application.Application;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import java.util.*;

public class Main extends Application {

    public static int N = 4;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        String[] state = new String[N * N];
        primaryStage.setTitle("Game");
        Button startButton = new Button("Start");
        Button[] buttons = new Button[N * N];
        for (int i = 0; i < N * N; i++) {
            buttons[i] = new Button("?");
        }
        HBox[] rows = new HBox[N];
        boolean[] pressed = new boolean[N * N];
        for (int i = 0; i < pressed.length; i++) {
            pressed[i] = false;
        }
        int[] count = new int[1];
        int matched[] = new int[1];
        startButton.setOnAction(e -> {
            for (int i = 0; i < state.length; i++) {
                state[i] = "0";
                pressed[i] = false;
                buttons[i].setDisable(false);
                buttons[i].setText("?");

            }
            count[0] = 0;
            matched[0] = 0;
            List<Integer> cells = new ArrayList<>();
            for (int i = 0; i < N * N; i++) {
                cells.add(i);
            }
//            cells.forEach(x -> System.out.print(x + " "));
//            System.out.println();
            Collections.shuffle(cells);
//            cells.forEach(x -> System.out.print(x + " "));
//            System.out.println();

            for (int i = 0; i < N * N / 2; i++) {
                state[cells.get(i)] = "1";
            }
        });
        for (int i = 0; i < buttons.length; i++) {
            int ii = i;
            buttons[i].setOnAction(e -> {
                if (pressed[ii]) {
                    return;
                }
                count[0]++;
                pressed[ii] = true;
                if (count[0] == 2 && matched[0] == 14) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e1) {
                        //ignore
                    }
                    startButton.fire();
                    return;
                }
                if (count[0] <= 2) {
                    for (int j = 0; j < pressed.length; j++) {
                        if (pressed[j]) {
//                            System.out.println(j + " is pressed, value = " + state[j]);
                            buttons[j].setText(state[j]);
//                            System.out.println(buttons[j].getText() + " " + state[j]);
                        }
                    }
                } else {
                    List<Integer> pressedButtons = new ArrayList<>();
                    for (int j = 0; j < pressed.length; j++) {
                        if (j == ii) {
                            continue;
                        }
                        if (pressed[j]) {
                            pressedButtons.add(j);
                        }
                    }
                    if (buttons[pressedButtons.get(0)].getText().equals
                            (buttons[pressedButtons.get(1)].getText())) {
                        buttons[pressedButtons.get(0)].setDisable(true);
                        buttons[pressedButtons.get(1)].setDisable(true);
                        matched[0] += 2;
                    }
                    for (int j = 0; j < pressed.length; j++) {
                        if (j == ii) {
                            continue;
                        }
                        if (pressed[j]) {
//                            System.out.println(j + " is unpressed, value = " + state[j]);
                            if (!buttons[pressedButtons.get(0)].getText().equals
                                    (buttons[pressedButtons.get(1)].getText())) {
//                                System.out.println(pressedButtons.get(0));
//                                System.out.println(pressedButtons.get(1));
                                buttons[j].setText("?");
                            }
                            count[0]--;
                            pressed[j] = false;
//                            System.out.println(buttons[j].getText() + " " + state[j]);
                        }
                    }
//                    System.out.println(ii + " is pressed, value = " + state[ii]);
                    buttons[ii].setText(state[ii]);
//                    System.out.println(buttons[ii].getText() + " " + state[ii]);
                }
            });
        }

        for (int i = 0; i < N; i++) {
            rows[i] = new HBox(Arrays.copyOfRange(buttons, i * N, (i + 1) * N ));
        }
        startButton.fire();
        //TODO ??? Надо как-то поаккуратнее массив рядов передавать
        VBox vbox = new VBox(startButton, rows[0], rows[1], rows[2], rows[3]);
        Scene scene = new Scene(vbox, 120, 130);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
