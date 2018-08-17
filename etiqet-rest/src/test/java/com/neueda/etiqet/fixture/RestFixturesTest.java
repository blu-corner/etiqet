package com.neueda.etiqet.fixture;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class RestFixturesTest {

    private EtiqetHandlers etiqetHandlers;
    private RestFixtures restFixtures;

    @Before
    public void setUp() throws Exception {
        etiqetHandlers = Mockito.spy(EtiqetHandlers.class);
        restFixtures = new RestFixtures(etiqetHandlers);
    }

    @Test
    public void testMethod() {
        Mockito.doReturn("123").when(etiqetHandlers).preTreatParams(Mockito.anyString());
    }
}