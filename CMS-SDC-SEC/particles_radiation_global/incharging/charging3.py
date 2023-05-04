import math
import numpy as np

# 选择参数
Para = 1
# E_me参数：产生最大二次电子发射率电子的能量，单位KeV

# cigema_em参数：最大二次电子发射系数，无量纲

# E_mi参数：产生最大二次质子发射率电子的能量，单位KeV

# Y1参数：1keV质子产生的二次电子发射率，无量纲

# 参数：Z原子数，无量纲

# J_ph0参数：光电流，单位A/m2
def select_para(Para):
	# 航天器表面材料：Aluminium
	if Para == 1:
		E_me = 0.3 
		cigema_me = 0.97  
		E_mi = 230		
		Y1 = 0.244		
		Z = 13  
		J_ph0 = 4e-5
	# 航天器表面材料：Conductive white paint PSG 120 FD
	elif Para == 2:
		E_me = 0.25 
		cigema_me = 2.85  
		E_mi = 230		
		Y1 = 0.244		
		Z = 5  
		J_ph0 = 2e-5 
	# 航天器表面材料：Indium tin oxide coating
	elif Para == 3:
		E_me = 0.3 
		cigema_me = 2.5  
		E_mi = 123		
		Y1 = 0.49		
		Z = 22  
		J_ph0 = 3.2e-5
	# 航天器表面材料：KAPTON
	elif Para == 4:
		E_me = 0.2 
		cigema_me = 1.9  
		E_mi = 140		
		Y1 = 0.455		
		Z = 5  
		J_ph0 = 2e-5
	# 航天器表面材料：TEFLON
	elif Para == 5:
		E_m = 0.25 
		cigema_me = 2.4  
		E_mi = 140		
		Y1 = 0.455		
		Z = 7  
		J_ph0 = 2e-5
	# 航天器表面材料：Cerium doped glass type MARECS/ECS
	elif Para == 6:
		E_me = 0.35 
		cigema_me = 3.5  
		E_mi = 230		
		Y1 = 0.244		
		Z = 10  
		J_ph0 = 2e-5
	# 航天器表面材料：SIO2
	elif Para == 7:
		E_me = 0.4 
		cigema_me = 2.4  
		E_mi = 140		
		Y1 = 0.455		
		Z = 10  
		J_ph0 = 2e-5
	# 航天器表面材料：Oxidized aluminium
	elif Para == 8:
		E_me = 0.35 
		cigema_me = 3.2  
		E_mi = 230		
		Y1 = 0.244		
		Z = 13  
		J_ph0 = 4e-5
	return E_me,cigema_me,E_mi,Y1,Z,J_ph0




# 差分电流积分成总电流
# E: J
# J: A/m2
def Calculate_integ_J(E,J):
    J_sum =np.zeros(len(E))
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
def Calculate_D(Te,Ne):
	Te = Te*11605
	Ne = Ne*1e6
	D = 69*pow(Te/Ne,0.5)
	return D


# 表面二次电子电流的计算J_se（由电子产生）
# E_s: 电子能量，单位J
# J: 电子产生的表面电流，单位n/A2
# E_me: 产生最大二次电子发射率电子的能量
# cigema_me: 最大二次电子发射系数
def Calculate_J_se(E_s,J,E_me,cigema_me): 
    e = 1.602e-19
    E_me = E_me*e*1000 # 由keV转化成J
    # 计算不同能量E对应的二次电子发射率
    cigema = np.zeros(len(E_s))
    for i in range(len(E_s)):
        Q = 2.28*pow((E_s[i]/E_me),1.35)
        cigema[i] = (2.228*cigema_me/Q)*pow((E_me/E_s[i]),0.35)*(Q-1+math.exp(-Q))
    # 对二次反射系数进行订正
    cigema_max = max(cigema)
    cigema = cigema*(cigema_me/cigema_max)
    J_se = cigema*J
    return J_se


# 表面二次粒子电流的计算J_i(由离子产生)
# E_s: 离子能量，单位J
# J: 离子产生的表面电流，单位A/m2
# E_mi: 产生最大二次质子发射率电子的能量
# Y1: 1keV质子产生的二次电子发射率
def Calculate_J_si(E_s,J,E_mi,Y1):
    e = 1.602e-19
    E_s = E_s/1000/e # 由J转化为keV
    J_si = np.zeros(len(E_s))
    cigema = np.zeros(len(E_s))
    for i in range(len(E_s)):
        if E_s[i]>=10:
            Q = 0
        elif (E_s[i]<10) & (E_s[i]>0.476):
            Q = 1/E_s[i]-0.1
        elif E_s[i]<=0.476:
            Q = 2
        cigema[i] = (2-Q/2)*pow(2,-Q)*Y1*pow(E_s[i],0.5)/(1+E_s[i]/E_mi)
        J_si[i] = cigema[i]*J[i]
    return J_si


# 背向散射电流计算
# 背向散射电流J_be是由表面散射入射电子产生的电流,它由表面材料的背向散射产生率Y_b决定
# E_s:电子能量，单位J
# J: 电子产生的表面电流，单位A/m2
# Z: 原子数
def Calculate_J_be(E_s,J,Z):
    e = 1.602e-19
    E_s = E_s/e
    Y_b = np.zeros(len(E_s))
    J_be = np.zeros(len(E_s))
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
        J_be[i] = Y_b[i]*J[i]
    return J_be

# 光电子电流计算
# USE: 是否考虑光电子效应
# J_ph0: 光电流 A/m2
def Calculate_J_ph(USE,J_ph0):
	if USE == 'T':
		# 参数
		J_ph = J_ph0
	elif USE == 'F':
		J_ph = 0
	return J_ph



# 计算自由电流
# Ne: 电子密度，单位:cm-3
# Ni: 离子密度，单位:cm-3
# Te: 电子温度，单位:eV
# Ti: 离子温度，单位:eV
# Var: 电子(e)还是离子(i)

def Calculate_J_free(Te,Ti,Ne,Ni,Var):

	# 计算德拜半径
    D = Calculate_D(Te,Ne)
    
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
    if D<=10:
    	E = np.arange(Te/100,Te*101,0.5)
    elif D>10:
    	E = np.arange(Te/100,Te*101,200)
    E = E*e
    F = []
    
    # 计算通量
    for i in range(len(E)):
    	a = 2*N*pow(m,-0.5)*pow(1/(2*math.pi*k*T),1.5)*E[i]
    	b = math.exp(-E[i]/(k*T))
    	F.append(a*b)
    F=np.array(F) # 通量：n/cm2.sec.sr.eV

    # 计算电流
    J0 = 1e9*math.pi*e*F  
    return E, J0


# 计算表面电流
# E: 电子能量或离子能量，单位J
# J0:自由电子电流或离子电流，单位A/m2
# U: 表面电压
# Te: 电子温度，单位eV
# Ne: 电子密度，单位cm-3
# Var: 电子或离子 
def Calculate_J_surface(E,J0,U,Te,Ne,Var): # E: J J0:A/m2
	D = Calculate_D(Te,Ne)
	e = 1.602e-19
	if Var == 'e':
		if(D <=10)&(U>=0):
			JS = J0
		elif(D>10)&(U>=0):
			JS = np.zeros(len(E))
			for i in range(len(E)):
				JS[i] = J0[i]*(1+e*math.fabs(U)/E[i])	
		elif(U<0):
			JS = np.zeros(len(E))
			for i in range(len(E)):
				JS[i] = J0[i]*(1-e*math.fabs(U)/E[i])
				if JS[i]<0:
					JS[i]=0
	elif Var == 'i':
		if(D>10)&(U<=0):
			JS = np.zeros(len(E))
			for i in range(len(E)):
				JS[i] = J0[i]*(1+e*math.fabs(U)/E[i])
		elif(D <= 10)&(U<=0):
			JS = J0
		elif(U>0):
			JS = np.zeros(len(E))
			for i in range(len(E)):
				JS[i] = J0[i]*(1-e*math.fabs(U)/E[i])
				if JS[i]<0:
					JS[i]=0
	return JS



# 计算充电电位
# Te:电子温度 eV
# Ti:离子温度 eV
# Ne:电子密度 cm-3
# Ni:离子密度 cm-3
# Para: 材料参数：1~8

def Calculate_U(Te,Ti,Ne,Ni,Paras):
	# 选择参数
	E_me,cigema_me,E_mi,Y1,Z,J_ph0 = select_para(Paras)
	# 计算德拜半径
	D = Calculate_D(Te,Ne)
	
	if D >=10:
		for ir in range(0,5):
			if ir == 0:
				U = np.arange(-50000,0,5000)
			elif ir == 1:
				U = np.arange(U_approx-5000,U_approx+5500,500)
			elif ir == 2:
				U = np.arange(U_approx-500,U_approx+550,50)
			elif ir== 3:
				U = np.arange(U_approx-50,U_approx+55,5)
			elif ir== 4:
				U = np.arange(U_approx-5,U_approx+6,1)

			J_total = np.zeros(len(U))
			for i in range(len(U)):
				# 计算离子自由电流
				E, J0 = Calculate_J_free(Te,Ti,Ne,Ni,'i')
				# 计算离子表面电流
				JS_i = Calculate_J_surface(E,J0,U[i],Te,Ne,Var='i')
				JS_i_integ = Calculate_integ_J(E,JS_i)  # 积分表面离子电流
				# 计算(离子产生)二次反射电流
				JS_si = Calculate_J_si(E,JS_i,E_mi,Y1)
				JS_si_integ = Calculate_integ_J(E,JS_si)  # 积分表面二次反射(离子产生)电流

				# 计算电子自由电流
				E, J0 = Calculate_J_free(Te,Ti,Ne,Ni,'e')
				# 计算电子表面电流
				JS_e = Calculate_J_surface(E,J0,U[i],Te,Ne,Var='e')
				JS_e_integ = Calculate_integ_J(E,JS_e)  # 积分表面电子电流
				# 计算(电子产生)二次反射电流
				JS_se = Calculate_J_se(E,JS_e,E_me,cigema_me)
				JS_se_integ= Calculate_integ_J(E,JS_se)  # 积分表面二次反射(电子产生)电流
				# 计算背向散射电流
				JS_be = Calculate_J_be(E,JS_e,Z)
				JS_be_integ = Calculate_integ_J(E,JS_be)
				# 计算光电子电流
				USE = 'T'
				JS_ph_integ = Calculate_J_ph(USE,J_ph0)

				J_total[i] = math.fabs(-JS_e_integ+JS_i_integ+JS_se_integ+JS_si_integ+JS_be_integ+JS_ph_integ)	
			ind = np.where(J_total == min(J_total))
			U_approx = U[ind]
		print('###平衡电流')
		print(J_total[ind])

	
	elif D<10:
		U = np.arange(-1000,10,0.5)
		J_total = np.zeros(len(U))
		for i in range(len(U)):
			# 计算离子自由电流
			E, J0 = Calculate_J_free(Te,Ti,Ne,Ni,'i')
			# 计算离子表面电流
			JS_i = Calculate_J_surface(E,J0,U[i],Te,Ne,Var='i')
			JS_i_integ = Calculate_integ_J(E,JS_i)   # 积分表面离子电流
			# 计算(离子产生)二次反射电流
			JS_si = Calculate_J_si(E,JS_i,E_mi,Y1)
			JS_si_integ = Calculate_integ_J(E,JS_si)  # 积分表面二次反射(离子产生)电流

			# 计算电子自由电流
			E, J0 = Calculate_J_free(Te,Ti,Ne,Ni,'e')
			# 计算电子表面电流
			JS_e = Calculate_J_surface(E,J0,U[i],Te,Ne,Var='e')
			JS_e_integ = Calculate_integ_J(E,JS_e)  # 积分表面电子电流
			# 计算(电子产生)二次反射电流
			JS_se = Calculate_J_se(E,JS_e,E_me,cigema_me)
			JS_se_integ= Calculate_integ_J(E,JS_se)  # 积分表面二次反射(电子产生)电流
			# 计算背向散射电流
			JS_be = Calculate_J_be(E,JS_e,Z)
			JS_be_integ = Calculate_integ_J(E,JS_be)
			# 计算光电子电流
			USE = 'T'
			JS_ph_integ = Calculate_J_ph(USE,J_ph0)

			J_total[i] = math.fabs(-JS_e_integ+JS_i_integ+JS_se_integ+JS_si_integ+JS_be_integ+JS_ph_integ)
		ind = np.where(J_total == min(J_total))
		U_approx = U[ind]
	print('###平衡电流')
	print(J_total[ind])
	print('###表面离子电流')
	print(JS_i_integ)
	print('###表面二次(离子)反射电流')
	print(JS_si_integ)
	print('###二次电流反射(离子)比例')
	print(JS_si_integ/JS_i_integ)
	print('###表面电子电流')
	print(JS_e_integ)
	print('###表面二次(电子)反射电流')
	print(JS_se_integ)
	print('###二次电流反射(电子)比例')
	print(JS_se_integ/JS_e_integ)
	print('###背向散射电流(电子)比例')
	print(JS_be_integ/JS_e_integ)
	print('###光子电流')
	print(JS_ph_integ)

	return(U_approx)



#print(Calculate_U(1e3,1e3,1,1,1))
print(Calculate_U(0.27,0.22,1e5,9.5e4,1))