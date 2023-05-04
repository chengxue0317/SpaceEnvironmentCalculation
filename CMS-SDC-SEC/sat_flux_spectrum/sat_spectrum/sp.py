import re
import os
import sys
import math
import scipy
import shutil
import dmPython
import warnings
import numpy as np
np.set_printoptions(threshold=sys.maxsize)
import pandas as pd
import datetime
import time
import random
warnings.filterwarnings("ignore")
import matplotlib.pyplot as plt
import json
from pathlib import Path
sys.path.append(str(Path(__file__).resolve().parents[1]))
import AP8AE8.ap8ae8 as ae

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


# 更新for
def updateFile(file,old_str,new_str):
    with open(file, "r", encoding="utf-8") as f1,open("%s.bak" % file, "w", encoding="utf-8") as f2:
        for line in f1:
            f2.write(re.sub(old_str,new_str,line))
    os.remove(file)
    os.rename("%s.bak" % file, file)


# 获取数据库对应卫星星下点的时间分辨率
def get_sqltime_step(SAT_ID):
  from datetime import datetime
  Time_step = "SELECT TIME from SEC_SATELLITE_LLA where SAT_ID = '%s' order by ID desc limit 2" %(SAT_ID)
  P = pd.read_sql(Time_step, conn).TIME.values
  time_0 = datetime.timestamp(datetime.strptime(str(P[0]).replace('T',' ').split('.')[0],'%Y-%m-%d %H:%M:%S'))
  time_1 = datetime.timestamp(datetime.strptime(str(P[1]).replace('T',' ').split('.')[0],'%Y-%m-%d %H:%M:%S'))
  return time_0-time_1


# 更新F90文件
m = str(float(sys.argv[5])).replace('"','').replace("'",'')
n = str(float(sys.argv[6])).replace('"','').replace("'",'')
current_dir = os.path.dirname(os.path.abspath(__file__).replace('sat_spectrum',''))+'/AP8AE8'
shutil.copyfile(current_dir+'/AP8AE8_ini.f90',current_dir+'/AP8AE8.f90')
filename = current_dir+'/AP8AE8.f90'
find = 'E_start'
replace = m
updateFile(filename,find,replace)
find = 'E_end'
replace = n
updateFile(filename,find,replace)
if (float(m)>=float(n)) | (float(m)<0) | (float(n)<0):
    sys.exit()

# 编译Fortran程序
os.system('rm -rf '+current_dir+'/ap8ae8')
command = 'cd '+current_dir+'; gfortran AP8AE8.f90 libirbem.so -o ap8ae8'
os.system(command)


# 连接 dm8
iniPath = os.path.dirname(os.path.abspath(__file__)).split('/CMS-SDC-SEC')[0]+'/DLXJS_DB.ini'
cursor,conn = Connect_SQL(iniPath)


# 选取时间和卫星名称
Time_start = str(sys.argv[1]).replace('"','')
Time_end = str(sys.argv[2]).replace('"','')
SAT_ID = str(sys.argv[3]).replace("'",'')

# 获取卫星时间分辨率
time_sql_step = get_sqltime_step(SAT_ID)


# 读取达梦数据,每隔多长时间(秒)取一个数据？
time_step = 60  #单位：s
if time_sql_step>=time_step:
  Time_span = "select LAT,LON,ALT,TIME from SEC_SATELLITE_LLA where TIME between '%s' and '%s' and SAT_ID = '%s'" % (Time_start,Time_end,SAT_ID)
else:
  Time_span = "SELECT * from SEC_SATELLITE_LLA where SAT_ID = '%s' and ID MOD %d = 0 and TIME between '%s' and '%s' and SAT_ID = '%s'"%(SAT_ID,int(time_step/time_sql_step),Time_start,Time_end,SAT_ID)
P = pd.read_sql(Time_span, conn)

# 读取时间
TIME = P.TIME.values
time1d = []
for i in range(len(TIME)):
  time1d.append(str(TIME[i]).replace('T',' ').split('.')[0])


# Ap8max模拟(高能质子)
LAT = P.LAT.values
LON = P.LON.values
ALT = P.ALT.values

# 模拟电子还是质子？
if int(sys.argv[4]) == 1:
  dir_name_flux,filedir_flux =ae.ap8ae8(time1d, ALT, LON, LAT, whatf='above', whichm='ap8min')
else:
  # Ap8max模拟(高能电子)
  dir_name_flux,filedir_flux =ae.ap8ae8(time1d, ALT, LON, LAT, whatf='above', whichm='ae8min')


# 读取flux文件
def Read_txt(dir_name_flux,filedir_flux):
  txt_all = []
  f = open(filedir_flux+'/'+dir_name_flux+'/flux.txt')
  file_size = len(f.readlines())
  ff = open(filedir_flux+'/'+dir_name_flux+'/flux.txt')
  for i in range(file_size):
    txt = list(map(float,ff.readline().replace('\n','').split(',')[3:5]))
    txt_all.append(txt)
  txt_all = np.array(txt_all)
  ind = np.where(txt_all<0)
  txt_all[ind] = 0
  return txt_all

# 读取模拟结果数据
fp = Read_txt(dir_name_flux,filedir_flux)
df = pd.DataFrame()
df['sp'] = fp[:,0]-fp[:,1]
df['LAT'] = LAT
df['LON'] = LON
df ['TIME'] = time1d

# 以JSON形式输出结果
dic = {}
dic['time'] = time1d
dic['sp'] = (fp[:,0]-fp[:,1]).astype(np.int).tolist()
dic['lat'] = np.around(LAT,2).tolist()
dic['lon'] = np.around(LON,2).tolist()
dic['alt'] = np.around(ALT,2).tolist()

print('###')
print(json.dumps(dic))
print('###')

# 删除多余文件
os.system('rm -rf '+filedir_flux+'/'+dir_name_flux)
