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
        new java.io.File("slots.txt").delete();
        scheduleService = new ScheduleService();
    }

    @Test
    void testInitialSlotsAreAvailable() {
        List<AppointmentSlot> slots = scheduleService.getAvailableSlots();
        // ✅ تحقق إن في slots متاحة بدل ما تحدد العدد
        assertFalse(slots.isEmpty());
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
        int before = slots.size();
        slots.get(0).book();
        List<AppointmentSlot> after = scheduleService.getAvailableSlots();
        // ✅ تحقق إن عدد الـ slots قل بواحد
        assertEquals(before - 1, after.size());
    }

    @Test
    void testAddSlot() {
        int before = scheduleService.getAvailableSlots().size();
        scheduleService.addSlot("20:00");
        int after = scheduleService.getAvailableSlots().size();
        assertEquals(before + 1, after);
    }
    @Test
    void testFreeSlot() {
        List<AppointmentSlot> slots = scheduleService.getAvailableSlots();
        slots.get(0).book();
        int booked = scheduleService.getAvailableSlots().size();
        scheduleService.freeSlot(slots.get(0).getTime());
        // بعد freeSlot يرجع متاح
        assertTrue(scheduleService.getAvailableSlots().size() > booked);
    }

    @Test
    void testAddDuplicateSlot() {
        int before = scheduleService.getAvailableSlots().size();
        scheduleService.addSlot("10:00"); // موجود بالفعل
        assertEquals(before, scheduleService.getAvailableSlots().size());
    }
}