package com.appointment.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AppointmentSlotTest {

    @Test
    void testInitiallyNotBooked() {
        AppointmentSlot slot = new AppointmentSlot("10:00");
        assertFalse(slot.isBooked());
    }

    @Test
    void testGetTime() {
        AppointmentSlot slot = new AppointmentSlot("10:00");
        assertEquals("10:00", slot.getTime());
    }

    @Test
    void testBookSlot() {
        AppointmentSlot slot = new AppointmentSlot("10:00");
        slot.book();
        assertTrue(slot.isBooked());
    }
}