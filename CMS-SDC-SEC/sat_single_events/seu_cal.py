import dmPython
import re
import os
import sys
import math
import scipy
import shutil
import warnings
import numpy as np
np.set_printoptions(threshold=sys.maxsize)
import pandas as pd
import random
warnings.filterwarnings("ignore")
import json

'''
单粒子效应：以单粒子翻转为主。半导体期间的单粒子翻转率是指器件每天
每bit发生单粒子翻转活错误的概率。计算空间质子产生的单粒子翻转率，
需知道空间质子能谱，以及器件对不同能量质子的单粒子翻转截面，两者
积分即可得到质子单粒子翻转率的预测值。常采用Bendel双参数法来计算单
粒子翻转截面。

单粒子效应对各种轨道飞行器都是有危害的，是目前最严重的空间环境效应之一。
我们能够预测不同轨道的航天器在不同空间环境条件下的器件的单粒子翻转率，
也可以预测复杂三维屏蔽条件下敏感器件的单粒子翻转率，为航天器设计及器件
防护提供参考。

参考文献：[1] SSO及GEO典型太阳质子事件单粒子翻转率估算。
         [2] Variation in proton-induced upsets rates from large solar flares using an improved SEU model.
         [3] Bendel W L, Petersen E L. Proton upsets in orbit.
'''

'''
定义Cal_cross函数，计算粒子的翻转截面。
E为入射质子能量，单位MeV；
E_pl为发生单粒子翻转所需的能量阈值,单位为MeV；
Cross_pl为单粒子翻转的极限截面,单位为cm2 bit-1；
''' 
def Cal_cross(E,E_pl,Cross_pl):
	E = E.split('  ')
	Cross = np.zeros(len(E))
	for i in range(0,len(E)):
		Y = math.sqrt(18/E_pl)*(int(E[i])-E_pl)
		Y = -0.18*math.sqrt(Y)
		Y = pow(1-math.exp(Y),4)
		Y = Cross_pl*Y
		Cross[i] = Y
	return Cross

'''
定义Cal_crossrate函数，计算粒子的翻转率。
flux为质子的积分能谱，单位为cm-2 d-1 MeV-1;
Cross为单粒子翻转截面，单位cm2 bit-1;
E_pl为单粒子翻转质子能量阈值，单位为：MeV；
'''
def Cal_crossrate(Cross,flux):
	Crossrate = np.zeros(len(Cross)-1)
	for i in range(0,len(Cross)-1):
		flux_diff = flux[i]-flux[i+1]
		Cross_diff = (Cross[i]+Cross[i+1])/2
		Crossrate[i] = flux_diff*Cross_diff
	Crossrate = sum(Crossrate)
	return Crossrate

# 更新inp
def updateFile(file,old_str,new_str):
    with open(file, "r", encoding="utf-8") as f1,open("%s.bak" % file, "w", encoding="utf-8") as f2:
        for line in f1:
            f2.write(re.sub(old_str,new_str,line))
    os.remove(file)
    os.rename("%s.bak" % file, file)


# 计算辐射剂量
def Cal_shieldose2(material,shield_type,sptrum_point,sptrum_E,sptrum_fx):
	# 创建模型运行的inp文件
	#if os.path.exists(path+filename) == False:
	shutil.copyfile(current_dir+'/example_seu_ini.inp',current_dir+'/example_seu.inp')

	# 写入SHIELDOSE input文件
	filename = current_dir+'/example_seu.inp'
	find = 'aa'
	replace = str(material)+' 1 70 '+'2'
	updateFile(filename,find,replace)
	find = 'bb'
	replace = str(sptrum_point)
	updateFile(filename,find,replace)
	find = 'cc'
	replace = sptrum_E
	updateFile(filename,find,replace)
	find = 'dd'
	replace = sptrum_fx
	updateFile(filename,find,replace)
	os.system('cp '+current_dir+'/example_seu.inp '+current_dir+'/'+dir_name)

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


try:

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
	conn = dmPython.connect(user='SDC',password='sdc123456',server='219.145.62.54',\
	port=15236)
	cursor = conn.cursor()

	# 选取时间
	Time_start = str(sys.argv[1])
	Time_end = str(sys.argv[2])
	SAT_ID = str(sys.argv[3])

	# 判断所选卫星是否在数据库内
	SAT_name = "select SAT_ID from SEC_PARTICLE_FLUX where TIME between '%s' and '%s'"%(Time_start,Time_end)
	P = pd.read_sql(SAT_name,conn)
	P = str(P.SAT_ID.values).replace(' ','')
	# 如果不在，删除文件夹并退出
	k = SAT_ID in P
	if k == False:
		os.system('rm -rf '+current_dir+'/'+dir_name)
		sys.exit()

	Time_span = "select P3,P4,P5,P6,TIME from SEC_PARtICLE_FLUX where TIME between '%s' and '%s' and SAT_ID = '%s'" % (Time_start,Time_end,SAT_ID)
	P = pd.read_sql(Time_span, conn)

	# 读取 10~26 MeV 数据
	P3 = P.P3.values

	# 读取 26~40 MeV 数据
	P4 = P.P4.values

	# 读取 40~100 MeV 数据
	P5 = P.P5.values

	# 读取 100~300 MeV 数据
	P6 = P.P6.values

	# 读取时间
	TIME = P.TIME.values

	# 剔除异常值,-1e31
	ind = np.where(P3== -1e31)
	P3[ind]= np.nan
	ind = np.where(P4== -1e31)
	P4[ind]= np.nan
	ind = np.where(P5== -1e31)
	P5[ind]= np.nan
	ind = np.where(P6== -1e31)
	P6[ind]= np.nan


	# 计算每天的通量数据
	year = np.zeros(len(TIME))
	month = np.zeros(len(TIME))
	day = np.zeros(len(TIME))

	for i in range(0,len(TIME)):
		t = str(TIME[i])
		t_ymd = t.split('T')[0]
		year[i] = int(t_ymd.split('-')[0])
		month[i] = int(t_ymd.split('-')[1])
		day[i] = int(t_ymd.split('-')[2])

	P3_day = []
	P4_day = []
	P5_day = []
	P6_day = []
	TIME_day = []
	for i in range(0,len(TIME)-1):
		if (year[i]!=year[i+1])|(month[i]!=month[i+1])|(day[i]!=day[i+1]):
			year_0 = int(year[i])
			month_0 = int(month[i])
			day_0 = int(day[i])
			ind = np.where((year==year_0)&(month==month_0)&(day==day_0))
			P3_day.append(np.nanmean(P3[ind]))
			P4_day.append(np.nanmean(P4[ind]))
			P5_day.append(np.nanmean(P5[ind]))
			P6_day.append(np.nanmean(P6[ind]))
			if (month_0 < 10) & (day_0 < 10):
				time_str = str(year_0)+'-0'+str(month_0)+'-0'+str(day_0)
			elif (month_0 < 10) & (day_0 > 9):
				time_str = str(year_0)+'-0'+str(month_0)+'-'+str(day_0)
			elif (month_0 > 9) & (day_0 < 10):
				time_str = str(year_0)+'-'+str(month_0)+'-0'+str(day_0)
			else:
				time_str = str(year_0)+'-'+str(month_0)+'-'+str(day_0)
			TIME_day.append(time_str)

	year_0 = int(year[len(TIME)-1])
	month_0 = int(month[len(TIME)-1])
	day_0 = int(day[len(TIME)-1])
	ind = np.where((year==year_0)&(month==month_0)&(day==day_0))
	P3_day.append(np.nanmean(P3[ind]))
	P4_day.append(np.nanmean(P4[ind]))
	P5_day.append(np.nanmean(P5[ind]))
	P6_day.append(np.nanmean(P6[ind]))
	if (month_0 < 10) & (day_0 < 10):
		time_str = str(year_0)+'-0'+str(month_0)+'-0'+str(day_0)
	elif (month_0 < 10) & (day_0 > 9):
		time_str = str(year_0)+'-0'+str(month_0)+'-'+str(day_0)
	elif (month_0 > 9) & (day_0 < 10):
		time_str = str(year_0)+'-'+str(month_0)+'-0'+str(day_0)
	else:
		time_str = str(year_0)+'-'+str(month_0)+'-'+str(day_0)
	TIME_day.append(time_str)

	TIME_day = np.array(TIME_day)
	P3_day = np.array(P3_day)*4*math.pi
	P4_day = np.array(P4_day)*4*math.pi
	P5_day = np.array(P5_day)*4*math.pi
	P6_day = np.array(P6_day)*4*math.pi

	# 选用哪一种屏蔽材料？ 范围1~11
	material = int(sys.argv[4])
	'''	
	屏蔽材料有：1：铝（Al）,2：石墨（Graphite）,3：硅（Silicon）,4：空气（Air）,
	5：身体骨骼（Bone），6：氟化钙（CaF2）,7：砷化镓（GaAs）,8：氟化锂（LiF）,
	9：二氧化硅（SiO2）,10：身体组织（Tissue），11：水（Water）
	'''
	if (material>11)|(material<1):
		os.system('rm -rf '+current_dir+'/'+dir_name)
		sys.exit()

	# 选用哪种屏蔽方式？ 范围：1-3
	# 1：有限平板模型
	# 2：半无限平板模型
	# 3：实心球模型
	shield_type = int(sys.argv[5])
	if (shield_type>3)|(shield_type<1):
		os.system('rm -rf '+current_dir+'/'+dir_name)
		sys.exit()

	#选择屏蔽厚度？
	shield_depth = [1,2,3,4,5,6]

	#能谱点的个数？
	sptrum_point = 4

	#积分能谱能量？
	sptrum_E = [10,26,40,100]
	sptrum_E = str(sptrum_E)
	sptrum_E = sptrum_E[1:-1]
	sptrum_E = sptrum_E.replace(',',' ')

	#积分能谱通量？
	does_total = np.zeros(len(shield_depth))
	for i in range(0,len(TIME_day)):
		sptrum_fx = np.zeros(sptrum_point)
		sptrum_fx[0] = P3_day[i]+P4_day[i]+P5_day[i]+P6_day[i]
		sptrum_fx[1] = P4_day[i]+P5_day[i]+P6_day[i]
		sptrum_fx[2] = P5_day[i]+P6_day[i]
		sptrum_fx[3] = P6_day[i]

		# 剔除0值变0.1,否则报错
		ind = np.where(sptrum_fx==0)
		sptrum_fx[ind]=0.1

		sptrum_fx = str(sptrum_fx)
		sptrum_fx = sptrum_fx[1:-1]
		does, z_mm = Cal_shieldose2(material,shield_type,sptrum_point,sptrum_E,sptrum_fx) #计算辐射剂量
		does = list(map(float,does))
		z_mm = list(map(float,z_mm))
		does = np.array(does)
		z_mm = np.array(z_mm)
		f = scipy.interpolate.interp1d(z_mm,does,'linear') 
		does = f(shield_depth)  # 插值到1,2,3,4,5 mm
		does_total = np.vstack((does_total,does))
	does_total = does_total[1:len(TIME_day)+1,:]

	'''
	计算翻转截面
	参数E_pl为发生单粒子翻转所需的能量阈值,单位为MeV；
	参数Cross_pl为单粒子翻转的极限截面,单位为cm2 bit-1；
	'''
	E_pl = 8           
	Cross_pl = 1e-12    
	Cross_area = Cal_cross(sptrum_E,E_pl,Cross_pl)

	'''	
	计算粒子的翻转率
	注：所有能量通道近似用一个翻转截面
	'''
	# 需要辐射剂量与到达部件辐射通量的转换关系，这里还未添加！！以后添加！！！
	# 近似辐射剂量=辐射通量
	Cross_rate = np.zeros((len(shield_depth),len(TIME_day)))
	for day in range(0,len(TIME_day)):
		for depth in range(0,len(shield_depth)):
			Cross_rate[depth,day] = np.nanmean(Cross_area)*float(does_total[day,depth])

	# 删除文件夹
	os.system('rm -rf '+current_dir+'/'+dir_name)
	
	# 以JSON形式显示结果
	tm_js = list(map(str,TIME_day))
	cr1_js = list(map(float,Cross_rate[0,:]))
	cr2_js = list(map(float,Cross_rate[1,:]))
	cr3_js = list(map(float,Cross_rate[2,:]))
	cr4_js = list(map(float,Cross_rate[3,:]))
	cr5_js = list(map(float,Cross_rate[4,:]))
	cr6_js = list(map(float,Cross_rate[5,:]))
	dic = {}
	dic['time'] = tm_js
	dic['one'] = cr1_js
	dic['two'] = cr2_js
	dic['three'] = cr3_js
	dic['four'] = cr4_js
	dic['five'] = cr5_js
	dic['six'] = cr6_js
	print('###')
	print(json.dumps(dic))
	print('###')
	#删除文件夹
	os.system('rm -rf '+current_dir+'/'+dir_name)
except:
	print('###Fail###')




