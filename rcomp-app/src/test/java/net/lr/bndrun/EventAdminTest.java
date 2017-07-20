package net.lr.bndrun;
import static net.lr.bndrun.BndRunOption.bndRun;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

@RunWith(PaxExam.class)
public class EventAdminTest {
    @Inject
    EventAdmin eventAdmin;

    @Configuration
    public Option[] configure() throws Exception {
        return options(bndRun("rcomp-example.bndrun", "cnf/cache/pom-rcomp.xml"));
    }

    @Test
    public void test() {
        Map<String, String> properties = new HashMap<>();
        eventAdmin.sendEvent(new Event("eainput", properties));
    }
}
