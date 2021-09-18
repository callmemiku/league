package league.homework.calc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Calculator {

    private boolean areThereMultiplies = true;
    private boolean areThereDivisions = true;
    private boolean areThereMinuses = true;
    private static final Pattern generalPattern = Pattern.compile("[0-9(),.*/+-]+$");
    private static final Pattern secondaryPattern = Pattern.compile(".*\\d.*");

    private static final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException {
        Calculator calculator = new Calculator();
        while (true) {
            try {
                System.out.println(calculator.compute(readAndFixString()));
            } catch (IllegalArgumentException e){
                System.out.println(e.getMessage());
            }
        }
    }

    //Reads and makes string format compatible for further work
    public static String readAndFixString() throws IOException {
        String stringToParse = "";
        boolean isStringValid = false;
            while (!isStringValid) {
                System.out.println("Write a string to compute or 'EXIT' to end program execution: \n" +
                        "If you need to use a negative number, put it in round brackets like this (-4).");
                stringToParse = bufferedReader.readLine();
                stringToParse = stringToParse
                        .replaceAll("\\s", "")
                        .replaceAll(",", ".")
                        .replaceAll("÷", "/")
                        .replaceAll(":", "/")
                        .replaceAll("×", "*");
                if (stringToParse.toUpperCase(Locale.ROOT).equals("EXIT")) {
                    isStringValid = true;
                    System.out.println("Shutting down.");
                    bufferedReader.close();
                    System.exit(0);
                } else {
                    if (generalPattern.matcher(stringToParse).matches() && secondaryPattern.matcher(stringToParse).matches()) {
                        isStringValid = true;
                    } else {
                        System.out.println("String is invalid, try one more time!");
                    }
                }
            }
        return stringToParse;
    }

    //It gets list of numbers
    //Then it gets list of operations
    //Next step is to mix them together according to given order
    //Then it executes the said actions
    public String compute(String stringToParse) {
        //we don't know if there will be such operations, so 'compute pair' method will check it for us
        areThereMultiplies = true;
        areThereDivisions = true;
        areThereMinuses = true;

        List<String> numbers = parseNumbers(stringToParse);

        if (numbers.size() == 1)
            return numbers.get(0);
        List<String> operations = parseOperations(stringToParse);

        //mixing 'em to revert original order of operations
        for (int i = 1, j = 0; i < numbers.size(); i = i + 2, j++) {
            numbers.add(i, operations.get(j));
        }

        Pair pair;
        int index;
        //for each action 2 members (action and number) of 'numbers' list will be removed
        while (numbers.size() != 1) {
            pair = computePair(numbers);
            index = pair.getIndex();
            numbers.set(index - 1, count(pair.getAction(), numbers.get(index - 1), numbers.get(index + 1)));
            numbers.remove(index);
            numbers.remove(index);
        }

        return String.format("%.2f", Double.parseDouble(numbers.get(0)));
    }

    //Do the needed counting on data given
    private String count(Action action, String first, String second) {
        switch (action) {
            case PLUS -> {
                return "" + (Double.parseDouble(first) + Double.parseDouble(second));
            }
            case MINUS -> {
                return "" + (Double.parseDouble(first) - Double.parseDouble(second));
            }
            case DIVIDE -> {
                return "" + (Double.parseDouble(first) / Double.parseDouble(second));
            }
            case MULTIPLY -> {
                return "" + (Double.parseDouble(first) * Double.parseDouble(second));
            }
            //It probably will never appear
            default -> throw new IllegalArgumentException("No such operation!");
        }
    }

    //tech methods
    private Pair computePair(List<String> list) {
        if (areThereDivisions && list.contains("/")) {
            return new Pair(Action.DIVIDE, list.indexOf("/"));
        }

        if (areThereMultiplies && list.contains("*")) {
            areThereDivisions = false;
            return new Pair(Action.MULTIPLY, list.indexOf("*"));
        }

        if (areThereMinuses && list.contains("-")) {
            areThereMultiplies = false;
            return new Pair(Action.MINUS, list.indexOf("-"));
        }

        else {
            areThereMinuses = false;
            return new Pair(Action.PLUS, list.indexOf("+"));
        }
    }
    private List<String> parseNumbers(String stringToParse){
        return new ArrayList<>(
                Arrays.asList(stringToParse.split("[(*/+-]")))
                .stream()
                .filter(x -> !x.equals(""))
                .filter(x -> !x.equals("\\."))
                .map(x -> {
                    //To find numbers with multiple commas or points
                    if (x.contains(".")) {
                        String[] buf = Arrays.stream(x.split("\\."))
                                .filter(t -> !t.equals(""))
                                .toArray(String[]::new);
                        x = buf[0] + "." + buf[1];
                    }

                    if (x.contains(")")) {
                        return "-" + x.replaceAll("\\)", "");
                    }

                    return x;
                })
                .collect(Collectors.toList());
    }
    private List<String> parseOperations(String stringToParse) {
        return new ArrayList<>(Arrays.asList(stringToParse.split("[).,0-9]")))
                .stream()
                .map(x -> {
                    if (x.contains("(")) {
                        x = x.replaceAll("\\(-", "");
                    }
                    return x;
                })
                .filter(x -> !x.equals(""))
                .peek(x -> {
                    if (x.length() > 1)
                        throw new IllegalArgumentException("String is invalid, try one more time!");
                })
                .collect(Collectors.toList());
    }

}
