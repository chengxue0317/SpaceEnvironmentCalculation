import os
import pandas as pd
import time
import sys


def forecast_length(conn, LLAtime_terminated):
    
    # LLAtime_terminated: 获取轨道数据最后一天 '2023-02-27T23:45:00.00000000'
    def timestamp(shijian):
        s_t = time.strptime(shijian, "%Y-%m-%d %H:%M:%S")
        mkt = int(time.mktime(s_t))
        return (mkt)

    timef107_s = []
    f107_s = []
    # 获取数据库F107指数最后一天时间
    sql_command = "select TIME from SEC_F107_FLUX order by ID desc limit 1"
    P = pd.read_sql(sql_command, conn).TIME.values
    F107time_terminated = str(P[0]).split('T')[0]

    # F107_forecast_day: 往后预报的天数
    F107_forecast_day = int((timestamp(LLAtime_terminated.split('T')[0] + ' 00:00:00')-timestamp(F107time_terminated + ' 00:00:00'))/86400 + 40)
    if F107_forecast_day > 100:
        print('预报时间超出期限,超出', F107_forecast_day-100, '天')
        sys.exit()
    # 若时间超过F107观测范围，且在算法预报时长最大范围内，则进行预报
    if F107_forecast_day > 0:
        sys.path.append(os.path.dirname(os.path.abspath(__file__)).replace('atm_density', 'F107_forecast/forecast_code'))
        from Spaceweather_index_forecast_multilinear import forecast_solar_index
        timef107_s, f107_s = forecast_solar_index('F10.7')

    # 若时间超过AP观测范围，且在算法预报时长最大范围内，则进行预报
    timeap_s = []
    ap_s = []
    # 获取数据库AP指数最后一天时间
    sql_command = "select * from SEC_AP_INDEX order by ID desc limit 1"
    P = pd.read_sql(sql_command, conn)
    aptime_terminated = str(P.TIME.values[0]).split('.')[0]
    # 数据库（AP）最后的时间减去星下点最后的时间，当数据库时间滞后于星下点时间时不做预报，反之进行预报
    if ((time.mktime(time.strptime(aptime_terminated, "%Y-%m-%dT%H:%M:%S")) - time.mktime(time.strptime(LLAtime_terminated.split('.')[0], "%Y-%m-%dT%H:%M:%S")))/3600) < 0:
        sys.path.append(os.path.dirname(os.path.abspath(__file__)).replace('atm_density', 'ap_forecast'))
        from ap_index_forecast import forecast_ap
        timeap_s, ap_s = forecast_ap()
    print('F107观测终止时间：', F107time_terminated)
    print('AP观测终止时间：', aptime_terminated)
    return timef107_s, f107_s, timeap_s, ap_s