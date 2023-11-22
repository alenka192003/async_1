package com.example.async_1;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {
    private int[] shelf = {-1, -1, -1, -1, -1};
    private int nextFreeSlot = 0;
    private volatile boolean isRunning = true;

    private Circle[] circles = new Circle[5];

    @Override
    public void start(Stage primaryStage) {
        HBox root = new HBox(10);
        for (int i = 0; i < 5; i++) {
            circles[i] = new Circle(20);
            circles[i].setFill(Color.GRAY);
            root.getChildren().add(circles[i]);
        }

        Scene scene = new Scene(root, 250, 50);

        primaryStage.setTitle("Shelf Simulation");
        primaryStage.setScene(scene);
        primaryStage.show();

        Producer producer = new Producer();
        Consumer consumer = new Consumer();

        Thread producerThread = new Thread(producer);
        Thread producerThread1 = new Thread(producer);
        Thread producerThread2 = new Thread(producer);
        Thread consumerThread = new Thread(consumer);
        Thread consumerThread1 = new Thread(consumer);

        producerThread.start();
        producerThread1.start();
        producerThread2.start();
        consumerThread.start();
        consumerThread1.start();

        root.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                isRunning = false;
                Thread.currentThread().interrupt();
                System.exit(0);
            }
        });

        root.requestFocus();
//

        //убить потоки + избавиться от бесконечного цикла
        //интераптед можно


        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Thread.currentThread().interrupt();
                System.exit(0);
            }
        });


    }

    class Producer implements Runnable {
        @Override
        public void run() {
            try {
                int itemNumber = 1;
                boolean ok = false;
                while (isRunning== true) {
                    ok = false;
                    synchronized (shelf) {
                        if (nextFreeSlot < 5) {
                            shelf[nextFreeSlot] = itemNumber;
                            System.out.println("Producer " + Thread.currentThread().getName() + " placed item " + itemNumber + " on shelf");
                            itemNumber++;
                            nextFreeSlot++;
                            updateUI();
                            shelf.notifyAll();
                            ok =true;
                        } else {
                            System.out.println("Producer " + Thread.currentThread().getName() + " is WAITING ");
                            shelf.wait();

                        }
                    }
                    if (ok){
                        Thread.sleep(1000);
                    }

                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    class Consumer implements Runnable {
        @Override
        public void run() {
            try {
                boolean ok = false;
                while (isRunning== true) {
                    ok = false;
                    synchronized (shelf) {
                        if (nextFreeSlot > 0) {
                            int itemNumber = shelf[nextFreeSlot - 1];
                            shelf[nextFreeSlot - 1] = -1;
                            System.out.println("Consumer " + Thread.currentThread().getName() + " took item " + itemNumber + " from shelf");
                            nextFreeSlot--;
                            updateUI();
                            shelf.notifyAll();
                            ok =true;
                        } else {
                            System.out.println("Consumer " + Thread.currentThread().getName() + " is WAITING ");
                            shelf.wait();
                        }
                    }
                    if (ok){
                        Thread.sleep(1000);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void updateUI() {
        for (int i = 0; i < circles.length; i++) {
            if (i < nextFreeSlot) {
                circles[i].setFill(Color.GREENYELLOW);
            } else {
                circles[i].setFill(Color.GRAY);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

