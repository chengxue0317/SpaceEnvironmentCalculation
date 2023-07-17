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
import matplotlib.pyplot as plt


'''
单粒子效应：以单粒子翻转为主。半导体期间的单粒子翻转率是指器件每天
每bit发生单粒子翻转活错误的概率。计算空间质子产生的单粒子翻转率，
需知道空间质子能谱，以及器件对不同能量质子的单粒子翻转截面，两者
积分即可得到质子单粒子翻转率的预测值。常采用Bendel单参/双参数法来计算单
粒子翻转截面。

对于低轨卫星，单粒子效应主要是由地球捕获带上的质子与半导体材料的和反应引起的。
地球同步轨道或行星际轨道不经过地球捕获带所在的区域，这些轨道上的单粒子效应主要
是由银河宇宙线和太阳宇宙线中的重离子直接电离引起产生。
'''


# 计算极限翻转截面：Bendel 双参模型
def Cal_limiting_cross_section_Bendel2(E,A,B):
	Cross = np.zeros(len(E))
	for i in range(0,len(E)):
		if (E[i]-A)>0:
			Y = math.sqrt(18/A)*(E[i]-A)
			Y = -0.18*math.sqrt(Y)
			Y = pow(1-math.exp(Y),4)
			Y = pow(B/A,14)*Y*1e-12
			Cross[i] = Y
		else:
			Cross[i] = 0
	return Cross


# 计算极限翻转截面：Bendel 单参模型
def Cal_limiting_cross_section_Bendel1(E,A):
	Cross = np.zeros(len(E))
	for i in range(0,len(E)):
		if (E[i]-A)>0:
			Y = math.sqrt(18/A)*(E[i]-A)
			Y = -0.18*math.sqrt(Y)
			Y = pow(1-math.exp(Y),4)
			Y = pow(24/A,14)*Y*1e-12
			Cross[i] = Y
		else:
			Cross[i] = 0
	return Cross


# 根据极限翻转截面计算单粒子翻转率
def Cal_SEU_proton(Cross,flux):
	s = 0
	for i in range(len(flux)):
		s = s+Cross[i]*flux[i]
	return s


# 读取空间环境辐射数据--低轨卫星质子通量
def Cal_radiation_flux(shield_depth,solar,inc,height):
	current_dir = os.path.dirname(os.path.abspath(__file__))
	file_path = current_dir + '/data_proton/'
	file_name = 'trans'+str(shield_depth)+'mm_'+str(solar)+'_inc_'+str(inc)+'h_'+str(height)+'.tfx.tsv'
	f = open(file_path+file_name)
	size = len(f.readlines())
	f = open(file_path+file_name)
	E = np.zeros(size)
	F = np.zeros(size)
	for i in range(size):
		txt = f.readline().split()
		if i>1:
			E[i] = float(txt[0])
			F[i] = float(txt[1])*3600*24/10000*4*3.14
	return E,F

# 保留有效数字
def valid_numbers(x):
	x = float(np.format_float_positional(x, precision=3, unique = False, fractional = False, trim = 'k'))
	return x

def flux_target_height_inc(shield_depth,solar,i_low,h_low,i_high,h_high):
	energy_1,flux_1 = Cal_radiation_flux(shield_depth,solar,i_low,h_low)
	energy_2,flux_2 = Cal_radiation_flux(shield_depth,solar,i_high,h_low)
	flux_h_low = np.zeros(len(flux_1))
	for i in range(len(flux_1)):
		flux_h_low[i] = flux_1[i]+(flux_2[i]-flux_1[i])/2*(i_high-incl)
		energy_3,flux_3 = Cal_radiation_flux(shield_depth,solar,i_low,h_high)
		energy_4,flux_4 = Cal_radiation_flux(shield_depth,solar,i_high,h_high)
		flux_h_high = np.zeros(len(flux_3))
		for i in range(len(flux_3)):
			flux_h_high[i] = flux_3[i]+(flux_4[i]-flux_3[i])/2*(i_high-incl)
		flux = np.zeros(len(flux_1))
		for i in range(len(flux)):
			flux[i] = flux_h_low[i]+(flux_h_high[i]-flux_h_low[i])/50*(h_high-geo_height)
	return flux,energy_1


# 参数
incl = float(str(sys.argv[1]))   # 倾角
i_low = int(incl/2)*2
i_high = i_low+2

perigee = float(str(sys.argv[2])) # 近地点
apogee = float(str(sys.argv[3])) # 远地点
geo_height = (perigee+apogee)/2 # 近地点远地点平均
h_low = int(geo_height/50)*50
h_high = h_low+50

Bendel = int(str(sys.argv[4])) # Bendel参数化方案，1：Bendel单参 2：Bendel双参

Bendel_para_name = str(sys.argv[5])

#print('i_low',i_low)
#print('i_high',i_high)
#print('h_low',h_low)
#print('h_high',h_high)




#plt.plot(E,flux/(3600*24/10000*4*3.14))
#plt.xscale('log')
#plt.yscale('log')
#plt.xlim((1e-1,1e3))
#plt.ylim((1e1,1e5))
#plt.xlabel('Kinetic Energy(MeV/nucleon)')
#plt.ylabel('Flux(m2-s-sr-MeV/nuc)-1')
#plt.savefig('test')

depth = ['1','3','5']
scenario = ['ap8max','ap8max']
for shield_depth in depth: 
	for solar in scenario:
		para_name = []
		para_A = []
		flux,E = flux_target_height_inc(shield_depth,solar,i_low,h_low,i_high,h_high)
		current_dir = os.path.dirname(os.path.abspath(__file__))
		if Bendel == 1:	
			f = open(current_dir+'/Bendel1_parameter.txt')
			size = len(f.readlines())
			f = open(current_dir+'/Bendel1_parameter.txt')
			for i in range(size):
				txt = f.readline().split()
				if i>0:
					para_name.append(txt[0])
					para_A.append(txt[1])
			A = float(para_A[para_name.index(Bendel_para_name)])
			Cross = Cal_limiting_cross_section_Bendel1(E,A)
			SEU = Cal_SEU_proton(Cross,flux)

		elif Bendel == 2:
			para_B = []
			f = open(current_dir+'/Bendel2_parameter.txt')
			size = len(f.readlines())
			f = open(current_dir+'/Bendel2_parameter.txt')
			for i in range(size):
				txt = f.readline().split()
				if i>0:
					para_name.append(txt[0])
					para_A.append(txt[1])
					para_B.append(txt[2])
			A = float(para_A[para_name.index(Bendel_para_name)])
			B = float(para_B[para_name.index(Bendel_para_name)])
			Cross = Cal_limiting_cross_section_Bendel2(E,A,B)
			SEU = Cal_SEU_proton(Cross,flux)
		SEU = valid_numbers(SEU)
		SEU = format('{:.2e}'.format(SEU,'e'))
		print('#### SEU by protron,',solar,',',shield_depth,'mm shield depth:',SEU,'bit/day ####' )
print('### parameter ####')
if Bendel == 1:
	print('name: Bendel1_para')
	print('A: ',A)
else:
	print('name: Bendel2_para')
	print('A: ',A)
	print('B: ',B)
print('#################')





