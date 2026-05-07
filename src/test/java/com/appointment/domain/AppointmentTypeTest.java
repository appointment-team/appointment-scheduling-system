package com.appointment.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AppointmentTypeTest {

    @Test
    void testAllValuesExist() {
        AppointmentType[] values = AppointmentType.values();
        assertEquals(7, values.length);
    }

    @Test
    void testValueOfUrgent() {
        assertEquals(AppointmentType.URGENT, AppointmentType.valueOf("URGENT"));
    }

    @Test
    void testValueOfFollowUp() {
        assertEquals(AppointmentType.FOLLOW_UP, AppointmentType.valueOf("FOLLOW_UP"));
    }

    @Test
    void testValueOfVirtual() {
        assertEquals(AppointmentType.VIRTUAL, AppointmentType.valueOf("VIRTUAL"));
    }

    @Test
    void testValueOfInPerson() {
        assertEquals(AppointmentType.IN_PERSON, AppointmentType.valueOf("IN_PERSON"));
    }

    @Test
    void testValueOfIndividual() {
        assertEquals(AppointmentType.INDIVIDUAL, AppointmentType.valueOf("INDIVIDUAL"));
    }

    @Test
    void testValueOfGroup() {
        assertEquals(AppointmentType.GROUP, AppointmentType.valueOf("GROUP"));
    }

    @Test
    void testValueOfAssessment() {
        assertEquals(AppointmentType.ASSESSMENT, AppointmentType.valueOf("ASSESSMENT"));
    }

    @Test
    void testEachTypeNameMatchesEnumName() {
        for (AppointmentType type : AppointmentType.values()) {
            assertNotNull(type.name());
            assertEquals(type, AppointmentType.valueOf(type.name()));
        }
    }

    @Test
    void testToString() {
        assertEquals("URGENT", AppointmentType.URGENT.toString());
        assertEquals("GROUP", AppointmentType.GROUP.toString());
    }
}
