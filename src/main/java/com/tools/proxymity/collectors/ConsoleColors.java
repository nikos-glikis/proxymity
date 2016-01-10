package com.tools.proxymity.collectors;

public class ConsoleColors
{
    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";


    public static void printColor(String text, String color)
    {
        System.out.println(color+text+ConsoleColors.RESET);
    }

    public static void printRed(String text)
    {
        printColor(text, ConsoleColors.RED);
    }

    public static void printBlue(String text)
    {
        printColor(text, ConsoleColors.BLUE);
    }

    public static void printCyan(String text)
    {
        printColor(text, ConsoleColors.CYAN);
    }
}
