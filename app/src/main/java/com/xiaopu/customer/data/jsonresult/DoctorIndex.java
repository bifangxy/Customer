package com.xiaopu.customer.data.jsonresult;

import java.util.List;

public class DoctorIndex {
    //留言订单
    private List<DoctorMessageOrderItem> doctorMessageOrderItemList;
    //预约订单
    List<DoctorAppointmentOrderItemDoctor> doctorAppointmentOrderItemDoctorList;
    //私人订单
    List<DoctorPrivateOrderItem> doctorPrivateOrderItemList;

    public List<DoctorMessageOrderItem> getDoctorMessageOrderItemList() {
        return doctorMessageOrderItemList;
    }

    public List<DoctorAppointmentOrderItemDoctor> getDoctorAppointmentOrderItemDoctorList() {
        return doctorAppointmentOrderItemDoctorList;
    }

    public List<DoctorPrivateOrderItem> getDoctorPrivateOrderItemList() {
        return doctorPrivateOrderItemList;
    }
}
