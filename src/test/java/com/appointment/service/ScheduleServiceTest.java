package com.appointment.service;

import com.appointment.domain.AppointmentSlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ScheduleServiceTest {

    private ScheduleService scheduleService;

    @BeforeEach
    void setUp() {
        scheduleService = new ScheduleService();
    }

    @Test
    void testInitialSlotsAreAvailable() {
        List<AppointmentSlot> slots = scheduleService.getAvailableSlots();
        assertEquals(3, slots.size());
    }

    @Test
    void testAvailableSlotsNotBooked() {
        List<AppointmentSlot> slots = scheduleService.getAvailableSlots();
        for (AppointmentSlot slot : slots) {
            assertFalse(slot.isBooked());
        }
    }

    @Test
    void testAfterBookingSlotNotAvailable() {
        List<AppointmentSlot> slots = scheduleService.getAvailableSlots();
        slots.get(0).book();
        List<AppointmentSlot> available = scheduleService.getAvailableSlots();
        assertEquals(2, available.size());
    }

    @Test
    void testSlotsHaveCorrectTimes() {
        List<AppointmentSlot> slots = scheduleService.getAvailableSlots();
        assertEquals("10:00", slots.get(0).getTime());
        assertEquals("11:00", slots.get(1).getTime());
        assertEquals("12:00", slots.get(2).getTime());
    }
}