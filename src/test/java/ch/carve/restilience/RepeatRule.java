package ch.carve.restilience;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class RepeatRule implements TestRule {
    private int count;
    
    public RepeatRule(int count) {
        this.count = count;
    }
    
    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                for (int i = 0; i < count; i++) {
                    base.evaluate();
                }
            }
        };
    }
}
