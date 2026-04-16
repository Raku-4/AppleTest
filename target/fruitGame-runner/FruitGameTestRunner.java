import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
public class FruitGameTestRunner {
    public static void main(String[] args) {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(DiscoverySelectors.selectClass("com.raku.fruitGame.fruit.functionalClass.FruitHistoryTest"))
                .build();
        Launcher launcher = LauncherFactory.create();
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);
        long failed = listener.getSummary().getFailures().size();
        long found = listener.getSummary().getTestsFoundCount();
        long succeeded = listener.getSummary().getTestsSucceededCount();
        System.out.println("found=" + found + ", succeeded=" + succeeded + ", failed=" + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }
}
