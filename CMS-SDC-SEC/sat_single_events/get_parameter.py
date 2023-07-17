import sys
import os
Bendel = int(str(sys.argv[1])) # Bendel参数化方案，1：Bendel单参 2：Bendel双参 3: Weibull
para_name = []
para_A = []
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

elif Bendel == 3:
	para_sigma0 = []
	para_Lth = []
	para_W = []
	para_S = []
	f = open(current_dir+'/Weibull_parameter.txt')
	size = len(f.readlines())
	f = open(current_dir+'/Weibull_parameter.txt')
	for i in range(size):
		txt = f.readline().split()
		if i >0:
			para_name.append(txt[0])
			para_sigma0.append(txt[1])
			para_Lth.append(txt[2])
			para_W.append(txt[3])
			para_S.append(txt[4])

# 以JSON形式显示结果
dic = {}
dic['parameter_name'] = para_name
print(dic)

