import math
import numpy as np
from matplotlib import pyplot as plt
import sys
from pathlib import Path
sys.path.append(str(Path(__file__).resolve().parents[1]))
import AP8AE8.ap8ae8flux as ae


# 差分电流积分成总电流
def Calculate_integ_J(E,J):
    J_sum =np.zeros(len(E))
    for i in range(len(E)-1):
        E1 = E[i+1]
        E0 = E[i]
        J1 = J[i+1]
        J0 = J[i]
        J_sum[i] = (E1-E0)*(J1+J0)/2
    return sum(J_sum)


# 表面二次电子电流的计算J_se（电子）
def Calculate_J_se(E_s): # E_s:表面能量，J:初级电流密度
    # 参数：产生最大二次电子发射率电子的能量：E_m=0.3
    e = 1.602e-19
    E_m = 0.3*e*1000 # keV
    # 参数：最大二次电子发射系数：cigema_m = 0.97
    cigema_m = 3.5
    # 计算不同能量E对应的二次电子发射率
    cigema = np.zeros(len(E_s))
    for i in range(len(E_s)):
        Q = 2.28*pow((E_s[i]/E_m),1.35)
        cigema[i] = (2.228*cigema_m/Q)*pow((E_m/E_s[i]),0.35)*(Q-1+math.exp(-Q))
    # 对二次反射系数进行订正
    cigema_max = max(cigema)
    cigema = cigema*(cigema_m/cigema_max)
    return cigema

# 表面二次粒子电流的计算J_i(离子)
def Calculate_J_si(E_s):
    e = 1.602e-19
    E_s = E_s/1000/e # keV
    # 参数：产生最大二次质子发射率电子的能量：E_m=230
    E_m = 230 # Kev
    # 参数：1keV质子产生的二次电子发射率:Y1=0.244
    Y1=0.244
    cigema = np.zeros(len(E_s))
    for i in range(len(E_s)):
        if E_s[i]>=10:
            Q = 0
        elif (E_s[i]<10) & (E_s[i]>0.476):
            Q = 1/E_s[i]-0.1
        elif E_s[i]<=0.476:
            Q = 2
        cigema[i] = (2-Q/2)*pow(2,-Q)*Y1*pow(E_s[i],0.5)/(1+E_s[i]/E_m)
    return cigema


# 背向散射电流计算
# 背向散射电流J_be是由表面散射入射电子产生的电流,它由表面材料的背向散射产生率Y_b决定
def Calculate_J_be(E_s):
    # 参数Z:原子数
    Z = 13
    Y_b = np.zeros(len(E_s))
    for i in range(len(E_s)):
        if E_s[i]>100100:
            Y_b[i] = 0
        elif (E_s[i]<=100100) & (E_s[i]>10000):
            Y_b[i] = 1-pow(0.7358,0.037*Z)
        elif(E_s[i]<=10000) & (E_s[i]>1000):
            Y_b[i] = 1-pow(0.7358,0.037*Z)+0.1*math.exp(-E_s[i]/5000)
        elif(E_s[i]<=1000) & (E_s[i]>50):
            Y_b[i] = 0.3338*math.log(E_s[i]/50,math.e)*(1-pow(0.7358,0.037*Z)+0.1*math.exp(-E_s[i]/50))
        else:
            Y_b[i] = 0
        if Y_b[i]!=0:
            Y_b[i] = 2*(1-Y_b[i]+Y_b[i]*math.log(Y_b[i],math.e))/(pow(math.log(Y_b[i],math.e),2))
        else:
            Y_b[i] = 0
    return Y_b


# 光电子电流计算J_ph
# 入射阳光会引起表面材料发射电子，引起光电子电流。
def Calculate_J_ph(V,S):
    J_ph = 4*e-5 # 单位（A/m2）
    # 需要乘以接触面积得到总的光子电流
    J_ph = J_ph*S
    k = Calculate_J_k(1,V)
    J_ph = J_ph*k
    return J_ph

# 表面电子流
def Calculate_Je_sur(V,J,E):
    e = 1.602e-19
    if V>=0:
        Je_sur = J
    elif V<0:
        Je_sur = J*(1-(-V)/E)
    return Je_sur


# 计算通量
#Ne: cm-3
#Te: eV
#U: i / e
def Calculate_factor(Te,Ne,Var):
    
    # 离子 i 还是电子 e ？
    if Var == 'i':
        # 离子质量
        m = 2.657e-26*1000
    elif Var == 'e':
        # 电子质量
        m = 9.1066e-31*1000
    # 玻尔兹曼常数
    k = 1.38e-23
    # 电子电量
    e = 1.602e-19
    # 粒子积分上下限
    E = np.arange(Te/100,Te*101,0.5)
    E = E*e
    F = []
    Te = Te*11605
    
    # 计算通量
    for i in range(len(E)):
        print(Te)
        a = 2*Ne*pow(m,-0.5)*pow(1/(2*math.pi*k*Te),1.5)*E[i]
        b = math.exp(-E[i]/(k*Te))  
        F.append(a*b)
    F=np.array(F)

    # 计算电流
    J0 = 1e9*math.pi*e*F  
      
    return E,J0

E,J = Calculate_factor(0.3,1e4,'e')
print(Calculate_integ_J(E,J))

###############################################
# 等离子体环境下孤立导体表面充电时域特性研究
# 在计算中，以高速飞行得航天器作为静止参考系，航天器
# 简化为半径为R的导体球模型。航天器表面电位是由入射到
# 表面的电流和从表面发射的电流平衡所决定。
def Calculate_Charging(t_e,n_e,t_i,n_i,R,t,Var):
    #一些参数
    # 电子的温度
    #t_e = 0.5*11605 # k
    # 电子的数密度 
    #n_e = 8e10 # n/m3
    # 离子的温度
    #t_i = 0.5*11605 # k
    # 离子的数密度
    #n_i = 8e10 # n/m3
    # 离子的重量 
    m_i = 2.657e-26 # kg
    # 电子的重量
    m_e = 9.1066e-31 # kg
    # 介电常数
    cigema = 8.854e-12 # F/m
    # 电子电量
    q = 1.602e-19 # coulombs
    # 玻尔兹曼常数
    k = 1.380649e-23 # J/K

    factor_i = Calculate_factor(t_i/11605,n_i/1e6,'i')
    factor_e = Calculate_factor(t_i/11605,n_i/1e6,'e')
    
    #print(factor_i)
    #print(factor_e)

    # a
    a = - k*t_e/q
    # b
    b = 1- (n_e*(1-factor_e))/(n_i*(1+factor_i))*pow(m_i/m_e,0.5)
    # c
    c = (-q*n_i*R*(1+factor_i))/(k*t_e*cigema)*pow(k*t_i/(2*math.pi*m_i*1000),0.5)
    # d
    d = (n_e*(1-factor_e))/(n_i*(1+factor_i))*pow(m_i/m_e,0.5)
    print('###')
    print(a)
    print(b)
    print(c)
    print(d)
    print(b*math.exp(c*t)+d)
    # Je 电子电流密度
    #Je = q*n_e*pow(k*t_e/(2*math.pi*m_e),0.5)*math.exp(q*U/(k*t_e))
    # Ji 离子电流密度
    #Ji = q*n_i*pow(k*t_i/(2*math.pi*m_i),0.5)
    # U(V)
    U = a*math.log(b*math.exp(c*t)+d,math.e)
    # Q
    Q = 4*math.pi*cigema*R*a*math.log(b*math.exp(c*t)+d,math.e)
    # E
    E = Q*Q/(8*math.pi*cigema*R)
    # which calculated variable?
    CV = Var
    if Var == 'U':
        return U
    elif Var == 'Q':
        return Q
    elif Var == 'E':
        return E
    elif Var == 'Je':
        return Je
    elif Var == 'Ji':
        return Ji

#print(Calculate_Charging(t_e = 0.5*11605 ,n_e = 8e10, t_i = 0.5*11605 ,n_i = 8e10, R= 0.1,t = 1, Var ='U'))
#print(Calculate_Charging(t_e = 1e7 ,n_e = 1e7, t_i = 1e7 ,n_i = 1e7, R= 0.1,t = 1, Var ='U'))