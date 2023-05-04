import dmPython
import time
import numpy as np
import re
import os
import sys
import pandas as pd
import json
import warnings
warnings.filterwarnings("ignore")


def Connect_SQL(iniPath):
    from configparser import ConfigParser
    cfg = ConfigParser()
    cfg.read(iniPath)
    sql_cfg = dict(cfg.items("dmsql"))
    conn = dmPython.connect(
        user=sql_cfg['user'],
        password=sql_cfg['password'],
        server=sql_cfg['server'],
        port=sql_cfg['port'])
    cursor = conn.cursor()
    return cursor,conn


def get_sqltime_step(SAT_ID):
    from datetime import datetime
    Time_step = "SELECT TIME from SEC_SATELLITE_LLA where SAT_ID = '%s' order by ID desc limit 2" %(SAT_ID)
    P = pd.read_sql(Time_step, conn).TIME.values
    time_0 = datetime.timestamp(datetime.strptime(str(P[0]).replace('T',' ').split('.')[0],'%Y-%m-%d %H:%M:%S'))
    time_1 = datetime.timestamp(datetime.strptime(str(P[1]).replace('T',' ').split('.')[0],'%Y-%m-%d %H:%M:%S'))
    return time_0-time_1

# 连接 dm8
iniPath = os.path.dirname(os.path.abspath(__file__)).split('/CMS-SDC-SEC')[0]+'/DLXJS_DB.ini'
cursor,conn = Connect_SQL(iniPath)

SAT_ID = str(sys.argv[1]).replace("'",'')

# 获取卫星时间分辨率
time_sql_step = get_sqltime_step(SAT_ID)

# 读取达梦数据,每隔多长时间(秒)取一个数据？
time_step = 60  #单位：s
if time_sql_step>=time_step:
  Time_span = "select LAT,LON,ALT,TIME from SEC_SATELLITE_LLA where SAT_ID = '%s'" % (SAT_ID)
else:
  Time_span = "SELECT TIME from SEC_SATELLITE_LLA where SAT_ID = '%s' and ID MOD %d = 0 and SAT_ID = '%s'"%(SAT_ID,int(time_step/time_sql_step),SAT_ID)
P = pd.read_sql(Time_span, conn)
TIME = P.TIME.values
time_start = str(TIME[0]).replace('T',' ').split('.')[0]
time_end = str(TIME[-1]).replace('T',' ').split('.')[0]


# 以JSON形式输出结果
dic = {}
dic['start_time'] = time_start
dic['end_time'] = time_end
print('###')
print(json.dumps(dic))
print('###')
