package league.homework.calc;

//used to store action and index of it in RESULT LIST
public class Pair {

    private final Action action;
    private final int index;

    public Pair(Action action, int index) {
        this.action = action;
        this.index = index;
    }

    public Action getAction() {
        return action;
    }

    public int getIndex() {
        return index;
    }
}
