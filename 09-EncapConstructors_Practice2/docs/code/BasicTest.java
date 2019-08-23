import java.io.FileInputStream;

public class BasicTest {

  public static void main(String[] args) throws Exception {
    SecurityTest test = new SecurityTest();
    test.shouldDeny("Property get", new Snippet() {
      @Override
      public void run() throws Exception {
        System.getProperty("file.encoding");
      }
    });
    test.shouldDeny("file open", new Snippet() {
      @Override
      public void run() throws Exception {
        new FileInputStream("/temp/file.txt").close();
      }
    });
    test.printResult();
  }
}
