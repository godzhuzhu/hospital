package com.whlg.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.whlg.hospital.dto.CreateAppointmentRequest;
import com.whlg.hospital.dto.CreateConsultRequest;
import com.whlg.hospital.dto.CreatePaymentRequest;
import com.whlg.hospital.dto.PayRequest;
import com.whlg.hospital.dto.PaymentCallbackRequest;
import com.whlg.hospital.entity.Appointment;
import com.whlg.hospital.entity.Consult;
import com.whlg.hospital.entity.Doctor;
import com.whlg.hospital.entity.FamilyMember;
import com.whlg.hospital.entity.PaymentFlow;
import com.whlg.hospital.entity.Schedule;
import com.whlg.hospital.mapper.AppointmentMapper;
import com.whlg.hospital.mapper.ConsultMapper;
import com.whlg.hospital.mapper.DoctorMapper;
import com.whlg.hospital.mapper.FamilyMemberMapper;
import com.whlg.hospital.mapper.HospitalMapper;
import com.whlg.hospital.mapper.PaymentFlowMapper;
import com.whlg.hospital.mapper.ScheduleMapper;
import com.whlg.hospital.service.OrderService;
import com.whlg.hospital.support.ApiException;
import com.whlg.hospital.util.StatusCode;
import com.whlg.hospital.vo.PageResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceSupport implements OrderService {

    private final AppointmentMapper appointmentMapper;
    private final ConsultMapper consultMapper;
    private final DoctorMapper doctorMapper;
    private final FamilyMemberMapper familyMemberMapper;
    private final HospitalMapper hospitalMapper;
    private final PaymentFlowMapper paymentFlowMapper;
    private final ScheduleMapper scheduleMapper;

    public OrderServiceImpl(AppointmentMapper appointmentMapper,
                            ConsultMapper consultMapper,
                            DoctorMapper doctorMapper,
                            FamilyMemberMapper familyMemberMapper,
                            HospitalMapper hospitalMapper,
                            PaymentFlowMapper paymentFlowMapper,
                            ScheduleMapper scheduleMapper) {
        this.appointmentMapper = appointmentMapper;
        this.consultMapper = consultMapper;
        this.doctorMapper = doctorMapper;
        this.familyMemberMapper = familyMemberMapper;
        this.hospitalMapper = hospitalMapper;
        this.paymentFlowMapper = paymentFlowMapper;
        this.scheduleMapper = scheduleMapper;
    }

    @Override
    public Map<String, Object> createAppointment(CreateAppointmentRequest request) {
        Long userId = requireUserId();
        Schedule schedule = scheduleMapper.selectById(request.getScheduleId());
        FamilyMember familyMember = familyMemberMapper.selectById(request.getFamilyMemberId());
        check(schedule != null, "排班不存在");
        check(familyMember != null && userId.equals(familyMember.getUserId()), "就诊人不存在");
        check(schedule.getRemainCount() > 0, "号源不足");
        Doctor doctor = doctorMapper.selectById(schedule.getDoctorId());

        Appointment appointment = new Appointment();
        appointment.setOrderNo(nextOrderNo("AP"));
        appointment.setUserId(userId);
        appointment.setDoctorId(doctor.getId());
        appointment.setHospitalId(doctor.getHospitalId());
        appointment.setPatientName(familyMember.getName());
        appointment.setPatientPhone(familyMember.getPhone());
        appointment.setPatientIdCard(familyMember.getIdCard());
        appointment.setPatientGender(familyMember.getGender());
        appointment.setPatientAge(Period.between(familyMember.getBirthday(), schedule.getScheduleDate()).getYears());
        appointment.setAppointmentDate(schedule.getScheduleDate());
        appointment.setAppointmentTime(schedule.getTimeSlot());
        appointment.setDiseaseDesc(request.getDiseaseDesc());
        appointment.setAmount(doctor.getRegistrationPrice());
        appointment.setStatus(1);
        appointment.setCreateTime(LocalDateTime.now());
        appointment.setUpdateTime(LocalDateTime.now());
        appointmentMapper.insert(appointment);

        schedule.setRemainCount(schedule.getRemainCount() - 1);
        scheduleMapper.updateById(schedule);

        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("orderNo", appointment.getOrderNo());
        return result;
    }

    @Override
    public Map<String, Object> getAppointment(String orderNo) {
        Appointment appointment = requireOwnedAppointment(orderNo);
        Doctor doctor = doctorMapper.selectById(appointment.getDoctorId());
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("orderNo", appointment.getOrderNo());
        result.put("doctor", doctor.getName());
        result.put("hospital", hospitalMapper.selectById(appointment.getHospitalId()).getName());
        result.put("date", appointment.getAppointmentDate());
        result.put("timeSlot", appointment.getAppointmentTime());
        result.put("patientName", appointment.getPatientName());
        result.put("status", appointment.getStatus());
        result.put("fee", appointment.getAmount());
        result.put("createTime", appointment.getCreateTime());
        return result;
    }

    @Override
    public void cancelAppointment(String orderNo) {
        Appointment appointment = requireOwnedAppointment(orderNo);
        appointment.setStatus(4);
        appointment.setUpdateTime(LocalDateTime.now());
        appointmentMapper.updateById(appointment);
    }

    @Override
    public Map<String, Object> payAppointment(String orderNo, PayRequest request) {
        Appointment appointment = requireOwnedAppointment(orderNo);
        validatePayType(request == null ? null : request.getPayType());
        appointment.setStatus(2);
        appointment.setPayTime(LocalDateTime.now());
        appointment.setUpdateTime(LocalDateTime.now());
        appointmentMapper.updateById(appointment);

        PaymentFlow paymentFlow = paymentFlowMapper.selectOne(new LambdaQueryWrapper<PaymentFlow>().eq(PaymentFlow::getBusinessOrderNo, orderNo));
        if (paymentFlow == null) {
            paymentFlow = new PaymentFlow();
            paymentFlow.setBusinessOrderNo(orderNo);
            paymentFlow.setBusinessType(1);
            paymentFlow.setActualAmount(appointment.getAmount());
            paymentFlow.setCreateTime(LocalDateTime.now());
        }
        paymentFlow.setPayMethod(request.getPayType());
        paymentFlow.setThirdPartyTradeNo("TP-" + System.nanoTime());
        paymentFlow.setPayStatus(1);
        paymentFlow.setPaySuccessTime(LocalDateTime.now());
        paymentFlow.setUpdateTime(LocalDateTime.now());
        savePaymentFlow(paymentFlow);

        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("success", true);
        return result;
    }

    @Override
    public PageResult<Map<String, Object>> listMyAppointments(Integer page, Integer pageSize, Integer status) {
        Long userId = requireUserId();
        List<Map<String, Object>> records = appointmentMapper.selectList(new LambdaQueryWrapper<Appointment>()
                        .eq(Appointment::getUserId, userId)
                        .eq(status != null, Appointment::getStatus, status)
                        .orderByDesc(Appointment::getCreateTime))
                .stream().map(item -> {
                    Map<String, Object> result = new LinkedHashMap<String, Object>();
                    result.put("orderNo", item.getOrderNo());
                    result.put("doctorName", doctorMapper.selectById(item.getDoctorId()).getName());
                    result.put("hospitalName", hospitalMapper.selectById(item.getHospitalId()).getName());
                    result.put("appointmentDate", item.getAppointmentDate());
                    result.put("appointmentTime", item.getAppointmentTime());
                    result.put("patientName", item.getPatientName());
                    result.put("amount", item.getAmount());
                    result.put("status", item.getStatus());
                    return result;
                }).collect(Collectors.toList());
        return paginate(records, page, pageSize);
    }

    @Override
    public Map<String, Object> createConsult(CreateConsultRequest request) {
        Long userId = requireUserId();
        Doctor doctor = doctorMapper.selectById(request.getDoctorId());
        FamilyMember familyMember = familyMemberMapper.selectById(request.getFamilyMemberId());
        check(doctor != null, "医生不存在");
        check(familyMember != null && userId.equals(familyMember.getUserId()), "就诊人不存在");

        Consult consult = new Consult();
        consult.setOrderNo(nextOrderNo("CO"));
        consult.setUserId(userId);
        consult.setDoctorId(doctor.getId());
        consult.setPatientName(familyMember.getName());
        consult.setPatientPhone(familyMember.getPhone());
        consult.setDiseaseDesc(request.getDiseaseDesc());
        consult.setAppointmentTime(LocalDateTime.now().plusDays(1));
        consult.setDuration(15);
        consult.setAmount(doctor.getPrice());
        consult.setStatus(1);
        consult.setCreateTime(LocalDateTime.now());
        consult.setUpdateTime(LocalDateTime.now());
        consultMapper.insert(consult);

        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("orderNo", consult.getOrderNo());
        return result;
    }

    @Override
    public Map<String, Object> getConsult(String orderNo) {
        Consult consult = requireOwnedConsult(orderNo);
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("orderNo", consult.getOrderNo());
        result.put("doctor", doctorMapper.selectById(consult.getDoctorId()).getName());
        result.put("patientName", consult.getPatientName());
        result.put("status", consult.getStatus());
        result.put("fee", consult.getAmount());
        result.put("duration", consult.getDuration());
        result.put("appointmentTime", consult.getAppointmentTime());
        result.put("createTime", consult.getCreateTime());
        return result;
    }

    @Override
    public Map<String, Object> payConsult(String orderNo, PayRequest request) {
        Consult consult = requireOwnedConsult(orderNo);
        validatePayType(request == null ? null : request.getPayType());
        consult.setStatus(2);
        consult.setPayTime(LocalDateTime.now());
        consult.setUpdateTime(LocalDateTime.now());
        consultMapper.updateById(consult);

        PaymentFlow paymentFlow = paymentFlowMapper.selectOne(new LambdaQueryWrapper<PaymentFlow>().eq(PaymentFlow::getBusinessOrderNo, orderNo));
        if (paymentFlow == null) {
            paymentFlow = new PaymentFlow();
            paymentFlow.setBusinessOrderNo(orderNo);
            paymentFlow.setBusinessType(2);
            paymentFlow.setActualAmount(consult.getAmount());
            paymentFlow.setCreateTime(LocalDateTime.now());
        }
        paymentFlow.setPayMethod(request.getPayType());
        paymentFlow.setThirdPartyTradeNo("TP-" + System.nanoTime());
        paymentFlow.setPayStatus(1);
        paymentFlow.setPaySuccessTime(LocalDateTime.now());
        paymentFlow.setUpdateTime(LocalDateTime.now());
        savePaymentFlow(paymentFlow);

        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("success", true);
        return result;
    }

    @Override
    public PageResult<Map<String, Object>> listMyConsults(Integer page, Integer pageSize, Integer status) {
        Long userId = requireUserId();
        List<Map<String, Object>> records = consultMapper.selectList(new LambdaQueryWrapper<Consult>()
                        .eq(Consult::getUserId, userId)
                        .eq(status != null, Consult::getStatus, status)
                        .orderByDesc(Consult::getCreateTime))
                .stream().map(item -> {
                    Map<String, Object> result = new LinkedHashMap<String, Object>();
                    result.put("orderNo", item.getOrderNo());
                    result.put("doctorName", doctorMapper.selectById(item.getDoctorId()).getName());
                    result.put("patientName", item.getPatientName());
                    result.put("fee", item.getAmount());
                    result.put("duration", item.getDuration());
                    result.put("status", item.getStatus());
                    return result;
                }).collect(Collectors.toList());
        return paginate(records, page, pageSize);
    }

    @Override
    public Map<String, Object> createPayment(CreatePaymentRequest request) {
        check(request.getBusinessOrderNo() != null && !request.getBusinessOrderNo().trim().isEmpty(), "业务订单号不能为空");
        check(request.getAmount() != null && request.getAmount().compareTo(BigDecimal.ZERO) > 0, "支付金额必须大于0");
        int businessType = resolveBusinessType(request.getBusinessType());
        validatePayType(request.getPayType());
        ensureBusinessOwner(request.getBusinessOrderNo(), businessType);

        PaymentFlow paymentFlow = paymentFlowMapper.selectOne(new LambdaQueryWrapper<PaymentFlow>().eq(PaymentFlow::getBusinessOrderNo, request.getBusinessOrderNo()));
        if (paymentFlow == null) {
            paymentFlow = new PaymentFlow();
            paymentFlow.setBusinessOrderNo(request.getBusinessOrderNo());
            paymentFlow.setBusinessType(businessType);
            paymentFlow.setCreateTime(LocalDateTime.now());
        }
        paymentFlow.setPayMethod(request.getPayType());
        paymentFlow.setActualAmount(request.getAmount());
        paymentFlow.setPayStatus(0);
        paymentFlow.setThirdPartyTradeNo(nextOrderNo("PAY"));
        paymentFlow.setUpdateTime(LocalDateTime.now());
        savePaymentFlow(paymentFlow);

        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("paymentNo", paymentFlow.getThirdPartyTradeNo());
        return result;
    }

    @Override
    public void paymentCallback(PaymentCallbackRequest request) {
        PaymentFlow paymentFlow = paymentFlowMapper.selectOne(new LambdaQueryWrapper<PaymentFlow>().eq(PaymentFlow::getThirdPartyTradeNo, request.getPaymentNo()));
        check(paymentFlow != null, "支付流水不存在");
        paymentFlow.setThirdPartyTradeNo(request.getTradeNo());
        paymentFlow.setPayStatus(request.getPayStatus());
        paymentFlow.setPaySuccessTime(LocalDateTime.now());
        paymentFlow.setOriginalCallback("callback-success");
        paymentFlow.setUpdateTime(LocalDateTime.now());
        paymentFlowMapper.updateById(paymentFlow);
    }

    @Override
    public Map<String, Object> getPayment(String businessOrderNo) {
        PaymentFlow paymentFlow = paymentFlowMapper.selectOne(new LambdaQueryWrapper<PaymentFlow>().eq(PaymentFlow::getBusinessOrderNo, businessOrderNo));
        if (paymentFlow == null) {
            throw new ApiException(StatusCode.NOT_FOUND, "支付流水不存在");
        }
        ensureBusinessOwner(businessOrderNo, paymentFlow.getBusinessType());
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("paymentNo", paymentFlow.getThirdPartyTradeNo());
        result.put("businessOrderNo", paymentFlow.getBusinessOrderNo());
        result.put("amount", paymentFlow.getActualAmount());
        result.put("payType", paymentFlow.getPayMethod());
        result.put("payStatus", paymentFlow.getPayStatus());
        result.put("payTime", paymentFlow.getPaySuccessTime());
        return result;
    }

    private void savePaymentFlow(PaymentFlow paymentFlow) {
        if (paymentFlow.getId() == null) {
            paymentFlowMapper.insert(paymentFlow);
        } else {
            paymentFlowMapper.updateById(paymentFlow);
        }
    }

    private Appointment requireOwnedAppointment(String orderNo) {
        Appointment appointment = appointmentMapper.selectOne(new LambdaQueryWrapper<Appointment>().eq(Appointment::getOrderNo, orderNo));
        if (appointment == null) {
            throw new ApiException(StatusCode.NOT_FOUND, "挂号订单不存在");
        }
        ensureOrderOwner(appointment.getUserId());
        return appointment;
    }

    private Consult requireOwnedConsult(String orderNo) {
        Consult consult = consultMapper.selectOne(new LambdaQueryWrapper<Consult>().eq(Consult::getOrderNo, orderNo));
        if (consult == null) {
            throw new ApiException(StatusCode.NOT_FOUND, "咨询订单不存在");
        }
        ensureOrderOwner(consult.getUserId());
        return consult;
    }

    private void ensureBusinessOwner(String businessOrderNo, Integer businessType) {
        if (Integer.valueOf(1).equals(businessType)) {
            Appointment appointment = appointmentMapper.selectOne(new LambdaQueryWrapper<Appointment>().eq(Appointment::getOrderNo, businessOrderNo));
            check(appointment != null, "挂号订单不存在");
            ensureOrderOwner(appointment.getUserId());
            return;
        }
        if (Integer.valueOf(2).equals(businessType)) {
            Consult consult = consultMapper.selectOne(new LambdaQueryWrapper<Consult>().eq(Consult::getOrderNo, businessOrderNo));
            check(consult != null, "咨询订单不存在");
            ensureOrderOwner(consult.getUserId());
            return;
        }
        throw new ApiException(StatusCode.BAD_REQUEST, "业务类型不支持");
    }

    private void ensureOrderOwner(Long ownerUserId) {
        if (!requireUserId().equals(ownerUserId)) {
            throw new ApiException(StatusCode.FORBIDDEN, "无权限访问该订单");
        }
    }

    private int resolveBusinessType(String businessType) {
        if ("appointment".equals(businessType)) {
            return 1;
        }
        if ("consult".equals(businessType)) {
            return 2;
        }
        throw new ApiException(StatusCode.BAD_REQUEST, "业务类型不支持");
    }

    private void validatePayType(Integer payType) {
        check(payType != null && (Integer.valueOf(1).equals(payType) || Integer.valueOf(2).equals(payType)), "支付方式不支持");
    }

    private String nextOrderNo(String prefix) {
        return prefix + System.currentTimeMillis() + (System.nanoTime() % 1000);
    }
}