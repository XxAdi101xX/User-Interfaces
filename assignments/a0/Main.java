import java.io.PrintWriter;

/*public class Main {

    public static void main(String[] args) {
        Runtime.Version version = Runtime.version();
        new Hello(version.toString());

        try {
            PrintWriter writer = new PrintWriter("results.txt", "UTF-8");
            writer.println(version);
            writer.close();
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
}*/

public class Main {

    public static void main(String[] args) {
        String version = System.getProperty("java.version");
        new Hello(version);

        try {
            PrintWriter writer = new PrintWriter("results.txt", "UTF-8");
            writer.println(version);
            writer.close();
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
}
