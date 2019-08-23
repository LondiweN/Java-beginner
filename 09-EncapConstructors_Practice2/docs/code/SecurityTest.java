import java.util.ArrayList;
import java.util.List;

public class SecurityTest {

  private final List<String> failures;
  private final List<String> successes;

  public SecurityTest() {
    this.failures = new ArrayList<>();
    this.successes = new ArrayList<>();
  }

  public void shouldAllow(String id, Snippet snippet) {
    try {
      snippet.run();
      successes.add("[ OK ] " + id);
    } catch (Exception e) {
      failures.add("[FAIL] " + id + " -- " + e.getClass().getName());
    }
  }

  public void shouldDeny(String id, Snippet snippet) {
    try {
      snippet.run();
      failures.add("[FAIL] " + id + " SHOULD NOT RUN!");
    } catch (Exception e) {
      successes.add("[ OK ] " + id + " -- " + e.getClass().getName());
    }
  }

  public void printResult() {
    System.out.println("TEST COMPLETE");
    if (!failures.isEmpty()) {
      System.out.println("\n\n");
      System.out.println("FAILURES");
      for (String s : failures) {
        System.out.println(s);
      }
    }
    System.out.println("\n\n");
    System.out.println("INFO");
    for (String s : successes) {
      System.out.println(s);
    }
  }
}
