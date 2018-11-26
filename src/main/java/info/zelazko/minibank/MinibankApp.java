package info.zelazko.minibank;

import info.zelazko.minibank.util.SparkRunner;

import static info.zelazko.minibank.util.SparkRunner.DEFAULT_PORT;

public class MinibankApp {
    public static void main(String[] args) {
        SparkRunner.run(DEFAULT_PORT);
    }
}
