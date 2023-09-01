import math
import numpy as np


# E_me参数：产生最大二次电子发射率电子的能量，单位KeV

# cigema_em参数：最大二次电子发射系数，无量纲

# E_mi参数：产生最大二次质子发射率电子的能量，单位KeV

# Y1参数：1keV质子产生的二次电子发射率，无量纲

# 参数：Z原子数，无量纲

# J_ph0参数：光电流，单位A/m2


# 差分电流积分成总电流
# E: J
# J: A/m2
def Calculate_integ_J(E, J):
    J_sum = np.zeros(len(E))
    for i in range(len(E)-1):
        E1 = E[i+1]
        E0 = E[i]
        J1 = J[i+1]
        J0 = J[i]
        J_sum[i] = (E1-E0)*(J1+J0)/2
    return sum(J_sum)


# 计算德拜长度
# Te:电子温度，单位 eV
# Ne:电子密度，单位 cm-3
def Calculate_D(Te, Ne):
    Te = Te*11605
    Ne = Ne*1e6
    D = 69*pow(Te/Ne, 0.5)
    return D


# 表面二次电子电流的计算J_se（由电子产生）
# E_s: 电子能量，单位J
# J: 电子产生的表面电流，单位n/A2
# E_me: 产生最大二次电子发射率电子的能量
# cigema_me: 最大二次电子发射系数
def Calculate_J_se2(E_s, J, E_me, cigema_me): 
    e = 1.602e-19
    E_s = (E_s/e)/1000  # 由J转化为KeV
    # 计算不同能量E对应的二次电子发射率
    cigema = np.zeros(len(E_s))
    for i in range(len(E_s)):
        Q = 2.28*pow((E_s[i]/E_me), 1.35)
        cigema[i] = (2.228*cigema_me/Q)*pow((E_me/E_s[i]), 0.35)*(Q-1+math.exp(-Q))
    # 对二次反射系数进行订正
    cigema_max = max(cigema)
    cigema = cigema*(cigema_me/cigema_max)
    # J_se = cigema*J
    return cigema


# 材料次级电子发射特性对表面充电影响的数值计算研究
def Calculate_J_se(E_s, J, E_me, cigema_me):
    e = 1.602e-19
    E_s = (E_s/e)/1000
    cigema = np.zeros(len(E_s))
    # J_se = np.zeros(len(E_s))
    for i in range(len(E_s)):
        cigema[i] = 7.4*cigema_me*(E_s[i]/E_me)*math.exp(-2*pow(E_s[i]/E_me,0.5))
    return cigema


# 表面二次粒子电流的计算J_i(由离子产生)
# E_s: 离子能量，单位J
# J: 离子产生的表面电流，单位A/m2
# E_mi: 产生最大二次质子发射率电子的能量
# Y1: 1keV质子产生的二次电子发射率
def Calculate_J_si(E_s, J, E_mi, Y1):
    e = 1.602e-19
    E_s = E_s/1000/e  # 由J转化为keV
    J_si = np.zeros(len(E_s))
    cigema = np.zeros(len(E_s))
    for i in range(len(E_s)):
        if E_s[i] >= 10:
            Q = 0
        elif (E_s[i] < 10) & (E_s[i] > 0.476):
            Q = 1/E_s[i]-0.1
        elif E_s[i] <= 0.476:
            Q = 2
        cigema[i] = (2-Q/2)*pow(2, -Q)*Y1*pow(E_s[i], 0.5)/(1+E_s[i]/E_mi)
        J_si[i] = cigema[i]*J[i]
    return cigema


# 背向散射电流计算
# 背向散射电流J_be是由表面散射入射电子产生的电流,它由表面材料的背向散射产生率Y_b决定
# E_s:电子能量，单位J
# J: 电子产生的表面电流，单位A/m2
# Z: 原子数
def Calculate_J_be(E_s, J, Z):
    e = 1.602e-19
    E_s = E_s/e
    Y_b = np.zeros(len(E_s))
    J_be = np.zeros(len(E_s))
    for i in range(len(E_s)):
        if E_s[i] > 100100:
            Y_b[i] = 0
        elif (E_s[i] <= 100100) & (E_s[i] > 10000):
            Y_b[i] = 1-pow(0.7358, 0.037*Z)
        elif (E_s[i] <= 10000) & (E_s[i] > 1000):
            Y_b[i] = 1-pow(0.7358, 0.037*Z)+0.1*math.exp(-E_s[i]/5000)
        elif (E_s[i] <= 1000) & (E_s[i] > 50):
            Y_b[i] = 0.3338*math.log(E_s[i]/50, math.e)*(1-pow(0.7358, 0.037*Z)+0.1*math.exp(-E_s[i]/50))
        else:
            Y_b[i] = 0
        if Y_b[i] != 0:
            Y_b[i] = 2*(1-Y_b[i]+Y_b[i]*math.log(Y_b[i], math.e))/(pow(math.log(Y_b[i], math.e), 2))
        else:
            Y_b[i] = 0
    return Y_b


def Calculate_J_ph(USE,J_ph0):
    # 光电子电流计算
    # USE: 是否考虑光电子效应
    # J_ph0: 光电流 A/m2
    if USE == 'T':
        # 参数
        J_ph = J_ph0
    elif USE == 'F':
        J_ph = 0
    return J_ph


def Calculate_p(E, J, U, E_me, cigema_me, Z):
    e = 1.602e-19
    #############
    # 计算反射系数
    # E_s = np.arange(E,)
    # f = Calculate_J_se(E_s*e,J,E_me,cigema_me)
    # factor_se = f[int(E/6)]
    E_s = E/1000/5
    factor_se = 7.4*cigema_me*(E_s/E_me)*math.exp(-2*pow(E_s/E_me, 0.5))
    # 计算沉降电子的背向散射系数
    # f = Calculate_J_be(E_s*e,J,Z)
    # factor_be = f[-1]
    factor_be = 1-pow(0.7358, 0.037*Z)+0.1*math.exp(-E_s/5000)
    factor = factor_be+factor_se
    # print(factor)
    # 计算表面沉降电子
    JS_p = J*(1-math.fabs(-U)/E)  # 表面沉降电子
    if JS_p < 0:
        JS_p = 0
    # 计算反射沉降电子
    if (factor_se+factor_be) <= 1:
        JSS_p = JS_p*(factor_se+factor_be)
    else:
        JSS_p = JS_p
    return JS_p, JSS_p, factor

# 计算自由电流
# Ne: 电子密度，单位:cm-3
# Ni: 离子密度，单位:cm-3
# Te: 电子温度，单位:eV
# Ti: 离子温度，单位:eV
# Var: 电子(e)还是离子(i)


def Calculate_J_free(Te, Ti, Ne, Ni, Var):
    # 离子 i 还是电子 e ？
    if Var == 'i':
        # 离子质量
        m = 2.657e-26*1000
        N = Ni
        T = Ti*11605
    elif Var == 'e':
        # 电子质量
        m = 9.1066e-31*1000
        N = Ne
        T = Ti*11605

    # 玻尔兹曼常数
    k = 1.38e-23
    # 电子电量
    e = 1.602e-19
    # 粒子积分上下限
    if Te <= 20:
        E = np.arange(Te/40, Te*15, 0.05)
    elif Te > 20:
        E = np.arange(1, Te*20, 100)
    E = E*e
    F = []
    
    # 计算通量
    a = 2*N*pow(m, -0.5)*pow(1/(2*math.pi*k*T), 1.5)*E
    b = -E/(k*T)
    
    def num_func(x):
        return math.exp(x)

    F.append(list(map(num_func, b))*a)
    F = F[0]
    F = np.array(F)  # 通量：n/cm2.sec.sr.eV

    J0 = 1e9*math.pi*e*F
    return E, J0


def Calculate_J_free2(Te, Ti, Ne, Ni, Var):
    # 离子 i 还是电子 e ？
    if Var == 'i':
        # 离子质量
        m = 2.657e-26*1000
        N = Ni*1e6
        T = Ti*11605
    elif Var == 'e':
        # 电子质量
        m = 9.1066e-31*1000
        N = Ne*1e6
        T = Te*11605
    # 玻尔兹曼常数
    k = 1.38e-23
    # 电子电量
    e = 1.602e-19
    # 粒子积分上下限
    if Te <= 20:
        E = np.arange(Te/40, Te*15, 0.05)
    elif Te > 20:
        E = np.arange(1, Te*20, 1) 
    E = E*e
    J = np.zeros(len(E))
    for i in range(len(E)):
        J[i] = N*2/(pow(math.pi, 0.5)*pow(k*T, 1.5))*pow(E[i], 0.5)*math.exp(-E[i]/(k*T))*e*0.5*pow(2*k*T/(math.pi*m), 0.5)
        # J[i] = math.exp(-E[i]/(k*T))
    return E, J


# 计算表面电流
# E: 电子能量或离子能量，单位J
# J0:自由电子电流或离子电流，单位A/m2
# U: 表面电压
# Te: 电子温度，单位eV
# Ne: 电子密度，单位cm-3
# Var: 电子或离子 
def Calculate_J_surface(E, J0, U, Te, Ne, Var):  # E: J J0:A/m2

    D = Calculate_D(Te, Ne)
    e = 1.602e-19
    if Var == 'e':
        if (D <= 10) & (U >= 0):
            JS = J0
        elif (D > 10) & (U >= 0):
            JS = np.zeros(len(E))
            for i in range(len(E)):
                JS[i] = J0[i]*(1+e*math.fabs(U)/E[i])   
        elif (U < 0):
            # print(E/e)  ##################################
            JS = np.zeros(len(E))
            for i in range(len(E)):
                JS[i] = J0[i]*(1-e*math.fabs(U)/E[i])
                if JS[i] < 0:
                    JS[i] = 0
    elif Var == 'i':
        if (D > 10) & (U <= 0):
            JS = np.zeros(len(E))
            for i in range(len(E)):
                JS[i] = J0[i]*(1+e*math.fabs(U)/E[i])
        elif (D <= 10) & (U <= 0):
            JS = J0
        elif (U > 0):
            JS = np.zeros(len(E))
            for i in range(len(E)):
                JS[i] = J0[i]*(1-e*math.fabs(U)/E[i])
            #   if JS[i]<0:
            #       JS[i]=0
    return JS


# 计算充电电位
# Te:电子温度 eV
# Ti:离子温度 eV
# Ne:电子密度 cm-3
# Ni:离子密度 cm-3
# Para: 材料参数：1~8


Auroral_Precipitated_Particles = 'T'


def Calculate_Utest(U, Te, Ti, Ne, Ni, Paras, P):
    # 选择参数
    E_me = Paras[0]
    cigema_me = Paras[1]
    E_mi = Paras[2]
    Y1 = Paras[3]
    Z = Paras[4]
    J_ph0 = Paras[5]
    # 计算德拜半径
    # ####离子部分电流#####
    # 计算离子自由电流
    E, J0 = Calculate_J_free(Te, Ti, Ne, Ni, 'i')
    # 计算离子表面电流
    JS_i = Calculate_J_surface(E, J0, U, Te, Ne, Var='i')
    JS_i_integ = Calculate_integ_J(E, JS_i)   # 积分表面离子电流
    # print(JS_i_integ)
    # 计算(离子产生)二次反射电流
    cigema = Calculate_J_si(E, JS_i, E_mi, Y1)
    ind = np.where(cigema > 1)
    cigema[ind] = 1
    JS_si = JS_i*cigema
    JS_si_integ = Calculate_integ_J(E, JS_si)  # 积分表面二次反射(离子产生)电流

    # ####电子部分电流#####
    # 计算电子自由电流
    E, J0 = Calculate_J_free(Te, Ti, Ne, Ni, 'e')

    # 计算电子表面电流
    JS_e = Calculate_J_surface(E, J0, U, Te, Ne, Var='e')
    JS_e_integ = Calculate_integ_J(E, JS_e)  # 积分表面电子电流
    # 计算(电子产生)二次反射电流和背向散射电流
    cigema = Calculate_J_se(E, JS_e, E_me, cigema_me)+Calculate_J_be(E, JS_e, Z)
    ind = np.where(cigema > 1)
    cigema[ind] = 1
    JS_se_be = JS_e*cigema
    JS_se_be_integ = Calculate_integ_J(E, JS_se_be)  # 积分表面二次反射背向(电子产生)电流

    # 计算沉降表面电流
    if P == 'F':
        JS_p = 0
        JSS_p = 0
    else:
        JS_p, JSS_p, factor = Calculate_p(5e3, 0.005, U, E_me, cigema_me, Z)  # 可以设定参数，等离子体沉降能量和电流密度

    # 计算光电子电流
    USE = 'F'
    JS_ph_integ = Calculate_J_ph(USE, J_ph0)
    
    J_total = (-JS_e_integ-JS_p+JSS_p+JS_i_integ+JS_se_be_integ+JS_si_integ+JS_ph_integ)
    
    return J_total


def Cal_J_with_diff_U(U, Te, Ti, Ne, Ni, Paras, P):
    J = np.zeros(len(U))
    for i in range(len(U)):
        J[i] = Calculate_Utest(U[i], Te, Ti, Ne, Ni, Paras, P)
    return J


def Calculate_balance_U(Te, Ti, Ne, Ni, Paras, P, Lat):
    U_start = -24000
    U_end = 0
    U = [U_start, U_end]
    if (P == 'T'):
        threshold = 5
    else:
        threshold = 0.01
    while (U_end-U_start > threshold):
        J = Cal_J_with_diff_U(U, Te, Ti, Ne, Ni, Paras, P)
        if J[0]*J[1] < 0:
            U_middle = (U_start+U_end)/2
            U = [U_start, U_middle]
            J = Cal_J_with_diff_U(U, Te, Ti, Ne, Ni, Paras, P)
            if J[0]*J[1] > 0:
                U_start = U_middle
                U = [U_start, U_end]
            else:
                U_end = U_middle
                U = [U_start, U_end]
        # print(U)
    return U[0]
