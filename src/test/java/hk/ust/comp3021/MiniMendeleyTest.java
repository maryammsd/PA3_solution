package hk.ust.comp3021;

import hk.ust.comp3021.utils.TestExtension;
import hk.ust.comp3021.utils.TestKind;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Date;

@ExtendWith(TestExtension.class)
class MiniMendeleyTest {
    @Tag(TestKind.PUBLIC)
    @Test
    void testPaperParser() {
        MiniMendeleyEngine engine = new MiniMendeleyEngine();
        assertEquals(engine.getPaperBase().size(), 123);
    }


    @Tag(TestKind.PUBLIC)
    @Test
    void testUserRegister() {
        MiniMendeleyEngine engine = new MiniMendeleyEngine();
        assertEquals(engine.getUsers().size(), 1);
        String userID = "User_" + engine.getUsers().size();
        engine.processUserRegister(userID, "testUser", new Date());
        assertEquals(engine.getUsers().size(), 2);
    }
}
