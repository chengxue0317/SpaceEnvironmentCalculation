import os
import sys
import numpy as np
import math
import json


# 保留有效数字
def valid_numbers(x):
    x = float(np.format_float_positional(x, precision=3, unique=False, fractional=False, trim='k'))
    return x


def Cal_Weibull(L, sigma0, L_th, W, S):
    sigma = np.zeros(len(L))
    for i in range(len(L)):
        if L[i]-L_th > 0:
            b = -1*pow((L[i]-L_th)/W, S)
            sigma[i] = sigma0*(1 - math.exp(b))
        else:
            sigma[i] = 0
    return sigma


def Read_let_flux(scenario, shield_depth):
    current_dir = os.path.dirname(os.path.abspath(__file__))
    f = open(current_dir+'/data_heavy_ion/'+scenario+'_'+shield_depth+'mm_let.let.tsv')
    size = len(f.readlines())
    f = open(current_dir+'/data_heavy_ion/'+scenario+'_'+shield_depth+'mm_let.let.tsv')
    # print(current_dir+'/data_heavy_ion/'+scenario+'_'+shield_depth+'mm_let.let.tsv')
    LET = []
    flux = []
    for i in range(size):
        txt = f.readline().split()
        if i > 0:
            LET.append(txt[0])
            flux.append(txt[1])
    LET = list(map(float, LET))
    flux = list(map(float, flux))
    LET = np.array(LET)
    flux = np.array(flux)*24*3600/1e4*4*3.14159
    return LET/1000, flux  # let: MeV cm2/mg; flux: m2 s-sr


def Cal_effective_flux(LET, flux):
    # Analytic SEU rate calculation compared to space data, Publisher: IEEE
    flux_e = np.zeros(len(LET))
    for i in range(len(LET)):
        if LET[i] < L_th:
            flux_e[i] = 0.5*flux[i]*pow(LET[i]/L_th, 2)
        else:
            flux_e[i] = 0.5*flux[i]
    return flux_e


# 参数
para_switch = int(sys.argv[1])
if para_switch == 1:
    Weibull_para_name = str(sys.argv[2])

total_SEU = []
dic = {}
for scenario in range(1, 6):
    if scenario == 1:
        scenario = 'solar_min'
    elif scenario == 2:
        scenario = 'solar_max'
    elif scenario == 3:
        scenario = 'worst_week'
    elif scenario == 4:
        scenario = 'worst_day'
    elif scenario == 5:
        scenario = 'peak_5min'
    
    depth = ['1', '3', '5']
    for shield_depth in depth:
        if para_switch == 1:
            current_dir = os.path.dirname(os.path.abspath(__file__))
            para_name = []
            para_sigma0 = []
            para_Lth = []
            para_W = []
            para_S = []
            f = open(current_dir+'/Weibull_parameter.txt')
            size = len(f.readlines())
            f = open(current_dir+'/Weibull_parameter.txt')
            for i in range(size):
                txt = f.readline().split()
                if i > 0:
                    para_name.append(txt[0])
                    para_sigma0.append(txt[1])
                    para_Lth.append(txt[2])
                    para_W.append(txt[3])
                    para_S.append(txt[4])

            sigma0 = float(para_sigma0[para_name.index(Weibull_para_name)])
            L_th = float(para_Lth[para_name.index(Weibull_para_name)])
            W = float(para_W[para_name.index(Weibull_para_name)])
            S = float(para_S[para_name.index(Weibull_para_name)])
        elif para_switch == 2:
            sigma0 = float(sys.argv[2])
            L_th = float(sys.argv[3])
            W = float(sys.argv[4])
            S = float(sys.argv[5])

        # Calculate SEU_ion
        LET, flux = Read_let_flux(scenario, shield_depth)
        sigma = Cal_Weibull(LET, sigma0, L_th, W, S)
        flux_e = Cal_effective_flux(LET, flux)
        SEU = 0
        for i in range(len(flux_e)):
            SEU = SEU+sigma[i]*flux_e[i]
        SEU = valid_numbers(SEU)
        SEU = format('{:.2e}'.format(SEU, 'e'))
        total_SEU.append(SEU)

dic = {}
if para_switch == 1:
    dic['material name'] = Weibull_para_name
elif para_switch == 2:
    dic['material name'] = 'user-defined material'
dic['sigma0'] = sigma0
dic['LET threshold'] = L_th
dic['Weibull para W'] = W
dic['Weibull para S'] = S 

dic['solar min 1mm depth'] = total_SEU[0]
dic['solar min 3mm depth'] = total_SEU[1]
dic['solar min 5mm depth'] = total_SEU[2]
dic['solar max 1mm depth'] = total_SEU[3]
dic['solar max 3mm depth'] = total_SEU[4]
dic['solar max 5mm depth'] = total_SEU[5]
dic['worst week 1mm depth'] = total_SEU[6]
dic['worst week 3mm depth'] = total_SEU[7]
dic['worst day 5mm depth'] = total_SEU[8]
dic['worst day 1mm depth'] = total_SEU[9]
dic['worst day 3mm depth'] = total_SEU[10]
dic['worst day 5mm depth'] = total_SEU[11]
dic['solar peak 1mm depth'] = total_SEU[12]
dic['solar peak 3mm depth'] = total_SEU[13]
dic['solar peak 5mm depth'] = total_SEU[14]
print('######')
print(json.dumps(dic, ensure_ascii=False))
print('######')
