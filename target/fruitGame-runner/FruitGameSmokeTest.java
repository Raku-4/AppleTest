import com.raku.fruitGame.fruit.functionalClass.utility.FruitHistory;
import java.nio.file.Files;
import java.nio.file.Path;
public class FruitGameSmokeTest {
    public static void main(String[] args) throws Exception {
        Path p = Path.of("target", "fruitgame_smoke.csv");
        FruitHistory h1 = new FruitHistory();
        h1.recordCreation("apple", "RED", 150);
        h1.recordCreation("banana", "YELLOW", 120);
        h1.saveCsv(p);
        FruitHistory h2 = new FruitHistory();
        h2.loadCsv(p);
        if (h2.size() != 2) {
            throw new IllegalStateException("Unexpected size: " + h2.size());
        }
        Files.deleteIfExists(p);
        System.out.println("smoke-test-ok");
    }
}
