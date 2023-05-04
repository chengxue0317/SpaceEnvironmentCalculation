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



'''
剂量深度曲线功能：
横坐标：深度(mm)
纵坐标：辐射剂量(Rad)
'''
def Connect_SQL(inipath):
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
    return cursor, conn


# 获取数据库对应卫星星下点的时间分辨率
def get_sqltime_step(SAT_ID):
	from datetime import datetime
	Time_step = "SELECT TIME from SEC_SATELLITE_LLA where SAT_ID = '%s' order by ID desc limit 2" %(SAT_ID)
	P = pd.read_sql(Time_step, conn).TIME.values
	time_0 = datetime.timestamp(datetime.strptime(str(P[0]).replace('T',' ').split('.')[0],'%Y-%m-%d %H:%M:%S'))
	time_1 = datetime.timestamp(datetime.strptime(str(P[1]).replace('T',' ').split('.')[0],'%Y-%m-%d %H:%M:%S'))
	return time_0-time_1


#计算时间天数差
def getDays(day1,day2):
    # 获取需要计算的时间戳
    time_array = time.strptime(day1, '%Y-%m-%d')
    timestamp = int(time.mktime(time_array))
    # 获取今天的时间戳
    time_array2 = time.strptime(day2, '%Y-%m-%d')
    timestamp2 = int(time.mktime(time_array2))
    if timestamp2 < timestamp:
        timestamp2, timestamp = timestamp, timestamp2
    # 时间戳相减，然后算出天数
    day = int((timestamp2 - timestamp)/(24 * 60 * 60))
    if day<1:
    	day = 1
    return day

# 更新inp
def updateFile(file,old_str,new_str):
    with open(file, "r", encoding="utf-8") as f1,open("%s.bak" % file, "w", encoding="utf-8") as f2:
        for line in f1:
            f2.write(re.sub(old_str,new_str,line))
    os.remove(file)
    os.rename("%s.bak" % file, file)


# 计算辐射剂量
def Cal_shieldose2(Var,material,shield_type,sptrum_point,sptrum_E,sptrum_fx,sum_day):
	# 创建模型运行的inp文件
	#if os.path.exists(path+filename) == False:
	shutil.copyfile(current_dir+'/example_seu_ini.inp',current_dir+'/example_seu.inp')

	# 写入SHIELDOSE input文件
	filename = current_dir+'/example_seu.inp'
	find = 'aa'
	replace = str(material)+' 1 70 '+'2'
	updateFile(filename,find,replace)
	if Var == 'p':
		find = 'proton'
		replace = str(sptrum_point)
		updateFile(filename,find,replace)
		find = 'electron'
		replace = '0'
		updateFile(filename,find,replace)
	else:
		find = 'electron'
		replace = str(sptrum_point)
		updateFile(filename,find,replace)
		find = 'proton'
		replace = '0'
		updateFile(filename,find,replace)

	find = 'cc'
	replace = sptrum_E
	updateFile(filename,find,replace)
	find = 'dd'
	replace = sptrum_fx
	updateFile(filename,find,replace)
	find = 'ee'
	replace = str(sum_day*24*3600)
	updateFile(filename,find,replace)
	os.system('cp '+current_dir+'/example_seu.inp '+current_dir+'/'+dir_name)
	print('cp '+current_dir+'/example_seu.inp '+current_dir+'/'+dir_name)

	# 运行FORTRAN seu.exe
	os.system('cd '+current_dir+'/'+dir_name+';./seu.exe; mv *.OUT results.txt')



	# 读取生成的
	filename = current_dir+'/'+dir_name+'/results.txt'
	file = open(filename)
	f_size = len(file.readlines())

	# target does data
	ind = np.zeros(3)
	s = 0
	file = open(filename)
	for i in range(0,f_size):
		f = file.readline()
		f = f.split()
		f = np.array(f)
		if len(f)!=0:
			if f[0]=='Z(mils)':
				ind[s] = i+1
				s = s+1


#Shield_Type 1: DOSE AT TRANSMISSION SURFACE OF FINITE SLAB SHIELDS
#Shield_Type 2: DOES IN SEMI-INFINITE SLAB SHIELDS
#Shield_Type 3: 1/2 DOES AT CENTER OF SLAB SHIELDS

	
	z_mils = []
	z_mm = []
	z_g = []
	does = []
	file = open(filename)
	for i in range(0,f_size):
		f = file.readline()
		if shield_type == 1:	
			if (i>ind[0]) & (i<ind[0]+77) & (len(f)!=0):
				f = f.split()
				if (len(f)!=0):
					z_mils.append(f[0])
					z_mm.append(f[1])	
					z_g.append(f[2])
					does.append(f[-1])
		elif shield_type == 2:
			if (i>ind[1]) & (i<ind[1]+77) & (len(f)!=0):
				f = f.split()
				if (len(f)!=0):
					z_mils.append(f[0])
					z_mm.append(f[1])	
					z_g.append(f[2])
					does.append(f[-1])
		else:
			if (i>ind[2]) & (i<ind[2]+77) & (len(f)!=0):
				f = f.split()
				if (len(f)!=0):
					z_mils.append(f[0])
					z_mm.append(f[1])	
					z_g.append(f[2])
					does.append(f[-1])
	return does,z_mm



# 建立模拟结果文件夹(唯一性)
current_dir = os.path.dirname(os.path.abspath(__file__))
while True:
	dir_name = str(random.randint(1,999999))
	if os.path.exists(dir_name) == False:
		os.system('mkdir '+current_dir+'/'+dir_name)
		break
os.system('cp '+current_dir+'/'+ 'ELBRBAS2.DAT '+current_dir+'/'+dir_name)
os.system('cp '+current_dir+'/'+ 'PROTBAS2.DAT '+current_dir+'/'+dir_name)
os.system('cp '+current_dir+'/'+ 'seu.exe '+current_dir+'/'+dir_name)


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
dir_name_pflux,filedir_pflux =ae.ap8ae8(time1d, ALT, LON, LAT, whatf='differential', whichm='ap8min')

# Ap8max模拟(高能电子)
dir_name_eflux,filedir_eflux =ae.ap8ae8(time1d, ALT, LON, LAT, whatf='differential', whichm='ae8min')


# 读取flux文件
def Cal_2ddoes(Var,dir_name_flux,filedir_flux,sptrum_point,sptrum_E):
    txt_all = []
    f = open(filedir_flux+'/'+dir_name_flux+'/flux.txt')
    file_size = len(f.readlines())
    ff = open(filedir_flux+'/'+dir_name_flux+'/flux.txt')
    for i in range(file_size):
        txt = list(map(float,ff.readline().replace('\n','').split(',')[3:3+sptrum_point]))
        txt_all.append(txt)
    txt_all = np.array(txt_all)
    ind = np.where(txt_all<0)
    txt_all[ind] = 0
    txt_all = np.mean(txt_all,axis=0)
    s = 0
    for i in txt_all:
        txt_all[s] = round(i,1)
        s+=1

	# 计算时间差
    time_min = str(TIME[0]).split('T')[0]
    time_max = str(TIME[-1]).split('T')[0]
    sum_day = getDays(time_min,time_max)

	# 选用哪一种靶材料？
    material = int(sys.argv[4])
    if (material>11)|(material<1):
        os.system('rm -rf '+current_dir+'/'+dir_name)
        sys.exit()
    '''	
    靶材料有：1：铝（Al）,2：石墨（Graphite）,3：硅（Silicon）,4：空气（Air）,
    5：身体骨骼（Bone），6：氟化钙（CaF2）,7：砷化镓（GaAs）,8：氟化锂（LiF）,
    9：二氧化硅（SiO2）,10：身体组织（Tissue），11：水（Water）
    '''

	# 选用哪种屏蔽方式？
    shield_type = int(sys.argv[5])
    if (shield_type>3)|(shield_type<1):
        os.system('rm -rf '+current_dir+'/'+dir_name)
        sys.exit()

	#能谱点的个数？
	#sptrum_point = 12

	#积分能谱能量？
	#sptrum_E = [0.1,0.5,1,3,5,10,20,40,80,120,160,200]
    sptrum_E = str(sptrum_E)
    sptrum_E = sptrum_E[1:-1]
    sptrum_E = sptrum_E.replace(',',' ')

	#积分能谱通量？
    sptrum_fx = np.zeros(sptrum_point)
    for i in range(sptrum_point):
        sptrum_fx[i] = txt_all[i]-0.5

	# 剔除0值变0.1,否则报错
    ind = np.where(sptrum_fx<=0)
    sptrum_fx[ind]=0.01
    sptrum_fx = str(sptrum_fx)
    sptrum_fx = sptrum_fx[1:-1]
    if Var == 'p':
        does, z_mm = Cal_shieldose2('p',material,shield_type,sptrum_point,sptrum_E,sptrum_fx,sum_day) #计算辐射剂量
    else:
        does, z_mm = Cal_shieldose2('e',material,shield_type,sptrum_point,sptrum_E,sptrum_fx,sum_day) #计算辐射剂量
    does = list(map(float,does))
    z_mm = list(map(float,z_mm))
    does = np.array(does)
    z_mm = np.array(z_mm)
    return z_mm,does

sptrum_point = 12
sptrum_E = [0.1,0.5,1,3,5,10,20,40,80,120,160,200]
z_mm,does_p = Cal_2ddoes('p',dir_name_pflux,filedir_pflux,sptrum_point,sptrum_E)
sptrum_point = 7
sptrum_E = [0.05,0.1,0.15,0.35,0.65,1.2,2.0]
z_mm,does_e = Cal_2ddoes('e',dir_name_eflux,filedir_eflux,sptrum_point,sptrum_E)
# 总辐射剂量
does = (does_p+does_e).astype(np.int)
# 外推未来辐射剂量
sum_day = getDays(str(TIME[0]).split('T')[0],str(TIME[-1]).split('T')[0])
does_1 = (does/sum_day*365).astype(np.int)
does_2 = (does/sum_day*365*2).astype(np.int)
does_3 = (does/sum_day*365*3).astype(np.int)
does_4 = (does/sum_day*365*4).astype(np.int)
does_5 = (does/sum_day*365*5).astype(np.int)
does_6 = (does/sum_day*365*6).astype(np.int)
does_7 = (does/sum_day*365*7).astype(np.int)
does_8 = (does/sum_day*365*8).astype(np.int)
does_9 = (does/sum_day*365*9).astype(np.int)
does_10 =(does/sum_day*365*10).astype(np.int)


# 计算起止时间
real_time_start = str(TIME[0]).replace('T',' ').split('.')[0]
real_time_end = str(TIME[-1]).replace('T',' ').split('.')[0]

# 剔除异常数据
index = [15,16,17,18]
z_mm = np.delete(z_mm,index)
does = np.delete(does,index)
does_1 = np.delete(does_1,index)
does_2 = np.delete(does_2,index)
does_3 = np.delete(does_3,index)
does_4 = np.delete(does_4,index)
does_5 = np.delete(does_5,index)
does_6 = np.delete(does_6,index)
does_7 = np.delete(does_7,index)
does_8 = np.delete(does_8,index)
does_9 = np.delete(does_9,index)
does_10 = np.delete(does_10,index)



print('******')
print(len(z_mm))

# 以JSON形式输出结果
dic = {}
dic['start_time'] = str(real_time_start)
dic['end_time'] = str(real_time_end)
dic['depth'] = z_mm.tolist()
dic['does'] = does.tolist()
dic['does1'] = does_1.tolist()
dic['does2'] = does_2.tolist()
dic['does3'] = does_3.tolist()
dic['does4'] = does_4.tolist()
dic['does5'] = does_5.tolist()
dic['does6'] = does_6.tolist()
dic['does7'] = does_7.tolist()
dic['does8'] = does_8.tolist()
dic['does9'] = does_9.tolist()
dic['does10'] = does_10.tolist()


print('###')
print(json.dumps(dic))
print('###')

# 删除文件夹
os.system('rm -rf '+current_dir+'/'+dir_name)
os.system('rm -rf '+filedir_pflux+'/'+dir_name_pflux)
os.system('rm -rf '+filedir_eflux+'/'+dir_name_eflux)



#plt.plot(depth_js,np.array(does_js),label='history')
#plt.plot(depth_js,np.array(does_js)/sum_day*365,label='future 1 year')
#plt.plot(depth_js,np.array(does_js)/sum_day*365*2,label='future 2 year')
#plt.plot(depth_js,np.array(does_js)/sum_day*365*3,label='future 3 year')
#plt.plot(depth_js,np.array(does_js)/sum_day*365*4,label='future 4 year')
#plt.plot(depth_js,np.array(does_js)/sum_day*365*5,label='future 5 year')
#plt.yscale('log')
#plt.xlim((0,15))
#plt.legend()
#plt.savefig('test.jpg',dpi=300)
