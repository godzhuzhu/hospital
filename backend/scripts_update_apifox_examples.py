import copy
import json
from pathlib import Path

path = Path(r'd:\Code\hospital\backend\apifox-openapi.json')
with path.open('r', encoding='utf-8') as f:
    doc = json.load(f)

paths = doc['paths']
components = doc['components']


def ensure_headers(op):
    params = op.setdefault('parameters', [])
    if not any(isinstance(p, dict) and p.get('name') == 'Authorization' and p.get('in') == 'header' for p in params):
        params.append({
            'name': 'Authorization',
            'in': 'header',
            'required': False,
            'schema': {'type': 'string'},
            'example': 'Bearer token-1-123456789',
            'description': '登录后携带的 Bearer Token'
        })


def ensure_json_response(op, code='200'):
    response = op['responses'][code]
    if '$ref' in response:
        ref = response['$ref']
        name = ref.split('/')[-1]
        response = copy.deepcopy(components['responses'][name])
        op['responses'][code] = response
    content = response.setdefault('content', {})
    app_json = content.setdefault('application/json', {})
    return app_json


def set_request_examples(op, examples):
    op['requestBody']['content']['application/json']['examples'] = examples


def set_response_examples(op, examples):
    app_json = ensure_json_response(op)
    app_json['examples'] = examples

# auth
op = paths['/api/auth/register']['post']
op['x-apifox-note'] = '以下 examples 来自现有集成测试，可在 Apifox 中直接作为调试用例。'
set_request_examples(op, {
    'registerSuccess': {'summary': '注册成功', 'value': {'phone': '13900139000', 'password': '123456', 'confirmPassword': '123456', 'captcha': '8888'}}
})
set_response_examples(op, {
    'registerSuccess': {'summary': '注册成功响应', 'value': {'code': 200, 'message': 'success', 'data': {'userId': 2}}}
})

op = paths['/api/auth/login']['post']
set_request_examples(op, {
    'loginSuccess': {'summary': '登录成功', 'value': {'phone': '13800138000', 'password': '123456'}}
})
set_response_examples(op, {
    'loginSuccess': {'summary': '登录成功响应', 'value': {'code': 200, 'message': 'success', 'data': {'token': 'token-1-123456789', 'userInfo': {'id': 1, 'name': '张三', 'phone': '13800138000', 'avatar': '/avatar/user-1.png'}}}}
})

op = paths['/api/auth/me']['get']
ensure_headers(op)
set_response_examples(op, {
    'meSuccess': {'summary': '获取当前用户成功', 'value': {'code': 200, 'message': 'success', 'data': {'userInfo': {'id': 1, 'name': '张三', 'phone': '13800138000', 'avatar': '/avatar/user-1.png'}}}}
})

op = paths['/api/auth/change-password']['post']
ensure_headers(op)
set_request_examples(op, {
    'changePasswordSuccess': {'summary': '修改密码成功', 'value': {'oldPassword': '123456', 'newPassword': 'abc12345', 'confirmPassword': 'abc12345'}}
})
ensure_headers(paths['/api/auth/logout']['post'])

# resources
op = paths['/api/home/index']['get']
set_response_examples(op, {
    'homeIndex': {'summary': '首页聚合数据', 'value': {'code': 200, 'message': 'success', 'data': {'banners': [{'image': '/img/banner-1.png', 'link': '/doctor/1'}], 'recommendHospitals': [{'id': 1, 'name': '北京第一人民医院'}], 'recommendDoctors': [{'id': 1, 'name': '李主任'}], 'recommendDiseases': [{'id': 1, 'name': '高血压'}], 'recommendArticles': [{'id': 1, 'title': '高血压日常管理'}]}}}
})

op = paths['/api/hospitals']['get']
set_response_examples(op, {
    'hospitalList': {'summary': '医院列表', 'value': {'code': 200, 'message': 'success', 'data': {'records': [{'id': 1, 'name': '北京第一人民医院', 'level': '三甲', 'province': '北京', 'city': '北京'}, {'id': 2, 'name': '上海瑞康医院', 'level': '三甲', 'province': '上海', 'city': '上海'}], 'total': 2, 'page': 1, 'pageSize': 10}}}
})

op = paths['/api/doctors/{id}/schedules']['get']
set_response_examples(op, {
    'doctorSchedules': {'summary': '医生排班', 'value': {'code': 200, 'message': 'success', 'data': [{'id': 1, 'scheduleDate': '2026-07-18', 'weekDay': '周六', 'timeSlot': '08:00-09:00', 'remainCount': 8, 'totalCount': 20, 'registrationPrice': 18.0, 'status': 1}]}}
})

op = paths['/api/search/global']['get']
set_response_examples(op, {
    'globalSearch': {'summary': '全局搜索高血压', 'value': {'code': 200, 'message': 'success', 'data': {'hospitalList': [], 'doctorList': [{'id': 1, 'name': '李主任'}], 'diseaseList': [{'id': 1, 'name': '高血压'}], 'articleList': [{'id': 1, 'title': '高血压日常管理'}]}}}
})

# appointment
op = paths['/api/appointments']['post']
ensure_headers(op)
set_request_examples(op, {
    'createAppointment': {'summary': '创建挂号订单', 'value': {'scheduleId': 1, 'familyMemberId': 1, 'diseaseDesc': '头晕'}}
})
set_response_examples(op, {
    'createAppointment': {'summary': '创建挂号成功', 'value': {'code': 200, 'message': 'success', 'data': {'orderNo': 'AP202607170002'}}}
})

op = paths['/api/appointments/{orderNo}']['get']
ensure_headers(op)
set_response_examples(op, {
    'appointmentDetail': {'summary': '挂号订单详情', 'value': {'code': 200, 'message': 'success', 'data': {'orderNo': 'AP202607170001', 'doctor': {'id': 1, 'name': '李主任'}, 'hospital': {'id': 1, 'name': '北京第一人民医院'}, 'date': '2026-07-18', 'timeSlot': '08:00-09:00', 'patientName': '张三', 'status': 2, 'fee': 18.0, 'createTime': '2026-07-17T10:00:00'}}}
})

op = paths['/api/appointments/{orderNo}/pay']['post']
ensure_headers(op)
set_request_examples(op, {
    'payAppointment': {'summary': '支付挂号订单', 'value': {'payType': 2}},
    'payAppointmentUnsupported': {'summary': '不支持的支付方式', 'value': {'payType': 9}}
})
set_response_examples(op, {
    'payAppointmentSuccess': {'summary': '支付成功', 'value': {'code': 200, 'message': 'success', 'data': {'success': True, 'payUrl': None}}},
    'payAppointmentFail': {'summary': '支付方式不支持', 'value': {'code': 400, 'message': '支付方式不支持', 'data': None}}
})
ensure_headers(paths['/api/appointments/my']['get'])

# consult
op = paths['/api/consults']['post']
ensure_headers(op)
set_request_examples(op, {
    'createConsult': {'summary': '创建咨询订单', 'value': {'doctorId': 1, 'familyMemberId': 1, 'diseaseDesc': '复诊'}}
})

op = paths['/api/consults/{orderNo}']['get']
ensure_headers(op)
set_response_examples(op, {
    'consultDetail': {'summary': '咨询订单详情', 'value': {'code': 200, 'message': 'success', 'data': {'orderNo': 'CO202607170001', 'doctor': {'id': 1, 'name': '李主任'}, 'patientName': '张三', 'status': 2, 'fee': 39.9, 'duration': 15, 'appointmentTime': '2026-07-18T10:00:00', 'createTime': '2026-07-17T10:00:00'}}}
})

op = paths['/api/consults/{orderNo}/pay']['post']
ensure_headers(op)
set_request_examples(op, {
    'payConsult': {'summary': '支付咨询订单', 'value': {'payType': 1}}
})
set_response_examples(op, {
    'payConsultSuccess': {'summary': '支付成功', 'value': {'code': 200, 'message': 'success', 'data': {'success': True, 'payUrl': None}}}
})
ensure_headers(paths['/api/consults/my']['get'])

# payments
op = paths['/api/payments']['post']
ensure_headers(op)
set_request_examples(op, {
    'createPaymentAppointment': {'summary': '为挂号单创建支付流水', 'value': {'businessOrderNo': 'AP202607170001', 'businessType': 'appointment', 'amount': 18.0, 'payType': 2}},
    'createPaymentConsult': {'summary': '为咨询单创建支付流水', 'value': {'businessOrderNo': 'CO202607170001', 'businessType': 'consult', 'amount': 39.9, 'payType': 1}},
    'createPaymentUnsupportedBusiness': {'summary': '不支持的业务类型', 'value': {'businessOrderNo': 'AP202607170001', 'businessType': 'unknown', 'amount': 18.0, 'payType': 2}}
})
set_response_examples(op, {
    'createPaymentSuccess': {'summary': '创建支付流水成功', 'value': {'code': 200, 'message': 'success', 'data': {'paymentNo': 'PAY202607170001'}}},
    'createPaymentFail': {'summary': '业务类型不支持', 'value': {'code': 400, 'message': '业务类型不支持', 'data': None}}
})

op = paths['/api/payments/callback']['post']
set_request_examples(op, {
    'paymentCallbackSuccess': {'summary': '支付回调成功', 'value': {'paymentNo': 'PAY202607170001', 'tradeNo': 'TRADE-001', 'payStatus': 1}}
})

op = paths['/api/payments/{businessOrderNo}']['get']
ensure_headers(op)
set_response_examples(op, {
    'paymentDetail': {'summary': '支付流水详情', 'value': {'code': 200, 'message': 'success', 'data': {'paymentNo': 'PAY202607170001', 'businessOrderNo': 'AP202607170001', 'amount': 18.0, 'payType': 2, 'payStatus': 1, 'payTime': '2026-07-17T10:05:00'}}}
})

# user center
op = paths['/api/user/profile']['get']
ensure_headers(op)
set_response_examples(op, {
    'profileSuccess': {'summary': '个人资料', 'value': {'code': 200, 'message': 'success', 'data': {'id': 1, 'name': '张三', 'phone': '13800138000', 'email': 'harry@example.com', 'avatar': '/avatar/user-1.png', 'gender': 1, 'birthday': '1990-01-01'}}}
})

op = paths['/api/user/profile']['put']
ensure_headers(op)
set_request_examples(op, {
    'updateProfile': {'summary': '更新个人资料', 'value': {'name': '张三丰', 'gender': 1, 'birthday': '1991-01-01', 'email': 'new@example.com', 'avatar': '/avatar/new.png'}}
})

ensure_headers(paths['/api/family-members']['get'])
op = paths['/api/family-members']['post']
ensure_headers(op)
set_request_examples(op, {
    'createFamilyMember': {'summary': '新增就诊人', 'value': {'name': '小张', 'phone': '13800138111', 'relation': '子女', 'gender': 1, 'birthday': '2015-05-01', 'idCard': '110101201505010033', 'isDefault': 0}}
})

op = paths['/api/family-members/{id}']['put']
ensure_headers(op)
set_request_examples(op, {
    'updateFamilyMember': {'summary': '更新就诊人', 'value': {'name': '张三-更新', 'phone': '13800138000', 'relation': '本人', 'gender': 1, 'birthday': '1990-01-01', 'idCard': '110101199001010011', 'isDefault': 1}}
})
ensure_headers(paths['/api/family-members/{id}']['delete'])

ensure_headers(paths['/api/reviews/my']['get'])
op = paths['/api/reviews']['post']
ensure_headers(op)
set_request_examples(op, {
    'createReview': {'summary': '提交评价', 'value': {'orderType': 1, 'orderId': 1, 'doctorId': 1, 'content': '服务很好', 'rating': 5}}
})

ensure_headers(paths['/api/messages']['get'])
ensure_headers(paths['/api/messages/{id}/read']['post'])
ensure_headers(paths['/api/messages/unread-count']['get'])
ensure_headers(paths['/api/feedbacks']['get'])
op = paths['/api/feedbacks']['post']
ensure_headers(op)
set_request_examples(op, {
    'createFeedback': {'summary': '提交反馈', 'value': {'type': 2, 'content': '客服响应慢', 'images': ['/img/x.png', '/img/y.png']}}
})

ensure_headers(paths['/api/follow/my']['get'])
op = paths['/api/follow/{type}/{id}']['post']
ensure_headers(op)
set_response_examples(op, {
    'createFollow': {'summary': '关注成功', 'value': {'code': 200, 'message': 'success', 'data': {'id': 4}}}
})
ensure_headers(paths['/api/follow/{type}/{id}']['delete'])

# system
op = paths['/api/configs/{key}']['get']
set_response_examples(op, {
    'paymentMethods': {'summary': '支付方式配置', 'value': {'code': 200, 'message': 'success', 'data': {'key': 'paymentMethods', 'value': [{'label': '支付宝', 'value': 1}, {'label': '微信', 'value': 2}]}}}
})

op = paths['/api/health']['get']
set_response_examples(op, {
    'healthUp': {'summary': '健康检查通过', 'value': {'code': 200, 'message': 'success', 'data': {'status': 'UP', 'timestamp': '2026-07-17T12:00:00'}}}
})

with path.open('w', encoding='utf-8') as f:
    json.dump(doc, f, ensure_ascii=False, indent=2)

print('UPDATED')