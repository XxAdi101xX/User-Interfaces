public enum Tool {
    CURSOR, ERASER, LINE, CIRCLE, RECTANGLE, FILL;

    public static Tool getToolFromInt(int x) {
        return Tool.values()[x];
    }
}